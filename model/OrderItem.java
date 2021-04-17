package model;

import lombok.Getter;
import lombok.Setter;

public class OrderItem {
    @Getter @Setter
    private int id, orderId, productId, amount;
}
