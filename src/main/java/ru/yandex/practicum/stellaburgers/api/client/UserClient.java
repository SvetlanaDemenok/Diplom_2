package ru.yandex.practicum.stellaburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.stellaburgers.api.model.User;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {
    @Step("Создание пользователя")
    public Response createUser(User user) {
        return given()
                .spec(getReqSpec())
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Удаление пользователя")
    public Response removeUser() {
        return given()
                .spec(getReqSpec())
                .auth()
                .oauth2(accessToken)
                .when()
                .delete("/api/auth/user");
    }

    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        return given()
                .spec(getReqSpec())
                .body(Map.of("email", email, "password", password))
                .when()
                .post("/api/auth/login");
    }

    @Step("Изменение данных пользователя")
    public Response changeUserData(String email, String name) {
        return given()
                .spec(getReqSpec())
                .auth()
                .oauth2(accessToken)
                .body(Map.of("email", email, "name", name))
                .when()
                .patch("/api/auth/user");
    }
}
