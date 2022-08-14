package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.client.UserClient;
import ru.yandex.practicum.stellaburgers.api.model.CreateUserResponse;
import ru.yandex.practicum.stellaburgers.api.model.ErrorResponse;
import ru.yandex.practicum.stellaburgers.api.model.LoginUserResponse;
import ru.yandex.practicum.stellaburgers.api.model.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class LoginUserTest {
    UserClient userClient;
    User user;
    Response response;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandomUser();
        response = userClient.createUser(user);
    }

    @After
    public void tearDown() {
        if (response.statusCode() == SC_OK) {
            userClient.setAccessToken(response.as(CreateUserResponse.class).getAccessToken());
            userClient.removeUser();
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void successLoginTest() {
        LoginUserResponse loginUserResponse = userClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);

        assertTrue(loginUserResponse.isSuccess());
        assertEquals(user.getEmail(), loginUserResponse.getUser().getEmail());
        assertEquals(user.getName(), loginUserResponse.getUser().getName());
    }

    @Test
    @DisplayName("Логин с неверным email")
    public void incorrectEmailLoginTest() {
        ErrorResponse errorResponse = userClient.loginUser("incorrect-" + user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("email or password are incorrect", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void incorrectPasswordLoginTest() {
        ErrorResponse errorResponse = userClient.loginUser(user.getEmail(), "incorrect-" + user.getPassword())
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("email or password are incorrect", errorResponse.getMessage());
    }

}
