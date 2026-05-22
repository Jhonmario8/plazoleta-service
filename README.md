# Plazoleta Service

Servicio centralizado para la gestión de una "plazoleta de comidas". Este microservicio está construido en Java 17 con Spring Boot 3, siguiendo principios de Clean Architecture y prácticas modernas para microservicios. Maneja dominios como usuarios, restaurantes, menús y pedidos, integrando autenticación JWT, acceso a base de datos y consumo de microservicios externos.

## Tabla de Contenidos

- [Descripción](#descripción)
- [Características principales](#características-principales)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Estructura de carpetas](#estructura-de-carpetas)
- [Configuración inicial](#configuración-inicial)
- [Variables de entorno](#variables-de-entorno)
- [Dependencias principales](#dependencias-principales)
- [Ejecución y pruebas](#ejecución-y-pruebas)
- [Contribuciones](#contribuciones)

---

## Descripción

Plazoleta Service abstrae los procesos relativos a la administración de una plazoleta de comidas: registro y autenticación de usuarios, gestión de restaurantes, menús y pedidos, integrando tanto persistencia como comunicación con servicios de usuarios y notificaciones.

## Características principales

- API RESTful construida con Spring Boot.
- Gestión y autenticación de usuarios usando JWT.
- Integración con microservicios externos vía OpenFeign.
- Arquitectura limpia: separación de capas dominio, aplicación e infraestructura.
- Uso de JPA (Hibernate) con base de datos MySQL.
- Soporte para pruebas unitarias y de integración con JUnit y Spring Test.
- Mapping automático de DTOs con MapStruct.
- Validación de datos y manejo estructurado de excepciones.

## Arquitectura del proyecto

El proyecto sigue principios de Clean Architecture, separando la lógica en:

- **Domain:** Modelos de dominio, interfaces (api, spi), excepciones y constantes.
- **Application:** Casos de uso (usecase), DTOs, mappers, handlers y lógica de aplicación.
- **Infrastructure:** Configuraciones, adaptadores para entrada (controladores REST), salida (repositorios, APIs externas), utilidades y excepciones específicas de infraestructura.

## Estructura de carpetas

```
src/main/java/com/pragma/plazoletaservice/
│
├── application/
│   ├── constants/
│   ├── dto/
│   ├── handler/
│   ├── mapper/
│   └── usecase/
├── domain/
│   ├── api/
│   ├── constants/
│   ├── exception/
│   ├── model/
│   └── spi/
├── infrastructure/
│   ├── configuration/
│   ├── constants/
│   ├── exception/
│   ├── input/
│   ├── output/
│   └── util/
└── PlazoletaServiceApplication.java
```

- **resources/application.yml**: Configuración del datasource, JWT y URLs de servicios externos.
- **src/test/java/**: Pruebas unitarias y de integración.

## Configuración inicial

1. Clona este repositorio.
2. Configura las variables de entorno requeridas (ver siguiente sección).
3. Asegúrate de tener MySQL en ejecución y accesible en `localhost:3306`, con una base de datos llamada `plazoleta`.
4. Ejecuta el proyecto con Gradle o tu IDE favorito.

### Variables de entorno

- `MYSQL_USER`: Usuario de la base de datos MySQL.
- `MYSQL_PASSWORD`: Contraseña de la base de datos.
- `PRAGMA_JWT_KEY`: Clave secreta para la autenticación JWT.

Opcionalmente, puedes cambiar las URLs de microservicios en `src/main/resources/application.yml`:
- `user-service.url-users`
- `user-service.url-sms`

## Dependencias principales

- Spring Boot (Web, Data JPA, Security, Validation, Devtools)
- MySQL Connector/J
- Spring Cloud OpenFeign (consumo de APIs externas)
- JWT (io.jsonwebtoken)
- MapStruct (mapeo de DTOs y modelos)
- Lombok (reducción de boilerplate)
- JUnit (pruebas)

## Ejecución y pruebas

Para compilar y ejecutar:

```bash
./gradlew bootRun
```

Para ejecutar las pruebas unitarias y de integración:

```bash
./gradlew test
```

El servicio estará disponible en `http://localhost:8081/`.

## Contribuciones

¡Las contribuciones son bienvenidas! Abre un issue o pull request siguiendo las buenas prácticas de GitHub.

---

> Proyecto desarrollado con Java 17 y Spring Boot por [Jhonmario8](https://github.com/Jhonmario8).