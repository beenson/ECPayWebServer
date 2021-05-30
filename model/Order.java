package model;

import Util.DateUtil;
import com.alibaba.fastjson.annotation.JSONField;
import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class Order {

    @Getter
    private static HashMap<Integer, Order> orders = new HashMap<Integer, Order>();

    @Getter @Setter
    private int id, userId, price;
    @Getter @Setter
    private Date createAt;
    @Getter @Setter
    private OrderStatus status;

    public Order(int id, int userId, int price, int status, Date createAt) {
        this.id = id;
        this.userId = userId;
        this.price = price;
        this.status = OrderStatus.getByValue(status);
        this.createAt =  createAt;
    }

    public Order(int userId) {
        this.id = -1;
        this.userId = userId;
        this.status = OrderStatus.createOrder;
        this.price = 0;
        this.createAt = DateUtil.getDateWithAddTime(0);
    }

    public String toString() {
        return "Order::" + id + " userId=" + userId + " price=" + price + " status=" + status + " createAt=" + createAt.toLocaleString();
    }

    @JSONField(serialize = false)
    public Collection<OrderItem> getOrderItems() {
        return OrderItem.loadAllByOrderId(this.id).values();
    }

    @JSONField(serialize = false)
    public OrderPayment getPayment() {
        return OrderPayment.loadByOrderId(this.id);
    }

    public void calculatePrice() {
        int value = 0;
        Collection<OrderItem> items = this.getOrderItems();
        for(OrderItem item : items) {
            Product prod = item.getProduct();
            if (prod == null) {
                continue;
            }
            value += prod.getPrice() * item.getAmount();
        }
        this.price = value;
        this.saveToDB();
    }

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
        public static OrderStatus getByValue(int value){
            for(OrderStatus orderStatus : OrderStatus.values()) {
                if (orderStatus.value == value) {
                    return orderStatus;
                }
            }
            return null;
        }
    }

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO orders (userId, price, status, createAt) VALUES (?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, userId);
                    ps.setInt(2, price);
                    ps.setInt(3, status.value);
                    ps.setDate(4, new java.sql.Date(createAt.getTime()));
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                    orders.put(this.id, this);
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE orders SET userId = ?, price = ?, status = ?, createAt = ? WHERE id = ?");
                    ps.setInt(1, userId);
                    ps.setInt(2, price);
                    ps.setInt(3, status.value);
                    ps.setDate(4, new java.sql.Date(createAt.getTime()));
                    ps.setInt(5, id);
                    ps.execute();
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void loadAllFromDB() {
        orders.clear();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orders");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("userId");
                    int price = rs.getInt("price");
                    int status = rs.getInt("status");
                    Date date = rs.getDate("createAt");
                    Order order = new Order(id, userId, price, status, date);
                    orders.put(id, order);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<Integer, Order> loadAllByUserId(int uid) {
        HashMap<Integer, Order> list = new HashMap<Integer, Order>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE userId = ?");
            ps.setInt(1, uid);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("userId");
                    int price = rs.getInt("price");
                    int status = rs.getInt("status");
                    Date date = rs.getDate("createAt");
                    Order order = new Order(id, userId, price, status, date);
                    list.put(id, order);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public static Order loadById(int orderId) {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orders WHERE id = ?");
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("userId");
                    int price = rs.getInt("price");
                    int status = rs.getInt("status");
                    Date date = rs.getDate("createAt");
                    Order order = new Order(id, userId, price, status, date);
                    return order;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveAllToDB() {
        for (Order order : orders.values()) {
            order.saveToDB();
        }
    }

}
