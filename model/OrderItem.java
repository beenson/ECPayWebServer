package model;

import com.alibaba.fastjson.annotation.JSONField;
import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class OrderItem {
    @Getter
    private static HashMap<Integer, OrderItem> orderItems = new HashMap<Integer, OrderItem>();

    @Getter @Setter
    private int id, orderId, productId, amount;

    public OrderItem(int id) {
        this.id = id;
        this.loadFromDB();
    }
    public OrderItem(int id, int orderId, int productId, int amount) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
    }

    public OrderItem(int orderId, int productId, int amount) {
        this.id = -1;
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
    }

    @JSONField(serialize = false)
    public Product getProduct() {
        return Product.getById(this.productId);
    }

    public static void loadAllFromDB() {
        orderItems.clear();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderitems");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int orderId = rs.getInt("orderId");
                    int productId = rs.getInt("productId");
                    int amount = rs.getInt("amount");
                    OrderItem order = new OrderItem(id, orderId, productId, amount);
                    orderItems.put(id, order);
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

    public void loadFromDB() {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderitems WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    int productId = rs.getInt("productId");
                    int amount = rs.getInt("amount");
                    this.orderId = orderId;
                    this.productId = productId;
                    this.amount = amount;
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

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO orderitems (orderId, productId, amount) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, orderId);
                    ps.setInt(2, productId);
                    ps.setInt(3, amount);
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating orderitem failed, no ID obtained.");
                        }
                    }
                    orderItems.put(this.id, this);
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE orderitems SET orderId = ?, productId = ?, amount = ? WHERE id = ?");
                    ps.setInt(1, orderId);
                    ps.setInt(2, productId);
                    ps.setInt(3, amount);
                    ps.setInt(4, id);
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

    public static HashMap<Integer, OrderItem> loadAllByOrderId(int oid) {
        HashMap<Integer, OrderItem> list = new HashMap<Integer, OrderItem>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderitems WHERE orderId = ?");
            ps.setInt(1, oid);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int orderId = rs.getInt("orderId");
                    int productId = rs.getInt("productId");
                    int amount = rs.getInt("amount");
                    OrderItem order = new OrderItem(id, orderId, productId, amount);
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
}
