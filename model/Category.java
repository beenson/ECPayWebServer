package model;

import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Category {

    @Getter @Setter
    public int id = 0, priority = 0;
    @Getter @Setter
    public String name = "";

    public Category(int id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public String toString() {
        return "Category::" + id + " name=" + name + " prioity=" + priority;
    }

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO category (name, priority) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, name);
                    ps.setInt(2, priority);
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating order failed, no ID obtained.");
                        }
                    }
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE category SET name = ?, priority = ? WHERE id = ?");
                    ps.setString(1, name);
                    ps.setInt(2, priority);
                    ps.setInt(3, id);
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
            ps = con.prepareStatement("DELETE FROM category WHERE id = ?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<Integer, Category> loadAllFromDB() {
        HashMap<Integer, Category> categories = new HashMap<>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int priority = rs.getInt("priority");
                    Category category = new Category(id, name, priority);
                    categories.put(id, category);
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
        return categories;
    }


    public static Category getById(String id) {
        int search;
        try{
            search = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return null;
        }
        if (search <= 0) {
            return null;
        }

        return Category.getById(search);
    }

    public static Category getById(int id) {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM category WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int priority = rs.getInt("priority");
                    return new Category(id, name, priority);
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
}
