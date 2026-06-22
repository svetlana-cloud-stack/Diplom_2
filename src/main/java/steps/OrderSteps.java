package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.OrderModel;

import static data.UserData.CREATE_ORDER_PATH;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа без авторизации")
    public Response createOrder(OrderModel order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(CREATE_ORDER_PATH);
    }

    @Step("Создание заказа с авторизацией")
    public Response createOrder(OrderModel order, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(order)
                .post(CREATE_ORDER_PATH);
    }
}