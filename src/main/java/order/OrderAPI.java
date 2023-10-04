package order;
import io.qameta.allure.Step;
import io.restassured.response.Response;


import static io.restassured.RestAssured.given;

public class OrderAPI {

    public static final String MAKE_ORDER_ENDPOINT = "/api/orders";

    public static final String INGREDIENTS_ENDPOINT = "/api/ingredients";


    @Step("Make order")
    public static Response makeOrder(String accessToken, Order order) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .and()
                .body(order)
                .when()
                .post(MAKE_ORDER_ENDPOINT);
        return response;
    }

    @Step("Get order authorised user")
    public static Response getOrderAuthorizedUser (String accessToken) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .when()
                .get(MAKE_ORDER_ENDPOINT);
        return response;
    }

    @Step("Get order no authorised user")
    public static Response getOrderNoAuthorizedUser () {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(MAKE_ORDER_ENDPOINT);
        return response;
    }

    @Step("Get ingredients")
    public static Response getIngredients() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(INGREDIENTS_ENDPOINT);
        return response;
    }

}
