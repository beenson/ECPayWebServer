package model;

import Util.DateUtil;
import database.DBCon;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AnalysisRecord {

    public static final String  KEY_LOGINTIMES = "LOGINTIMES"; // 登入次數
    public static final String  KEY_REGISTERTIMES = "REGISTERTIMES"; // 註冊次數
    public static final String  KEY_EARNINGS = "EARNINGS"; // 營收額
    public static final String  KEY_EARNING_TYPE = "EARNING_"; // 營收額(分類)
    public static final String  KEY_SELL_TYPE = "SELL_"; // 銷量(分類)
    public static final String  KEY_PAYMENT_TYPE = "PAYMENT_"; // 銷量(分類) 不分日期

    @Getter @Setter
    private int id = 0, value = 0;
    @Getter @Setter
    private String key;
    @Getter @Setter
    private Date date;

    public AnalysisRecord(int id, String date, String key, int value) {
        this.id = id;
        this.date = new Date(date);
        this.key = key;
        this.value = value;
    }
    public AnalysisRecord(int id, Date date, String key, int value) {
        this.id = id;
        this.date = date;
        this.key = key;
        this.value = value;
    }
    public AnalysisRecord(Date date, String key) {
        this.id = -1;
        this.date = date;
        this.key = key;
    }

    public String toString() {
        return "id::" + id + " Date=" + DateUtil.getReadableTime(date) + " key=" + key + " value=" + value;
    }

    public void saveToDB() {
        try (Connection con = DBCon.getConnection()) {
            if (this.id == -1) {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("INSERT INTO analysisrecord (`key`, date, value) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(1, key);
                    ps.setDate(2, new java.sql.Date(date.getTime()));
                    ps.setInt(3, value);
                    ps.execute();

                    // 取得自動遞增的id
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            setId(generatedKeys.getInt(1));
                        } else {
                            throw new SQLException("Creating AnalysisRecord failed, no ID obtained.");
                        }
                    }
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    PreparedStatement ps;
                    ps = con.prepareStatement("UPDATE analysisrecord SET `key` = ?, date = ?, value = ? WHERE id = ?");
                    ps.setString(1, key);
                    ps.setDate(2, new java.sql.Date(date.getTime()));
                    ps.setInt(3, value);
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

    public void addValue() {
        this.addValue(1);
    }
    public void addValue(int value) {
        this.value += value;
        this.saveToDB();
    }

    // 紀錄登入
    public static void recordRegister() {
        AnalysisRecord record = getByTodayKey(KEY_REGISTERTIMES);
        if (record == null) {
            record = new AnalysisRecord(DateUtil.getToday(), KEY_REGISTERTIMES);
        }
        record.addValue();
    }

    // 紀錄登入
    public static void recordLogin() {
        AnalysisRecord record = getByTodayKey(KEY_LOGINTIMES);
        if (record == null) {
            record = new AnalysisRecord(DateUtil.getToday(), KEY_LOGINTIMES);
        }
        record.addValue();
    }

    // 紀錄訂單
    public static void recordOrderData(Order order) {
        AnalysisRecord earn, earn_type, payment, sell;
        payment = getPaymentType(order.getPayment().getType().name(), order.getPayment().getBank());
        payment.addValue();
        earn = getByTodayKey(KEY_EARNINGS);
        if (earn == null) {
            earn = new AnalysisRecord(DateUtil.getToday(), KEY_EARNINGS);
        }
        earn.addValue(order.getPrice());
        for(OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int category = product.getCategoryId();
            int price = 0;
            sell = getByTodayKey(KEY_SELL_TYPE + category);
            earn_type = getByTodayKey(KEY_EARNING_TYPE + category);
            if (sell == null) {
                sell = new AnalysisRecord(DateUtil.getToday(), KEY_SELL_TYPE + category);
            }
            if (earn_type == null) {
                sell = new AnalysisRecord(DateUtil.getToday(), KEY_EARNING_TYPE + category);
            }
            if (product != null) {
                price = product.getPrice();
            }
            sell.addValue(item.getAmount());
            earn_type.addValue(item.getAmount() * product.getPrice());
        }
    }

    public static AnalysisRecord getPaymentType(String type, String bank) {
        //CVS 或 ATM_[銀行]
        if (type.equals("ATM")) {
            type = type + "_" + bank;
        }
        AnalysisRecord record = getByDateKey(DateUtil.getDefaultDay(), KEY_PAYMENT_TYPE + type);
        if (record == null) {
            record = new AnalysisRecord(DateUtil.getDefaultDay(), KEY_PAYMENT_TYPE + type);
        }
        return record;
    }

    public static AnalysisRecord getByTodayKey(String key) {
        return getByDateKey(DateUtil.getToday(), key);
    }
    public static AnalysisRecord getByDateKey(Date date, String key) {
        DateUtil.standardDate(date);
        AnalysisRecord record = null;
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM analysisrecord WHERE `key` = ? AND date = ?");
            ps.setString(1, key);
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int value = rs.getInt("value");
                    record = new AnalysisRecord(id, date, key, value);
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
        return record;
    }

    public static ArrayList<AnalysisRecord> getListByTodayKey(String key) {
        return getListByDateKey(DateUtil.getToday(), key);
    }
    public static ArrayList<AnalysisRecord> getListByDateKey(Date date, String key) {
        DateUtil.standardDate(date);
        ArrayList<AnalysisRecord> records = new ArrayList<>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM analysisrecord WHERE `key` LIKE ? AND date = ?");
            ps.setString(1, "%" + key + "%");
            ps.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String _key = rs.getString("key");
                    Date d = rs.getDate("date");
                    int value = rs.getInt("value");
                    records.add(new AnalysisRecord(id, date, _key, value));
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
        return records;
    }
    public static ArrayList<AnalysisRecord> getByKey(String key, Date startDate, Date endDate) {
        DateUtil.standardDate(startDate);
        DateUtil.standardDate(endDate);
        ArrayList<AnalysisRecord> records = new ArrayList<>();
        try (Connection con = DBCon.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM analysisrecord WHERE date >= ? AND date <= ? AND `key` LIKE ?");
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            ps.setString(3, "%" + key + "%");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String _key = rs.getString("key");
                    Date date = rs.getDate("date");
                    int value = rs.getInt("value");
                    records.add(new AnalysisRecord(id, date, _key, value));
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
        return records;
    }
}
