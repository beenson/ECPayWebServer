package model;

import database.DBCon;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class OrderPayment {
    @Getter
    private static HashMap<Integer, OrderPayment> orderPayments = new HashMap<Integer, OrderPayment>();

    @Getter @Setter
    private int id, orderId;// id, 訂單編號
    @Getter @Setter
    private String orderNumber, bank, code;// 訂單編號(綠界), 銀行代碼, 繳費代碼|銀行帳號
    @Getter @Setter
    private PaymentStatus status;
    @Getter @Setter
    private PaymentType type;

    public OrderPayment(int id, int orderId, String orderNumber, String bank, String code, PaymentStatus status, PaymentType type) {
        this.id = id;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.bank = bank;
        this.code = code;
        this.status = status;
        this.type = type;
    }
    public OrderPayment(int id) {
        this.id = id;
        this.loadFromDB();
    }
    public OrderPayment() {
        this.id = -1;
    }

    public enum PaymentType {
        CVS(0),// 超商代碼
        ATM(1);
        @Getter private int value;
        PaymentType(int value) {
            this.value = value;
        }
        public static OrderPayment.PaymentType getByValue(int value){
            for(OrderPayment.PaymentType paymentType : OrderPayment.PaymentType.values()) {
                if (paymentType.value == value) {
                    return paymentType;
                }
            }
            return null;
        }
    }
    public enum PaymentStatus {
        created(0),
        purchased(1),
        expired(2);
        @Getter private int value;
        PaymentStatus(int value) {
            this.value = value;
        }
        public static OrderPayment.PaymentStatus getByValue(int value){
            for(OrderPayment.PaymentStatus paymentStatus : OrderPayment.PaymentStatus.values()) {
                if (paymentStatus.value == value) {
                    return paymentStatus;
                }
            }
            return null;
        }
    }

    public static void loadAllFromDB() {
        orderPayments.clear();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderpayments");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int orderId = rs.getInt("orderId");
                    String orderNumber = rs.getString("orderNumber");
                    String bank = rs.getString("bank");
                    String code = rs.getString("code");
                    PaymentStatus status = PaymentStatus.getByValue(rs.getInt("status"));
                    PaymentType type = PaymentType.getByValue(rs.getInt("type"));
                    OrderPayment order = new OrderPayment(id, orderId, orderNumber, bank, code, status, type);
                    orderPayments.put(id, order);
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
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderpayments WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int orderId = rs.getInt("orderId");
                    String orderNumber = rs.getString("orderNumber");
                    String bank = rs.getString("bank");
                    String code = rs.getString("code");
                    PaymentStatus status = PaymentStatus.getByValue(rs.getInt("status"));
                    PaymentType type = PaymentType.getByValue(rs.getInt("type"));
                    this.orderId = orderId;
                    this.orderNumber = orderNumber;
                    this.bank = bank;
                    this.code = code;
                    this.status = status;
                    this.type = type;
                    orderPayments.put(id, this);
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

    public static OrderPayment loadByOrderId(int oid) {
        OrderPayment payment = null;
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM orderpayments WHERE orderId = ?");
            ps.setInt(1, oid);
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int orderId = rs.getInt("orderId");
                    String orderNumber = rs.getString("orderNumber");
                    String bank = rs.getString("bank");
                    String code = rs.getString("code");
                    PaymentStatus status = PaymentStatus.getByValue(rs.getInt("status"));
                    PaymentType type = PaymentType.getByValue(rs.getInt("type"));
                    payment = new OrderPayment(id, orderId, orderNumber, bank, code, status, type);
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
        return payment;
    }

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO orderpayments (orderId, orderNumber, bank, code, status, type) VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, orderId);
                    ps.setString(2, orderNumber);
                    ps.setString(3, bank);
                    ps.setString(4, code);
                    ps.setInt(5, status.getValue());
                    ps.setInt(6, type.getValue());
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating orderPayment failed, no ID obtained.");
                        }
                    }
                    orderPayments.put(this.id, this);
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE orderpayments SET orderId = ?, orderNumber = ?, bank = ?, code = ?, status = ?, type = ? WHERE id = ?");
                    ps.setInt(1, orderId);
                    ps.setString(2, orderNumber);
                    ps.setString(3, bank);
                    ps.setString(4, code);
                    ps.setInt(5, status.getValue());
                    ps.setInt(6, type.getValue());
                    ps.setInt(7, id);
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
