import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserTest {
    public static Faker faker = new Faker();

    private static String accessToken;
    private static final String EMAIL = RandomStringUtils.randomNumeric(5) + faker.internet().emailAddress();
    private static final String PASSWORD = faker.internet().password();
    private static final String NAME = faker.name().firstName();

    private static final String EMAIL_NEW = RandomStringUtils.randomNumeric(5) + faker.internet().emailAddress();
    private static final String PASSWORD_NEW  = faker.internet().password();
    private static final String NAME_NEW  = faker.name().firstName();

    public static final User USER = new User(EMAIL, PASSWORD, NAME);
    public static final User CHANGED_USER = new User(EMAIL_NEW,PASSWORD_NEW,NAME_NEW);


    @BeforeClass
    public static void setUp() {

        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;

        Response responseCreate = UserAPI.createUser(USER);

        responseCreate
                .then().statusCode(SC_OK).and()
                .assertThat().body("success", equalTo(true));

        accessToken = responseCreate.then().extract().path("accessToken").toString();

    }

    @Test
    @DisplayName("Change authorized user data, result ok")
    public void changeAuthorizedUserExpectOk() {

        Response responseLogin = UserAPI.logInUser(USER);

        responseLogin.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        Response response = UserAPI.changeUser(CHANGED_USER, accessToken);

        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(EMAIL_NEW.toLowerCase()))
                .and()
                .body("user.name", equalTo(NAME_NEW))
                .and()
                .statusCode(SC_OK);

    }

    @Test
    @DisplayName("Change noauthorized user data, result ok")
    public void changeNoAuthorizedUserExpectError() {

        Response response = UserAPI.changeUser(CHANGED_USER, "");

        response.then().assertThat()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);

    }

    @After
    public void deleteUser() {
        UserAPI.deleteUser(accessToken);
    }

}
