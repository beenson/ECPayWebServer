package model;

import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        this.createAt = createAt;
        this.status = OrderStatus.getByValue(status);
        this.createAt =  createAt;
    }

    public String toString() {
        return "Order::" + id + " userId=" + userId + " price=" + price + " status=" + status + " createAt=" + createAt.toLocaleString();
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

    public static void saveAllToDB() {
        for (Order order : orders.values()) {
            order.saveToDB();
        }
    }

}
