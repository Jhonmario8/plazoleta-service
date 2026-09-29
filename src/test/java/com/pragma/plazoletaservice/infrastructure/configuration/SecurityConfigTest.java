package com.pragma.plazoletaservice.infrastructure.configuration;

import com.pragma.plazoletaservice.application.handler.ICategoryHandler;
import com.pragma.plazoletaservice.application.handler.IDishHandler;
import com.pragma.plazoletaservice.application.handler.IOrderHandler;
import com.pragma.plazoletaservice.application.handler.IRestaurantHandler;
import com.pragma.plazoletaservice.infrastructure.input.controller.DishController;
import com.pragma.plazoletaservice.infrastructure.input.controller.OrderController;
import com.pragma.plazoletaservice.infrastructure.input.controller.RestaurantController;
import com.pragma.plazoletaservice.infrastructure.output.security.adapter.TokenServiceAdapter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica las reglas de acceso de SecurityConfig con un slice web (@WebMvcTest):
 * solo controladores, filtro JWT y seguridad. Los handlers y la validación del token
 * están mockeados, así que no hay base de datos, JWT real ni llamadas a otros servicios.
 */
@WebMvcTest(controllers = {DishController.class, OrderController.class, RestaurantController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final String OWNER_TOKEN = "owner-token";
    private static final String CLIENT_TOKEN = "client-token";
    private static final String EMPLOYEE_TOKEN = "employee-token";

    private static final String ORDER_BODY = """
            {"restaurantId": 1, "dishes": [{"dishId": 5, "quantity": 2}]}
            """;
    private static final String EMPLOYEE_BODY = """
            {"name": "Luis", "lastName": "Gómez", "identificationNumber": "123456",
             "phoneNumber": "+573001112233", "birthDate": "1990-01-01",
             "email": "luis@correo.com", "password": "secreta123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenServiceAdapter tokenServiceAdapter;
    @MockBean
    private IDishHandler dishHandler;
    @MockBean
    private ICategoryHandler categoryHandler;
    @MockBean
    private IOrderHandler orderHandler;
    @MockBean
    private IRestaurantHandler restaurantHandler;

    @BeforeEach
    void setUpTokens() {
        when(tokenServiceAdapter.validateToken(OWNER_TOKEN)).thenReturn(claims(1L, "OWNER"));
        when(tokenServiceAdapter.validateToken(CLIENT_TOKEN)).thenReturn(claims(2L, "CLIENT"));
        when(tokenServiceAdapter.validateToken(EMPLOYEE_TOKEN)).thenReturn(claims(3L, "EMPLOYEE"));
    }

    private static Claims claims(Long userId, String role) {
        Claims claims = Jwts.claims(Map.of("user_id", userId, "role_name", role));
        claims.setExpiration(new Date(System.currentTimeMillis() + 60_000));
        return claims;
    }

    private static MockHttpServletRequestBuilder withToken(MockHttpServletRequestBuilder request, String token) {
        return request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    @Nested
    @DisplayName("POST /categories")
    class CreateCategory {

        @Test
        @DisplayName("sin token responde 403 y no crea la categoría")
        void rejectsAnonymous() throws Exception {
            mockMvc.perform(post("/categories").param("categoryName", "Postres"))
                    .andExpect(status().isForbidden());
            verify(categoryHandler, never()).createCategory(anyString());
        }

        @Test
        @DisplayName("con rol CLIENT responde 403")
        void rejectsClient() throws Exception {
            mockMvc.perform(withToken(post("/categories").param("categoryName", "Postres"), CLIENT_TOKEN))
                    .andExpect(status().isForbidden());
            verify(categoryHandler, never()).createCategory(anyString());
        }

        @Test
        @DisplayName("con rol OWNER responde 201")
        void allowsOwner() throws Exception {
            mockMvc.perform(withToken(post("/categories").param("categoryName", "Postres"), OWNER_TOKEN))
                    .andExpect(status().isCreated());
            verify(categoryHandler).createCategory("Postres");
        }
    }

    @Nested
    @DisplayName("POST /orders")
    class CreateOrder {

        @Test
        @DisplayName("sin token responde 403 y no crea el pedido")
        void rejectsAnonymous() throws Exception {
            mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(ORDER_BODY))
                    .andExpect(status().isForbidden());
            verify(orderHandler, never()).createOrder(any());
        }

        @Test
        @DisplayName("con rol EMPLOYEE responde 403")
        void rejectsEmployee() throws Exception {
            mockMvc.perform(withToken(post("/orders"), EMPLOYEE_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON).content(ORDER_BODY))
                    .andExpect(status().isForbidden());
            verify(orderHandler, never()).createOrder(any());
        }

        @Test
        @DisplayName("con rol CLIENT responde 201")
        void allowsClient() throws Exception {
            mockMvc.perform(withToken(post("/orders"), CLIENT_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON).content(ORDER_BODY))
                    .andExpect(status().isCreated());
            verify(orderHandler).createOrder(any());
        }
    }

    @Nested
    @DisplayName("POST /users/employee")
    class CreateEmployee {

        @Test
        @DisplayName("sin token responde 403 y no crea el empleado")
        void rejectsAnonymous() throws Exception {
            mockMvc.perform(post("/users/employee").contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_BODY))
                    .andExpect(status().isForbidden());
            verify(restaurantHandler, never()).createEmployee(any());
        }

        @Test
        @DisplayName("con rol CLIENT responde 403")
        void rejectsClient() throws Exception {
            mockMvc.perform(withToken(post("/users/employee"), CLIENT_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_BODY))
                    .andExpect(status().isForbidden());
            verify(restaurantHandler, never()).createEmployee(any());
        }

        @Test
        @DisplayName("con rol OWNER responde 201")
        void allowsOwner() throws Exception {
            mockMvc.perform(withToken(post("/users/employee"), OWNER_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON).content(EMPLOYEE_BODY))
                    .andExpect(status().isCreated());
            verify(restaurantHandler).createEmployee(any());
        }
    }
}
