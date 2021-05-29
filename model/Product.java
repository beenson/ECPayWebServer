package model;

import com.alibaba.fastjson.annotation.JSONField;
import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Product {

    @Getter
    private static HashMap<Integer, Product> products = new HashMap<Integer, Product>();

    @Getter @Setter
    private int id, price, sellAmount/*售出數量*/, storageAmount/*庫存數量*/;
    @Getter @Setter
    private String name, desc, photo;// 名稱 描述 圖片網址
    @Getter @Setter
    private boolean onSell; // 是否正在販賣
    @Getter @Setter
    private int categoryId;//分類

    public Product(int id, String name, int price, String desc, int sellAmount, int storageAmount, boolean onSell, String photo, int categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.sellAmount = sellAmount;
        this.storageAmount = storageAmount;
        this.onSell = onSell;
        this.photo = photo;
        this.categoryId = categoryId;
    }

    public String toString() {
        return "Product::" + id + " name=" + name + " price=" + price + " desc=" + desc + " sellAmount=" + sellAmount + " storageAmount=" + storageAmount + " onSell=" + onSell + " photo=" + photo + " categoryId=" + categoryId;
    }

    @JSONField(serialize = false)
    public Category getCategory() {
        return Category.getById(this.categoryId);
    }

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO products (name, price, desciption, sellAmount, storageAmount, onSell, photo, categoryId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setInt(2, price);
                    ps.setString(3, desc);
                    ps.setInt(4, sellAmount);
                    ps.setInt(5, storageAmount);
                    ps.setString(7, photo);
                    ps.setInt(6, onSell?1:0);
                    ps.setInt(8, categoryId);
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating product failed, no ID obtained.");
                        }
                    }
                    products.put(this.id, this);
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE products SET name = ?, price = ?, desciption = ?, sellAmount = ?, storageAmount = ?, onSell = ?, photo = ?, categoryId = ? WHERE id = ?");
                    ps.setString(1, name);
                    ps.setInt(2, price);
                    ps.setString(3, desc);
                    ps.setInt(4, sellAmount);
                    ps.setInt(5, storageAmount);
                    ps.setInt(6, onSell?1:0);
                    ps.setString(7, photo);
                    ps.setInt(8, categoryId);
                    ps.setInt(9, id);
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

    public void deleteFromDB() {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps;
            ps = con.prepareStatement("DELETE FROM products WHERE id = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Product getById(String id) {
        int search;
        try{
            search = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return null;
        }
        if (search <= 0) {
            return null;
        }

        return Product.getById(search);
    }

    public static Product getById(int id) {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    String desc = rs.getString("desciption");
                    int sellAmount = rs.getInt("sellAmount");
                    int storageAmount = rs.getInt("storageAmount");
                    boolean onSell = rs.getInt("onSell") == 1;
                    String photo = rs.getString("photo");
                    int categoryId = rs.getInt("categoryId");
                    return new Product(id, name, price, desc, sellAmount, storageAmount, onSell, photo, categoryId);
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

    public static HashMap<Integer, Product> loadAllFromDB() {
        products.clear();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM products");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    String desc = rs.getString("desciption");
                    int sellAmount = rs.getInt("sellAmount");
                    int storageAmount = rs.getInt("storageAmount");
                    boolean onSell = rs.getInt("onSell") == 1;
                    String photo = rs.getString("photo");
                    int categoryId =  rs.getInt("categoryId");
                    Product prod = new Product(id, name, price, desc, sellAmount, storageAmount, onSell, photo, categoryId);
                    products.put(id, prod);
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
        return products;
    }

    public static void saveAllToDB() {
        for (Product product : products.values()) {
            product.saveToDB();
        }
    }

}
