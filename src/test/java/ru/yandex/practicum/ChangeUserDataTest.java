package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.client.UserClient;
import ru.yandex.practicum.stellaburgers.api.model.*;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class ChangeUserDataTest {
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
    @DisplayName("Изменение Email пользователя с авторизацией")
    public void changeEmailWithAuthTest() {
        String newEmail = "new-" + user.getEmail();
        LoginUserResponse loginUserResponse = userClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        userClient.setAccessToken(loginUserResponse.getAccessToken());

        ChangeUserDataResponse changeUserDataResponse = userClient.changeUserData(newEmail, user.getName())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(ChangeUserDataResponse.class);

        assertTrue(changeUserDataResponse.isSuccess());
        assertEquals(newEmail, changeUserDataResponse.getUser().getEmail());
        assertEquals(user.getName(), changeUserDataResponse.getUser().getName());
    }

    @Test
    @DisplayName("Изменение Name пользователя с авторизацией")
    public void changeNameWithAuthTest() {
        String newName = "new-" + user.getName();
        LoginUserResponse loginUserResponse = userClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        userClient.setAccessToken(loginUserResponse.getAccessToken());

        ChangeUserDataResponse changeUserDataResponse = userClient.changeUserData(user.getEmail(), newName)
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(ChangeUserDataResponse.class);

        assertTrue(changeUserDataResponse.isSuccess());
        assertEquals(user.getEmail(), changeUserDataResponse.getUser().getEmail());
        assertEquals(newName, changeUserDataResponse.getUser().getName());
    }

    @Test
    @DisplayName("Изменение Email пользователя без авторизацией")
    public void changeEmailWithoutAuthTest() {
        String newEmail = "new-" + user.getEmail();

        ErrorResponse errorResponse = userClient.changeUserData(newEmail, user.getName())
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("You should be authorised", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Изменение Name пользователя без авторизацией")
    public void changeNameWithoutAuthTest() {
        String newName = "new-" + user.getName();

        ErrorResponse errorResponse = userClient.changeUserData(newName, user.getName())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("You should be authorised", errorResponse.getMessage());
    }
}
