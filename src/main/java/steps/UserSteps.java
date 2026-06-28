package steps;

import io.restassured.response.Response;
import model.UserCredentials;
import model.UserModel;

import static data.UserData.*;
import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;

public class UserSteps {

    @Step ("Создание пользователя")
    public Response createUser(UserModel user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post(CREATE_USER_PATH);
    }

    @Step ("Авторизация пользователя")
    public Response loginUser(UserCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .post(LOGIN_USER_PATH);
    }

    @Step ("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .header("Authorization", token)
                .delete(DELETE_USER_PATH);
    }
}
