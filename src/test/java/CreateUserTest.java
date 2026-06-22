import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserModel;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;

import static data.UserData.NAME;
import static data.UserData.PASSWORD;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();
    private String accessToken;

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    @Description("Проверка успешного создания пользователя с уникальным email. Ожидается код ответа 200 и success=true")
    public void createUniqueUserSuccess() {
        UserModel user = new UserModel(
                "sveta" + System.currentTimeMillis() + "@mail.ru",
                PASSWORD,
                NAME
        );

        Response response = userSteps.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка ошибки при повторном создании пользователя с теми же данными. Ожидается код ответа 403 и success=false")
    public void createRegisteredUserReturnsError() {
        UserModel user = new UserModel(
                "sveta" + System.currentTimeMillis() + "@mail.ru",
                PASSWORD,
                NAME
        );

        Response firstResponse = userSteps.createUser(user);
        accessToken = firstResponse.path("accessToken");

        userSteps.createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля")
    @Description("Проверка ошибки при создании пользователя без email. Ожидается код ответа 403 и success=false")
    public void createUserWithoutRequiredFieldReturnsError() {
        UserModel user = new UserModel(
                null,
                PASSWORD,
                NAME
        );

        userSteps.createUser(user)
                .then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
