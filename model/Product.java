package model;

import lombok.Getter;
import lombok.Setter;

public class Product {

    @Getter @Setter
    private int id, price, sellAmount/*售出數量*/, storageAmount/*庫存數量*/;
    @Getter @Setter
    private String name, desc;
    @Getter @Setter
    private boolean onSell; // 是否正在販賣

}
