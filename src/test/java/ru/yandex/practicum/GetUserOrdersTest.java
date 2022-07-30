package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.ApiClient;
import ru.yandex.practicum.stellaburgers.api.model.*;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetUserOrdersTest {
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
    @DisplayName("Получение заказов пользователя с авторизацией")
    public void getAuthUserOrdersTest() {
        LoginUserResponse loginUserResponse = apiClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        apiClient.setAccessToken(loginUserResponse.getAccessToken());

        apiClient.createOrder(order)
                .then()
                .statusCode(SC_OK);

        GetUserOrdersResponse getUserOrdersResponse = apiClient.getUserOrders()
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
        apiClient.createOrder(order)
                .then()
                .statusCode(SC_OK);

        ErrorResponse errorResponse = apiClient.getUserOrders()
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("You should be authorised", errorResponse.getMessage());
    }
}
