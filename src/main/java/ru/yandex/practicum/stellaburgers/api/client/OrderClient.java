package ru.yandex.practicum.stellaburgers.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.stellaburgers.api.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {
    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given()
                .spec(getReqSpec())
                .auth()
                .oauth2(accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders() {
        return given()
                .spec(getReqSpec())
                .auth()
                .oauth2(accessToken)
                .when()
                .get("/api/orders");
    }
}
