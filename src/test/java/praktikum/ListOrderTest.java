package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.courier.Courier;
import praktikum.orders.OrderClient;

import static org.hamcrest.CoreMatchers.notNullValue;

public class ListOrderTest {
    Integer courierId;
    Integer orderTrackNumber;
    Integer orderId;

    Courier courier = new Courier();
    OrderClient orderClient = new OrderClient();
    Order order;

    @Before
    public void setUp() {
        Client client = new Client("eeefaiko", "12345");
        courier.createCourier(client);

        ValidatableResponse courierResponse = courier.loginCourier(client);
        courierId = courierResponse.extract().jsonPath().getInt("id");

        order = new Order("ваыпывп", "ывпывп", "пывпывп", 2, "423424234432", 4, "2020-06-21T21:00:00.000Z", "ываимм", new String[]{"BLACK"});
        ValidatableResponse response = orderClient.createOrder(order);

        orderTrackNumber = response.extract().jsonPath().getInt("track");
        ValidatableResponse orderResponse = orderClient.getOrderIdByTrackNumber(orderTrackNumber);

        orderId = orderResponse.extract().jsonPath().getInt("order.id");
        ValidatableResponse acceptResponse = orderClient.acceptOrder(courierId,orderId);
        acceptResponse.log().all();
    }

    @After
    public void cleanUp() {
        try {
            ValidatableResponse finishOrderResponse = orderClient.finishOrderResponse(orderId);
            finishOrderResponse.log().all();
        } catch (Exception e) {
            System.out.println("Не удается завершить заказ");
        }

        try {
            ValidatableResponse deleteCourierResponse = courier.deleteCourier(courierId);
            deleteCourierResponse.log().all();
        } catch (Exception e) {
            System.out.println("Не удается удалить курьера");
        }
    }

    @Test
    @DisplayName("Получение списка всех заказов")
    @Description("code 200 not null")
    public void getOrderListTest() {

        ValidatableResponse orderListResponse = orderClient.getOrderlist();

        orderListResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", notNullValue());
    }
}
