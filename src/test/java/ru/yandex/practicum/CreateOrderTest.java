package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.ApiClient;
import ru.yandex.practicum.stellaburgers.api.model.*;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    ApiClient apiClient;
    User user;
    Order order;
    Response response;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        user = User.getRandomUser();
        response = apiClient.createUser(user);

        order = new Order();
        order.addIngredient("61c0c5a71d1f82001bdaaa6d");
        order.addIngredient("61c0c5a71d1f82001bdaaa6f");
    }

    @After
    public void tearDown() {
        if (response.statusCode() == SC_OK) {
            apiClient.setAccessToken(response.as(CreateUserResponse.class).getAccessToken());
            apiClient.removeUser();
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {
        LoginUserResponse loginUserResponse = apiClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        apiClient.setAccessToken(loginUserResponse.getAccessToken());

        CreateOrderResponse createOrderResponse = apiClient.createOrder(order)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(CreateOrderResponse.class);

        assertTrue(createOrderResponse.isSuccess());
        assertNotNull(createOrderResponse.getOrder().getOwner());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        CreateOrderResponse createOrderResponse = apiClient.createOrder(order)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(CreateOrderResponse.class);

        assertTrue(createOrderResponse.isSuccess());
        assertNull(createOrderResponse.getOrder().getOwner());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        order.clearIngredients();

        ErrorResponse errorResponse = apiClient.createOrder(order)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("Ingredient ids must be provided", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиента")
    public void createOrderWithWrongIngredientsTest() {
        order.addIngredient("");
        order.addIngredient("666");

        apiClient.createOrder(order)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }


}
