import com.github.javafaker.Faker;
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

public class GetUserOrdersTest {

    public static Faker faker = new Faker();
    private List<String> ingredients;
    private List<String> ingredientsTwo;
    private static String accessToken;
    private Order orderOne;
    private Order orderTwo;
    private static final String EMAIL = RandomStringUtils.randomNumeric(5) + faker.internet().emailAddress();
    private static final String PASSWORD = faker.internet().password();
    private static final String NAME = faker.name().firstName();
    public static final User USER = new User(EMAIL, PASSWORD, NAME);

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;

        Response getResponse = OrderAPI.getIngredients();
        String firstIngredient = getResponse.then().extract().path("data[0]._id");
        String secondIngredient = getResponse.then().extract().path("data[1]._id");
        ingredients = new ArrayList<>();
        ingredients.add(firstIngredient);
        ingredients.add(secondIngredient);
        orderOne = new Order(ingredients);

        ingredientsTwo = new ArrayList<>();
        ingredientsTwo.add(secondIngredient);
        ingredientsTwo.add(firstIngredient);
        orderTwo= new Order(ingredientsTwo);

    }

    @Test
    @DisplayName("Get orders authorized user, result ok")
    public void getOrdersAuthorizedUserExpectOk() {

        Response responseCreate = UserAPI.createUser(USER);

        responseCreate
                .then().statusCode(SC_OK).and()
                .assertThat().body("success", equalTo(true));

        Response responseLogin = UserAPI.logInUser(USER);

        responseLogin.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        accessToken = responseLogin.then().extract().path("accessToken").toString();

        Response responseMakeOrderOne = OrderAPI.makeOrder(accessToken, orderOne);
        responseMakeOrderOne
                .then()
                .assertThat().body("success", equalTo(true));

        Response responseMakeOrderTwo = OrderAPI.makeOrder(accessToken, orderTwo);
        responseMakeOrderTwo
                .then()
                .assertThat().body("success", equalTo(true));

        Response responseGetOrders = OrderAPI.getOrderAuthorizedUser(accessToken);
        responseGetOrders
                .then()
                .assertThat().body("success",equalTo(true))
                .and()
                .body("orders",notNullValue())
                .and()
                .body("total",notNullValue());

    }


    @Test
    @DisplayName("Get orders no authorized user, result false")
    public void getOrdersNoAuthorizedUserExpectError() {

        Response responseGetOrdersNoAuthorized = OrderAPI.getOrderNoAuthorizedUser();
        responseGetOrdersNoAuthorized
                .then()
                .assertThat().body("success",equalTo(false))
                .and()
                .body("message",equalTo("You should be authorised"));

    }


    @After
    public void deleteUser() {
        UserAPI.deleteUser(accessToken);
    }

}
