import io.restassured.response.Response;
import org.junit.*;
import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class LogInUserTest {


    private static String accessToken;
    private static final String EMAIL = "email" + RandomStringUtils.randomNumeric(5) + "@yandex.ru";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    public static final User USER = new User(EMAIL, PASSWORD, NAME);

    @BeforeClass
    public static void setUp() {

        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;

        Response response = UserAPI.createUser(USER);

        response
                .then().statusCode(SC_OK).and()
                .assertThat().body("success", equalTo(true));

        accessToken = response.then().extract().path("accessToken").toString();

    }

    @Test
    @DisplayName("Login user with correct data, result ok")
    public void userLogInExpectOk() {

        Response response = UserAPI.logInUser(USER);

        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user.email", equalTo(EMAIL))
                .and()
                .body("user.name", equalTo(NAME))
                .and()
                .statusCode(SC_OK);

    }


    @Test
    @DisplayName("Login user with uncorrect email, result error")
    public void userLogInWithUncorrectEmailExpectError() {

        Response response = UserAPI.logInUser(new User("email@un.ru", PASSWORD, NAME));

        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Login user with uncorrect password, result error")
    public void userLogInWithUncorrectPasswordExpectError() {

        Response response = UserAPI.logInUser(new User(EMAIL, PASSWORD+"1", NAME));

        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Login user without email, result error")
    public void userLogInWithoutEmailExpectError() {

        Response response = UserAPI.logInUser(new User(null, PASSWORD, NAME));

        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Login user without password, result error")
    public void userLogInWithoutPasswordExpectError() {

        Response response = UserAPI.logInUser(new User(EMAIL, null, NAME));

        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }


    @AfterClass
    public static void deleteUser() {UserAPI.deleteUser(accessToken);

    }


}
