package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.client.OrderClient;
import ru.yandex.practicum.stellaburgers.api.client.UserClient;
import ru.yandex.practicum.stellaburgers.api.model.*;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateOrderTest {
    UserClient userClient;
    OrderClient orderClient;
    User user;
    Order order;
    Response response;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        user = User.getRandomUser();
        response = userClient.createUser(user);

        order = new Order();
        order.addIngredient("61c0c5a71d1f82001bdaaa6d");
        order.addIngredient("61c0c5a71d1f82001bdaaa6f");
    }

    @After
    public void tearDown() {
        if (response.statusCode() == SC_OK) {
            userClient.setAccessToken(response.as(CreateUserResponse.class).getAccessToken());
            userClient.removeUser();
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthTest() {
        LoginUserResponse loginUserResponse = userClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        orderClient.setAccessToken(loginUserResponse.getAccessToken());

        CreateOrderResponse createOrderResponse = orderClient.createOrder(order)
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
        CreateOrderResponse createOrderResponse = orderClient.createOrder(order)
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

        ErrorResponse errorResponse = orderClient.createOrder(order)
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

        orderClient.createOrder(order)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }


}
