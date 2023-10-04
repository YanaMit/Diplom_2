import com.github.javafaker.Faker;
import io.restassured.response.Response;
import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class CreateUserTest {
    public static Faker faker = new Faker();
    private String accessToken;
    private String email = RandomStringUtils.randomNumeric(5) + faker.internet().emailAddress();
    private String password = faker.internet().password();
    private String name = faker.name().firstName();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;
    }

    @Test
    @DisplayName("Create user, result ok")
    public void createUserExpectOk() {

        User user = new User(email, password, name);

        Response response = UserAPI.createUser(user);

                response.then().statusCode(SC_OK).and()
                .assertThat().body("success", equalTo(true))
                .and()
                .body("accessToken", notNullValue())
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("user.email", equalTo(email))
                .and()
                .body("user.name", equalTo(name));

        accessToken = response.then().extract().path("accessToken").toString();

    }

    @Test
    @DisplayName("Create user with the same data, result error")
    public void createSameUserExpectError() {

        User user = new User(email, password, name);

        Response response = UserAPI.createUser(user);

        response
                .then().statusCode(SC_OK);

        accessToken = response.then().extract().path("accessToken").toString();

        UserAPI.createUser(user)
                .then().statusCode(SC_FORBIDDEN).and()
                .assertThat().body("message", equalTo("User already exists"));

    }

    @After
    public void deleteUser() {
        UserAPI.deleteUser(accessToken);
    }

}


