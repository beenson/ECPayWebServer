package model;

import Util.DateUtil;
import Util.FileUtil;
import com.alibaba.fastjson.annotation.JSONField;
import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {

    @Getter @Setter
    private int id, admin = 0;
    @Getter @Setter
    private String name, email, phone;
    @JSONField(serialize = false)
    @Getter @Setter
    private String password;

    public User() {
        this.id = -1;
    }

    public User(int id) {
        this.id = id;
        if (id > 0) {
            this.loadFromDB();
        }
    }

    public User(int id, int admin, String name, String email, String password, String phone) {
        this.id = id;
        this.admin = admin;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String toString () {
        return "User::" + id + " admin=" + admin + " name=" + name + " email=" + email + " password=" + password + " phone=" + phone;
    }

    public void loadFromDB() {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE id = " + this.id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.admin = rs.getInt("admin");
                    this.name = rs.getString("name");
                    this.email = rs.getString("email");
                    this.password = rs.getString("password");
                    this.phone = rs.getString("phone");
                } else {
                    this.id = -1;
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
                    ps = con.prepareStatement("INSERT INTO users (admin, name, email, password, phone) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, admin);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setString(4, password);
                    ps.setString(5, phone);
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE users SET admin = ?, name = ?, email = ?, password = ?, phone = ?  WHERE id = ?");
                    ps.setInt(1, admin);
                    ps.setString(2, name);
                    ps.setString(3, email);
                    ps.setString(4, password);
                    ps.setString(5, phone);
                    ps.setInt(6, id);
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


    public static User getByEmail(String email) {
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int admin = rs.getInt("admin");
                    String name = rs.getString("name");
                    String mail = rs.getString("email");
                    String password = rs.getString("password");
                    String phone = rs.getString("phone");
                    return new User(id, admin, name, mail, password, phone);
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


    public static ArrayList<User> loadAllFromDB() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users ");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int admin = rs.getInt("admin");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String phone = rs.getString("phone");
                    User usr = new User(id, admin, name, email, password, phone);
                    users.add(usr);
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
        return users;
    }
}
