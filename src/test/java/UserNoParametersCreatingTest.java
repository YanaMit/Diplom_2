import user.User;
import user.UserAPI;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;

import static org.hamcrest.CoreMatchers.equalTo;


public class UserNoParametersCreatingTest {

    String email = "email" + RandomStringUtils.randomNumeric(3) + "@yandex.ru";
    String password = "password";
    String name = "name";

    @Before
    public void setUp() {

        RestAssured.baseURI = BaseURI.BURGER_SERVICE_URI;
    }

    @Test
    @DisplayName("Create user, no email, result error")
    public void createUserWithoutEmailExpectError() {

        User user = new User(null, password, name);

        UserAPI.createUser(user)
                .then().statusCode(SC_FORBIDDEN).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Create user, no password, result error")
    public void createUserWithoutPasswordExpectError() {

        User user = new User(email, null, name);

        UserAPI.createUser(user)
                .then().statusCode(SC_FORBIDDEN).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Create user, no name, result error")
    public void createUserWithoutNameExpectError() {

        User user = new User(email, password, null);

        UserAPI.createUser(user)
                .then().statusCode(SC_FORBIDDEN).and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));

    }


}
