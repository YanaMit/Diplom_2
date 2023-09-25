import io.restassured.response.Response;
import org.junit.*;
import user.RefreshToken;
import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;

public class LogoutUserTest {

    private static String accessToken;
    private static RefreshToken refreshToken;
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
        refreshToken = new RefreshToken(response.then().extract().path("refreshToken").toString());

    }

    @Test
    @DisplayName("Logout user, result ok")
    public void userLogOutExpectOk() {

        Response response = UserAPI.logOutUser(refreshToken);

        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .body("message", equalTo("Successful logout"))
                .and()
                .statusCode(SC_OK);

    }

    @After
    public void deleteUser() {
        UserAPI.deleteUser(accessToken);
    }

}
