import io.restassured.response.Response;
import order.Order;
import order.OrderAPI;
import org.junit.After;
import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class MakeOrderTest {

    private List<String> ingredients;
    private static String accessToken;
    private Order order;
    private static final String EMAIL = "email" + RandomStringUtils.randomNumeric(5) + "@yandex.ru";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    public static final User USER = new User(EMAIL, PASSWORD, NAME);

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;
        ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa76");
        ingredients.add("61c0c5a71d1f82001bdaaa6c");
        order = new Order(ingredients);
    }

    @Test
    @DisplayName("Make order authorized user, result ok")
    public void makeOrderAuthorizedUserExpectOk() {

        Response responseCreate = UserAPI.createUser(USER);

        responseCreate
                .then().statusCode(SC_OK).and()
                .assertThat().body("success", equalTo(true));

        Response responseLogin = UserAPI.logInUser(USER);

        responseLogin.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        accessToken = responseCreate.then().extract().path("accessToken").toString();

        Response responseMakeOrder = OrderAPI.makeOrder(order);
        responseMakeOrder
                .then()
                .assertThat().body("success", equalTo(true))
                .and()
                .body("name",notNullValue())
                .and()
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Make order noauthorized user, result ok")
    public void makeOrderNoAuthorizedUserExpectOk() {

        Response responseMakeOrder = OrderAPI.makeOrder(order);
        responseMakeOrder
                .then()
                .assertThat().body("success", equalTo(true))
                .and()
                .body("name",notNullValue())
                .and()
                .body("order.number", notNullValue());

    }

    @Test
    @DisplayName("Make order no valid ingredient, result error")
    public void makeOrderNoValidIngredientExpectError() {

        ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d_1");
        ingredients.add("61c0c5a71d1f82001bdaaa76");
        order = new Order(ingredients);

        Response responseMakeOrder = OrderAPI.makeOrder(order);
        responseMakeOrder
                .then()
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);;

    }

    @Test
    @DisplayName("Make order no ingredient, result error")
    public void makeOrderNoIngredientExpectError() {

        ingredients = new ArrayList<>();
        order = new Order(ingredients);

        Response responseMakeOrder = OrderAPI.makeOrder(order);
        responseMakeOrder
                .then()
                .assertThat().statusCode(SC_BAD_REQUEST)
                .and()
                .body("success",equalTo(false))
                .and()
                .body("message",equalTo("Ingredient ids must be provided"));

    }

    @After
    public void deleteUser() {
        UserAPI.deleteUser(accessToken);
    }

}
