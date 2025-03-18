import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.Client;
import praktikum.Order;
import praktikum.courier.Courier;
import praktikum.orders.OrderClient;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private int orderId;
    private int courierId;
    private int orderTrackNumber;

    private Courier courier = new Courier();
    private OrderClient orderClient = new OrderClient();

    private String firstName;
    private String lastName;
    private String address;
    private int metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public CreateOrderTest(String firstName, String lastName, String address, int metroStation, String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Before
    public void setUp() {
        Client client = new Client("faikfedos", "12345");

        courier.createCourier(client);
        ValidatableResponse courierResponse = courier.loginCourier(client);

        courierId = courierResponse.log().all().extract().jsonPath().getInt("id");
    }

    @After
    public void cleanUp() {
        ValidatableResponse orderResponse = orderClient.getOrderIdByTrackNumber(orderTrackNumber);
        orderId = orderResponse.log().all().extract().jsonPath().getInt("order.id");
        ValidatableResponse acceptResponse = orderClient.acceptOrder(courierId,orderId);
        acceptResponse.log().all();

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

    @Parameterized.Parameters(name = "Тестовые данные: firstName = {0}, lastName = {1}, address = {2}, metroStation = {3}, phone = {4}, rentTime = {5}, deliveryDate = {6}, comment = {7}, color = {8}")
    public static Object[][] getDate(){
        return new Object[][]{
                {"ваыпывп", "ывпывп", "пывпывп", 2, "423424234432", 4, "2020-06-21T21:00:00.000Z", "ываимм", new String[]{"BLACK"}},
                {"вфцфвц", "вфцвфцв", "вфцвфцвфц", 4, "1441412414", 4, "2020-06-08T21:00:00.000Z", "вфцвфцвфцв", new String[]{"BLACK", "GREY"}},
                {"Фаик", "Фаиков", "мск", 3, "12354543554", 2, "2025-06-08T21:00:00.000Z", "ацулдьюд", new String[]{}}
        };
    }

    @Test
    @DisplayName("Успешное создание заказа с цветом, с двумя и без цвета")
    @Description("Код 201 track")
    public void orderCanBeCreatedAllParametersTest() {
        Order order = new Order( firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        orderClient = new OrderClient();

        ValidatableResponse response = orderClient.createOrder(order);

        orderTrackNumber = response.extract().jsonPath().getInt("track");
        System.out.println("Трек номер: " + orderTrackNumber);

        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("track", equalTo(orderTrackNumber));
    }
}
