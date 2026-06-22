import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.OrderModel;
import model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Arrays;
import java.util.Collections;

import static data.UserData.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest extends BaseApiTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

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
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа авторизованным пользователем с ингредиентами")
    public void createOrderWithAuthorizationSuccess() {
        OrderModel order = new OrderModel(
                Arrays.asList(BUN_ID, MAIN_ID, SAUCE_ID)
        );

        orderSteps.createOrder(order, accessToken)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.owner.email", equalTo(user.getEmail()))
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.ingredients", notNullValue())
                .body("order.status", equalTo("done"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа неавторизованным пользователем с ингредиентами")
    public void createOrderWithoutAuthorizationSuccess() {
        OrderModel order = new OrderModel(
                Arrays.asList(BUN_ID, MAIN_ID, SAUCE_ID)
        );

        orderSteps.createOrder(order)
                .then()
                .log().all()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка создания заказа без ингредиентов. Ожидается ошибка 400")
    public void createOrderWithoutIngredientsReturnsBadRequest() {
        OrderModel order = new OrderModel(Collections.emptyList());

        orderSteps.createOrder(order)
                .then()
                .log().all()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка создания заказа с невалидным id ингредиента. Ожидается 400, но фактически API может возвращать 500 — баг")
    public void createOrderWithInvalidIngredientReturnsBadRequest() {
        OrderModel order = new OrderModel(
                Arrays.asList(INVALID_INGREDIENT_ID)
        );

        orderSteps.createOrder(order)
                .then()
                .log().all()
                .statusCode(HTTP_BAD_REQUEST);
    }
}
