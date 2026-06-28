import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserCredentials;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static data.UserData.NAME;
import static data.UserData.PASSWORD;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();

    private UserModel user;
    private String accessToken;

    @Before
    public void createUserBeforeTest() {
        user = new UserModel(
                "sveta" + System.currentTimeMillis() + "@mail.ru",
                PASSWORD,
                NAME
        );

        Response response = userSteps.createUser(user);
        accessToken = response.path("accessToken");
    }

    @After
    public void deleteUserAfterTest() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Проверка входа под существующим пользователем. Ожидается код ответа 200, success=true и токены в ответе")
    public void loginExistingUserSuccess() {
        UserCredentials credentials = new UserCredentials(
                user.getEmail(),
                user.getPassword()
        );

        userSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Проверка входа с неверным логином. Ожидается код ответа 401, success=false и сообщение об ошибке")
    public void loginWithInvalidLoginReturnsError() {
        UserCredentials credentials = new UserCredentials(
                "wrong" + System.currentTimeMillis() + "@mail.ru",
                user.getPassword()
        );

        userSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка входа с неверным паролем. Ожидается код ответа 401, success=false и сообщение об ошибке")
    public void loginWithInvalidPasswordReturnsError() {
        UserCredentials credentials = new UserCredentials(
                user.getEmail(),
                "wrongPassword"
        );

        userSteps.loginUser(credentials)
                .then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
