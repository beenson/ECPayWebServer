package model;

import Util.DateUtil;
import Util.FileUtil;
import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    @Getter @Setter
    private int id, admin;
    @Getter @Setter
    private String name, email, password, phone;

    public User() {
        this.id = -1;
    }

    public User(int id) {
        this.id = id;
        this.loadFromDB();
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
        try (Connection con = DBCon.getInstance().getCon()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user WHERE id = " + this.id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    this.id = rs.getInt("id");
                    this.admin = rs.getInt("admin");
                    this.name = rs.getString("name");
                    this.email = rs.getString("email");
                    this.password = rs.getString("password");
                    this.phone = rs.getString("phone");
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
        try (Connection con = DBCon.getInstance().getCon()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO user (admin, name, email, password, phone) VALUES (?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
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
                    ps = con.prepareStatement("UPDATE user SET admin = ?, name = ?, email = ?, password = ?, phone = ?  WHERE id = ?");
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

}
