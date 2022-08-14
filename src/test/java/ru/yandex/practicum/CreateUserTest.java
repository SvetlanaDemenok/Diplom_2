package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.stellaburgers.api.client.UserClient;
import ru.yandex.practicum.stellaburgers.api.model.CreateUserResponse;
import ru.yandex.practicum.stellaburgers.api.model.ErrorResponse;
import ru.yandex.practicum.stellaburgers.api.model.User;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;
import static ru.yandex.practicum.stellaburgers.api.model.User.getRandomUser;

@DisplayName("Создание пользователя")
public class CreateUserTest {
    UserClient userClient;
    User user;
    Response response;

    @Before
    public void setUp() {
        userClient = new UserClient();

        // Готовим данные
        user = getRandomUser();
    }

    @After
    public void tearDown() {
        if (response.statusCode() == SC_OK) {
            userClient.setAccessToken(response.as(CreateUserResponse.class).getAccessToken());
            userClient.removeUser();
        }
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void createUserTest() {
        // Делаем действие
        response = userClient.createUser(user);

        // Проверка
        CreateUserResponse createUserResponse = response
                .then()
                .statusCode(SC_OK)
                .extract()
                .as(CreateUserResponse.class);

        assertTrue(createUserResponse.isSuccess());
        assertEquals(user.getEmail(), createUserResponse.getUser().getEmail());
        assertEquals(user.getName(), createUserResponse.getUser().getName());
    }

    @Test
    @DisplayName("Создание двух одинаковых пользователей")
    public void createDupUserTest() {
        // Делаем действие
        response = userClient.createUser(user);

        // Проверка
        response.then().statusCode(SC_OK);

        // Делаем действие еще раз
        ErrorResponse errorResponse = userClient.createUser(user)
                .then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .as(ErrorResponse.class);

        // Проверка
        assertFalse(errorResponse.isSuccess());
        assertEquals("User already exists", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя с пустым Email")
    public void createUserWithEmptyEmailTest() {
        user.setEmail("");

        // Делаем действие
        response = userClient.createUser(user);

        // Проверка
        ErrorResponse errorResponse = response
                .then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("Email, password and name are required fields", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя с пустым паролем")
    public void createUserWithEmptyPasswordTest() {
        user.setPassword("");

        // Делаем действие
        response = userClient.createUser(user);

        // Проверка
        ErrorResponse errorResponse = response
                .then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("Email, password and name are required fields", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя с пустым логином")
    public void createUserWithEmptyNameTest() {
        user.setName("");

        // Делаем действие
        response = userClient.createUser(user);

        // Проверка
        ErrorResponse errorResponse = response
                .then()
                .statusCode(SC_FORBIDDEN)
                .extract()
                .as(ErrorResponse.class);

        assertFalse(errorResponse.isSuccess());
        assertEquals("Email, password and name are required fields", errorResponse.getMessage());
    }
}
