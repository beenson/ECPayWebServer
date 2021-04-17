package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Order {

    @Getter @Setter
    private int id, userId, price;
    @Getter @Setter
    private Date createAt;
    @Getter @Setter
    private OrderStatus status;

    public enum OrderStatus{
        createOrder(0),
        createPayment(1),
        purchased(2),
        expire(3),
        ;

        @Getter private int value;
        OrderStatus(int value) {
            this.value = value;
        }
    }

}
