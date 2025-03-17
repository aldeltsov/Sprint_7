package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;

import static praktikum.courier.Courier.BAD_LOGIN_REQUEST;
import static praktikum.courier.Courier.USER_NOT_FOUND;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginCourierTest {
    private Client client;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        client = new Client("eeefaiko", "12345");
        courier = new Courier();
        courier.createCourier(client);
    }

    @After
    public void cleanUp(){
        try {
            if (courierId != null){
                courier.deleteCourier(courierId).log().all();
            }
        } catch (Exception e) {
            System.out.println("Не удается удалить курьера");
        }
    }

    @Test
    @DisplayName("Успешный логин курьера со всеми данными")
    @Description("code 200 и id cоответствует значению в JSON")
    public void courierLoginAllParametersTest(){
        Client loginCourierDate = new Client(client.getLogin(),client.getPassword());
        ValidatableResponse response = courier.loginCourier(loginCourierDate);
        courierId = response.extract().jsonPath().getInt("id");

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(courierId));
    }

    @Test
    @DisplayName("Ошибка при логине с неверным паролем")
    @Description("code 404 'Учетная запись не найдена'")
    public void errorWithWrongPasswordTest(){
        Client loginCourierDate = new Client(client.getLogin(), "123456");
        ValidatableResponse response = courier.loginCourier(loginCourierDate);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo(USER_NOT_FOUND));
    }

    @Test
    @DisplayName("Ошибка при логине без ввода пароля")
    @Description("code 400 'Недостаточно данных для входа'")
    public void errorWithNoPasswordTest(){
        Client loginCourierDate = new Client(client.getLogin(), "");
        ValidatableResponse response = courier.loginCourier(loginCourierDate);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_LOGIN_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при логине без логина")
    @Description("code 400 'Недостаточно данных для входа'")
    public void errorWithNoLoginTest(){
        Client loginCourierDate = new Client("", client.getPassword());
        ValidatableResponse response = courier.loginCourier(loginCourierDate);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_LOGIN_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при логине несуществуещего курьера")
    @Description("code 404 'Учетная запись не найдена'")
    public void errorWithNonExistingLoginTest(){
        Client loginCourierDate = new Client("dsadsadasdas", "fdsafss");
        ValidatableResponse response = courier.loginCourier(loginCourierDate);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo(USER_NOT_FOUND));
    }

}
