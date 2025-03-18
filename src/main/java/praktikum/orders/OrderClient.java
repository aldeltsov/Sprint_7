package praktikum.orders;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.EnvConfig;
import praktikum.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends EnvConfig {
    public static final String CREATE_ORDER_URI = "/api/v1/orders"; // Orders - Создание заказа
    public static final String GET_ORDER_ID_BY_TRACK_NUMBER_URI = "/api/v1/orders/track"; // Orders - Получить заказ по его номеру
    public static final String ACCEPT_ORDER_URI = "/api/v1/orders/accept/{order}"; // Orders - Принять заказ
    public static final String FINISH_ORDER_URI = "/api/v1/orders/finish/{id}"; // Orders - Завершить заказ
    public static final String GET_ORDER_LIST = "/api/v1/orders"; // Orders - Получение списка заказов

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order){
        return given()
                .spec(requestSpecification())
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Получить заказ по его номеру")
    public ValidatableResponse getOrderIdByTrackNumber(int trackNumber){
        return given()
                .spec(requestSpecification())
                .queryParam("t", trackNumber)
                .when()
                .get(GET_ORDER_ID_BY_TRACK_NUMBER_URI)
                .then();
    }

    @Step("Принять заказ")
    public ValidatableResponse acceptOrder(int courierId, int orderId){
        return given()
                .spec(requestSpecification())
                .queryParam("courierId", courierId)
                .pathParams("order",orderId)
                .when()
                .put(ACCEPT_ORDER_URI)
                .then();
    }

    @Step("Завершить заказ")
    public ValidatableResponse finishOrderResponse(int orderId){
        return given()
                .spec(requestSpecification())
                .pathParams("id",orderId)
                .when()
                .put(FINISH_ORDER_URI)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrderlist(){
        return given()
                .spec(requestSpecification())
                .when()
                .get(GET_ORDER_LIST)
                .then();
    }
}
