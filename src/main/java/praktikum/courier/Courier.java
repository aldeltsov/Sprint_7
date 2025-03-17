package praktikum.courier;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;
import praktikum.EnvConfig;

import static io.restassured.RestAssured.given;

public class Courier extends EnvConfig {
    public static final String CREATE_COURIER_URI = "/api/v1/courier"; //Courier - Создание курьера
    public static final String LOGIN_COURIER_URI = "/api/v1/courier/login"; //Courier - Логин курьера в системе
    public static final String DELETE_COURIER_URI = "/api/v1/courier/{id}"; //Courier - Удаление курьера
    public static final String ERROR_COURIER_CONFLICT = "Этот логин уже используется";
    public static final String BAD_COURIER_REQUEST = "Недостаточно данных для создания учетной записи";
    public static final String BAD_LOGIN_REQUEST = "Недостаточно данных для входа";
    public static final String USER_NOT_FOUND = "Учетная запись не найдена";

    @Step("Создание курьера")
    public ValidatableResponse createCourier(Client courier){
        return given()
                .spec(requestSpecification())
                .and()
                .body(courier)
                .when()
                .post(CREATE_COURIER_URI)
                .then();
    }

    @Step("Логин курьера в системе")
    public ValidatableResponse loginCourier(Client courier){
        return given()
                .spec(requestSpecification())
                .and()
                .body(courier)
                .when()
                .post(LOGIN_COURIER_URI)
                .then();
    }

    @Step("Удаление курьера")
    public ValidatableResponse deleteCourier(Integer courierId){
        return given()
                .spec(requestSpecification())
                .pathParams("id",courierId)
                .when()
                .delete(DELETE_COURIER_URI)
                .then();
    }
}