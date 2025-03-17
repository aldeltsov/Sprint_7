package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;

import static praktikum.courier.Courier.BAD_COURIER_REQUEST;
import static praktikum.courier.Courier.ERROR_COURIER_CONFLICT;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {
    private Client client;
    private Courier courier;

    @Before
    public void setUp() {
        courier = new Courier();
    }

    @After
    public void cleanUp(){
        try {
            Client loginCourierDate = new Client(client.getLogin(),client.getPassword());
            Integer courierId  = courier.loginCourier(loginCourierDate).log().all().extract().jsonPath().getInt("id");
            if (courierId != null){
                courier.deleteCourier(courierId).log().all();
            }
        } catch (Exception e) {
            System.out.println("Не удается удалить курьера");
        }
    }

    @Test
    @DisplayName("Создание курьера со всеми параметрами")
    @Description("code 201 и boolean true")
    public void courierCanBeCreatedAllParametersTest(){
        client = new Client("Faik", "123456","Факиров");
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Создание курьера с обязательными параметрами")
    @Description("code 201 и boolean true")
    public void courierCanBeCreatedWithRequiredParametersTest(){
        client = new Client("Qwertyu12", "Qwertyu12");
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка при создании уже существующего курьера")
    @Description("code 409 message 'Этот логин уже используется'")
    public void errorWhenTheCourierAlreadyExistsTest(){
        client = new Client("Qwertyu12", "Qwertyu12");
        courier.createCourier(client);
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", equalTo(ERROR_COURIER_CONFLICT));
    }

    @Test
    @DisplayName("Ошибка при создании курьера только с логином")
    @Description("code 400 message 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyLoginFieldTest(){
        client = new Client("Qwertyu13","");
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }
    @Test
    @DisplayName("Ошибка при создании курьера только с паролем")
    @Description("code 400 message 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyPasswordFieldTest(){
        client = new Client("","Qwertyu13");
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }

    @Test
    @DisplayName("Ошибка при создании курьера только с необязательным полем")
    @Description("code 400 message 'Недостаточно данных для создания учетной записи'")
    public void errorWithOnlyUnnecessaryInputTest(){
        client = new Client("", "","Факиров");
        ValidatableResponse response = courier.createCourier(client);
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo(BAD_COURIER_REQUEST));
    }
}
