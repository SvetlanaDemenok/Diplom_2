package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.client.OrderClient;
import ru.yandex.practicum.stellaburgers.api.client.UserClient;
import ru.yandex.practicum.stellaburgers.api.model.*;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetUserOrdersTest {
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
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void getAuthUserOrdersTest() {
        LoginUserResponse loginUserResponse = userClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        orderClient.setAccessToken(loginUserResponse.getAccessToken());

        orderClient.createOrder(order)
                .then()
                .statusCode(SC_OK);

        GetUserOrdersResponse getUserOrdersResponse = orderClient.getUserOrders()
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(GetUserOrdersResponse.class);

        assertTrue(getUserOrdersResponse.isSuccess());
        assertEquals(1, getUserOrdersResponse.getOrders().size());
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getUnAuthUserOrdersTest() {
        orderClient.createOrder(order)
                .then()
                .statusCode(SC_OK);

        ErrorResponse errorResponse = orderClient.getUserOrders()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("You should be authorised", errorResponse.getMessage());
    }
}
