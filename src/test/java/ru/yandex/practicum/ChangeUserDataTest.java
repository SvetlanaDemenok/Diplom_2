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

public class ChangeUserDataTest {
    ApiClient apiClient;
    User user;
    Response response;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        user = User.getRandomUser();
        response = apiClient.createUser(user);
    }

    @After
    public void tearDown() {
        if (response.statusCode() == SC_OK) {
            apiClient.setAccessToken(response.as(CreateUserResponse.class).getAccessToken());
            apiClient.removeUser();
        }
    }

    @Test
    @DisplayName("Изменение Email пользователя с авторизацией")
    public void changeEmailWithAuthTest() {
        String newEmail = "new-" + user.getEmail();
        LoginUserResponse loginUserResponse = apiClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        apiClient.setAccessToken(loginUserResponse.getAccessToken());

        ChangeUserDataResponse changeUserDataResponse = apiClient.changeUserData(newEmail, user.getName())
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
        LoginUserResponse loginUserResponse = apiClient.loginUser(user.getEmail(), user.getPassword())
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(LoginUserResponse.class);
        apiClient.setAccessToken(loginUserResponse.getAccessToken());

        ChangeUserDataResponse changeUserDataResponse = apiClient.changeUserData(user.getEmail(), newName)
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

        ErrorResponse errorResponse = apiClient.changeUserData(newEmail, user.getName())
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

        ErrorResponse errorResponse = apiClient.changeUserData(newName, user.getName())
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("You should be authorised", errorResponse.getMessage());
    }
}
