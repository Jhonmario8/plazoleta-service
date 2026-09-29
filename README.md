# Plazoleta Service

Microservicio central del proyecto Reto Pragma (plazoleta de comidas). Gestiona restaurantes, categorías, platos y el ciclo de vida de los pedidos. Consulta y crea usuarios en `user-service`, envía notificaciones a través de `msg-service` y registra cada cambio de estado en `traceability-service`. Toda la comunicación con esos servicios es HTTP síncrona mediante OpenFeign.

Forma parte del repositorio [Reto-Pragma](https://github.com/Jhonmario8/Reto-Pragma).

## Tabla de contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Endpoints](#endpoints)
- [Reglas de negocio](#reglas-de-negocio)
- [Configuración](#configuración)
- [Ejecución en local](#ejecución-en-local)
- [Tests](#tests)
- [Deuda técnica conocida](#deuda-técnica-conocida)

## Tecnologías

- Java 17, Spring Boot 3.3.5 (Web, Data JPA, Security, Validation)
- Spring Cloud OpenFeign 2023.0.3
- MySQL 8 (Hibernate, `ddl-auto: update`)
- JWT con `io.jsonwebtoken` 0.11.5 (solo valida tokens; los emite `user-service`)
- MapStruct 1.5.5 y Lombok
- Gradle 9.4.1 (wrapper incluido)
- JUnit 5, Mockito y AssertJ (vía `spring-boot-starter-test`)

## Arquitectura

Arquitectura hexagonal en tres capas bajo `src/main/java/com/pragma/plazoletaservice/`:

```
domain/
  api/         Puertos de entrada (IOrderServicePort, IDishServicePort, ...) y puertos
               hacia otros servicios (IUserServicePort, ISmsServicePort, ITraceabilityServicePort)
  spi/         Puertos de persistencia (IOrderPersistencePort, IDishPersistencePort, ...)
  model/       Modelos de dominio con validaciones en constructor (Restaurant, Dish, Order)
  exception/   DomainException, NotFoundException, ConflictException, UnauthorizedException
application/
  usecase/     Implementación de la lógica de negocio (OrderUseCase, DishUseCase, ...)
  handler/     Orquestan DTO <-> dominio para los controladores
  dto/, mapper/
infrastructure/
  input/controller/     Controladores REST
  output/jpa/           Adaptadores JPA, entidades y repositorios
  output/feign/         Clientes Feign hacia user-service, msg-service y traceability-service
  output/security/      Filtro JWT y adaptador de autenticación
  configuration/        SecurityConfig, FeignConfig (reenvía el header Authorization)
```

## Endpoints

Todos los endpoints esperan `Authorization: Bearer <token>` emitido por `user-service`. La columna "Rol" refleja las reglas de `SecurityConfig`; algunos casos de uso agregan validaciones propias (ver [Reglas de negocio](#reglas-de-negocio)).

### Restaurantes

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/restaurants` | ADMIN | Crea un restaurante. El `ownerId` debe corresponder a un usuario con rol OWNER. |
| GET | `/restaurants?page=0&size=10` | Autenticado | Lista restaurantes paginados (nombre y logo). |

### Empleados

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/users/employee` | OWNER | Crea un empleado en `user-service` asociado al restaurante del propietario autenticado. |

### Categorías y platos

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/categories?categoryName=...` | OWNER | Crea una categoría. |
| POST | `/dishes` | OWNER | Crea un plato en un restaurante del propietario. |
| PUT | `/dishes` | OWNER | Actualiza un plato existente. |
| GET | `/restaurants/{restaurantId}/dishes?categoryId=&page=0&size=10` | Sin restricción | Lista platos del restaurante, filtrando opcionalmente por categoría. |

### Pedidos

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| POST | `/orders` | CLIENT | Crea un pedido en estado PENDING a nombre del cliente autenticado. |
| GET | `/orders/{restaurantId}?status=&page=0&size=10` | Sin restricción en SecurityConfig; el caso de uso exige EMPLOYEE | Lista pedidos del restaurante, filtrando opcionalmente por estado. |
| PUT | `/orders/{orderId}/assign/{employeeId}` | EMPLOYEE | Asigna un empleado y pasa el pedido a IN_PREPARATION. |
| PUT | `/orders/{orderId}/status?status=` | EMPLOYEE | Cambia el estado del pedido (IN_PREPARATION, READY, CANCELLED). |
| PUT | `/orders/{orderId}/deliver?orderCode=` | EMPLOYEE | Marca el pedido como DELIVERED validando el código de entrega. |
| PUT | `/orders/{orderId}/cancel` | CLIENT | Cancela el pedido. |

## Reglas de negocio

Reglas implementadas en `application/usecase` y en los modelos de dominio:

**Restaurantes**
- El usuario indicado como propietario debe existir y tener rol OWNER (consulta a `user-service`).
- NIT y teléfono deben ser únicos. El nombre no puede ser solo números, el NIT debe ser numérico y el teléfono tener hasta 13 dígitos con `+` opcional.

**Empleados**
- Solo un usuario con rol OWNER puede crear empleados. El empleado queda asociado al restaurante de ese propietario.

**Platos**
- Solo el propietario del restaurante puede crear o actualizar sus platos.
- No puede haber dos platos con el mismo nombre en un restaurante.
- El precio debe ser un entero positivo; nombre, descripción y categoría son obligatorios.
- Al actualizar, el plato debe pertenecer al restaurante indicado. Si el DTO trae `categoryId`, la categoría se busca y se reemplaza (404 si no existe); si no lo trae, se conserva la categoría actual.

**Pedidos**
- Un cliente no puede crear un pedido si ya tiene otro activo.
- Todos los platos del pedido deben pertenecer al restaurante del pedido.
- Solo los empleados pueden listar pedidos.
- Asignación: el pedido debe estar PENDING y sin empleado asignado; el usuario asignado debe tener rol EMPLOYEE y pertenecer al mismo restaurante que el pedido.
- Para cambiar el estado o entregar un pedido, el usuario autenticado debe tener rol EMPLOYEE y pertenecer al restaurante del pedido.
- Transiciones permitidas en `PUT /orders/{id}/status`:
  - PENDING → IN_PREPARATION
  - IN_PREPARATION → READY: genera un código de 4 dígitos (1000–9999) y envía al cliente un mensaje de WhatsApp con ese código a través de `msg-service`.
  - PENDING → CANCELLED
  - Cualquier cambio a PENDING o DELIVERED se rechaza por este endpoint.
- Entrega: solo pedidos en READY y con el código correcto. Se notifica al cliente.
- Cancelación (`/cancel`): solo el cliente que hizo el pedido y solo si está en PENDING. Si otro usuario lo intenta, responde 401 sin notificar a nadie. Si el pedido ya no está en PENDING, se notifica al cliente y se responde error.
- Cada cambio de estado se registra en `traceability-service`.

## Configuración

`src/main/resources/application.yml`:

| Propiedad | Valor por defecto | Descripción |
|---|---|---|
| `server.port` | `8081` | Puerto HTTP. |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/plazoleta` | Base de datos MySQL. |
| `spring.datasource.username` | `${MYSQL_USER}` | Usuario MySQL. |
| `spring.datasource.password` | `${MYSQL_PASSWORD}` | Contraseña MySQL. |
| `spring.security.jwt.secret` | `${PRAGMA_JWT_KEY}` | Clave para validar los JWT. Debe ser la misma que usa `user-service`. |
| `services.url-users` | `http://localhost:8080` | URL de `user-service`. |
| `services.url-sms` | `http://localhost:8082` | URL de `msg-service`. |
| `services.url-traceability` | `http://localhost:8083` | URL de `traceability-service`. |

Variables de entorno obligatorias para arrancar la aplicación: `MYSQL_USER`, `MYSQL_PASSWORD` y `PRAGMA_JWT_KEY`. Las URLs de `services.*` tienen valor fijo en el `application.yml`; se pueden sobrescribir con las variables `SERVICES_URL_USERS`, `SERVICES_URL_SMS` y `SERVICES_URL_TRACEABILITY` (Spring resuelve los placeholders también desde variables de entorno en mayúsculas con `_`).

## Ejecución en local

Requisitos: JDK 17, MySQL 8 en `localhost:3306` y los demás servicios levantados si se van a usar los flujos que dependen de ellos.

```bash
# 1. Crear la base de datos (Hibernate crea las tablas con ddl-auto: update)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS plazoleta;"

# 2. Variables de entorno
export MYSQL_USER=root
export MYSQL_PASSWORD=<tu_password>
export PRAGMA_JWT_KEY=<misma_clave_que_user-service>

# 3. Arrancar
./gradlew bootRun
```

El servicio queda en `http://localhost:8081`.

## Tests

```bash
./gradlew test
```

La mayoría son tests unitarios con JUnit 5 y Mockito (`@ExtendWith(MockitoExtension.class)`). La excepción es `SecurityConfigTest`, un `@WebMvcTest` que carga solo los controladores, el filtro JWT y `SecurityConfig`, con los handlers y la validación del token mockeados. Ningún test se conecta a MySQL ni a los otros microservicios, así que `./gradlew build` pasa sin configurar variables de entorno.

| Clase | Qué cubre |
|---|---|
| `OrderUseCaseTest` | Creación de pedidos, listado solo para empleados, asignación de empleado (estado, rol y restaurante), transiciones de estado válidas e inválidas, que solo un empleado del restaurante del pedido pueda cambiar su estado o entregarlo, que solo el cliente dueño pueda cancelar, entrega con código y SMS enviado al puerto de mensajería cuando el pedido pasa a READY. |
| `DishUseCaseTest` | Creación y actualización de platos validando que el usuario sea dueño del restaurante, duplicados, categoría (incluida la actualización sin `categoryId`) y paginación. |
| `RestaurantUseCaseTest` | Creación de restaurantes (rol OWNER, NIT y teléfono duplicados), creación de empleados por el propietario y paginación. |
| `CategoryUseCaseTest` | Creación de categorías, nombre vacío y duplicados. |
| `SmsServiceAdapterTest` | El adaptador delega en el cliente Feign de `msg-service` (mockeado). |
| `SecurityConfigTest` | `POST /categories` y `POST /users/employee` solo con rol OWNER, `POST /orders` solo con rol CLIENT, y sin token siempre 403. |

No hay tests de integración contra base de datos ni de extremo a extremo.

## Deuda técnica conocida

Hallazgos de las rondas de tests que todavía no se han corregido:

**Trazabilidad**
- Al crear un pedido, el registro de trazabilidad se envía antes de guardar el pedido, así que llega con `orderId` nulo.
- `PUT /orders/{id}/status` permite pasar de PENDING a CANCELLED sin registrar la hora de fin ni notificar al cliente, a diferencia de `/cancel`.

**SMS**
- El mensaje de pedido listo concatena el código sin espacio (`...pickup!1234`).
- El prefijo `+57` del teléfono es fijo.
- El código de entrega sale de un `new Random()` que no se puede inyectar en los tests.
- Si `/cancel` falla porque el pedido no está en PENDING, igual se envía un SMS al cliente.

**Validaciones y textos**
- `PUT /orders/{id}/status` con `status` nulo lanza `NullPointerException`; la constante `MSG_ORDER_STATUS_CANNOT_BE_NULL` existe pero no se usa.
- `PUT /orders/{id}/deliver` con `orderCode` nulo lanza `NullPointerException`.
- `createDish` revisa el nombre duplicado antes de verificar que el usuario sea dueño del restaurante.
- `PUT /dishes` siempre deja el plato activo: el DTO no tiene el campo `active` y el constructor de `Dish` lo pone en `true`.
- `DISH_DESCRIPTION_MIN_LENGTH_MESSAGE` dice "at least 10 characters", pero la validación es `@Size(min = 1)`.

**Seguridad**
- `GET /orders` en `SecurityConfig` no coincide con `/orders/{restaurantId}`; la restricción a empleados la aplica solo el caso de uso.
- Las peticiones sin token responden 403 en lugar de 401, porque no hay un `AuthenticationEntryPoint` configurado.
- `UnauthorizedException` se traduce a 401, aunque en los casos de rol o propiedad correspondería un 403.
