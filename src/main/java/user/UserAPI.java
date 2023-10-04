package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserAPI {

    public static final String USER_REG_ENDPOINT = "/api/auth/register";
    public static final String LOGIN_ENDPOINT = "/api/auth/login";
    public static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    public static final String USER_DATA_ENDPOINT = "/api/auth/user";

    @Step("Create user")
    public static Response createUser(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(USER_REG_ENDPOINT);
        return response;
    }

    @Step("User log in")
    public static Response logInUser(User user) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN_ENDPOINT);
        return response;
    }

    @Step("User log out")
    public static Response logOutUser(RefreshToken refreshToken) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(refreshToken)
                .when()
                .post(LOGOUT_ENDPOINT);
        return response;
    }

    @Step("Delete user")
    public static void deleteUser(String accessToken) {
        given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .delete(USER_DATA_ENDPOINT + accessToken);
    }

    @Step("Change user data")
    public static Response changeUser(User User, String accessToken) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .header("Authorization", accessToken)
                .body(User)
                .when()
                .patch(USER_DATA_ENDPOINT);
        return response;
    }

}
