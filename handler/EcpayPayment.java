package handler;

import Config.EcpayConfig;
import Util.DateUtil;
import Util.FileUtil;
import database.DBCon;
import handler.payment.integration.domain.AioCheckOutATM;
import handler.payment.integration.domain.AioCheckOutCVS;
import handler.payment.integration.domain.AioCheckOutOneTime;
import handler.payment.integration.domain.InvoiceObj;
import handler.payment.integration.ecpayOperator.EcpayFunction;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.User;

public class EcpayPayment {

    public static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String ReturnURL = "";

    /*public static String getPayInfoCVV(String accName, String html) {
        String filepath = FileUtil.getWorkingDirectory() + File.separator + "ecpay/" + accName + ".html";
        FileUtil.deleteFile(filepath);
        FileUtil.logToFile(filepath, html);
        return EcpayConstants.LineBotURL + "/ecpay/" + accName + ".html";
    }*/

    public static String getPayInfoCVS(EcpayFunction.PaymentInfo pay) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-3s", "#b綠界訂單編號 :#r", pay.getOrderNumber() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "#b綠界商店名稱 :#r", "GameSetConstants.serverName" + "遊戲贊助\r\n"));
        sb.append(String.format("%-10s %-3s", "#b綠界商品明細 :#r", pay.getItemName() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "#b實際繳費金額 :#r", pay.getItemPrice() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "#b綠界付款方式 :#r", pay.getSubPayment() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "#b繳費截止日期 :#r", pay.getPaymentExpiryDate() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "#b超商繳費代碼 :#r", pay.getPaymentNo() + "\r\n"));
        return sb.toString();
    }

    public static String getPayInfoCVS_LINE(EcpayFunction.PaymentInfo pay) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-3s", "綠界訂單編號 :", pay.getOrderNumber() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "綠界商店名稱 :", "GameSetConstants.serverName" + "遊戲贊助\r\n"));
        sb.append(String.format("%-10s %-3s", "綠界商品明細 :", pay.getItemName() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "實際繳費金額 :", pay.getItemPrice() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "綠界付款方式 :", pay.getSubPayment() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "繳費截止日期 :", pay.getPaymentExpiryDate() + "\r\n"));
        sb.append(String.format("%-10s %-3s", "超商繳費代碼 :", pay.getPaymentNo() + "\r\n"));
        return sb.toString();
    }

    public static String getPayInfoATM(EcpayFunction.PaymentInfo pay) {
        StringBuilder sb = new StringBuilder();
        if (!pay.getOrderNumber().isEmpty()) {
            sb.append(String.format("%-10s %-3s", "#b綠界訂單編號 :#r", pay.getOrderNumber() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b綠界商店名稱 :#r", "GameSetConstants.serverName" + "遊戲贊助\r\n"));
            sb.append(String.format("%-10s %-3s", "#b綠界商品明細 :#r", pay.getItemName() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b綠界訂單金額 :#r", pay.getItemPrice() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b綠界付款方式 :#r", pay.getSubPayment() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b繳費截止日期 :#r", pay.getPaymentExpiryDate() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b繳費銀行代碼 :#r", pay.getBankCode() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b銀行繳費帳號 :#r", pay.getPaymentNo() + "\r\n"));
        } else {
            sb.append("#r發生錯誤，請聯繫管理員。");
        }

        return sb.toString();
    }

    public static String getPayInfoATM_LINE(EcpayFunction.PaymentInfo pay) {
        StringBuilder sb = new StringBuilder();
        if (!pay.getOrderNumber().isEmpty()) {
            sb.append(String.format("%-10s %-3s", "綠界訂單編號 :", pay.getOrderNumber() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "綠界商店名稱 :", "GameSetConstants.serverName" + "遊戲贊助\r\n"));
            sb.append(String.format("%-10s %-3s", "綠界商品明細 :", pay.getItemName() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "綠界訂單金額 :", pay.getItemPrice() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "綠界付款方式 :", pay.getSubPayment() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "繳費截止日期 :", pay.getPaymentExpiryDate() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "繳費銀行代碼 :", pay.getBankCode() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "銀行繳費帳號 :", pay.getPaymentNo() + "\r\n"));
        } else {
            sb.append("#r發生錯誤，請聯繫管理員。");
        }

        return sb.toString();
    }

    public static EcpayFunction.PaymentInfo genAioCheckOutTEST(int amount) {
        AioCheckOutCVS obj = new AioCheckOutCVS();
        obj.setMerchantTradeNo(Long.toString(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        try{
            obj.setTradeDesc("GameDonate安安".getBytes("UTF-8").toString());
        } catch (Exception ex) {

        }
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("CVS");
        EcpayFunction.PaymentInfo pay = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        return pay;
    }

    public static void SavePaymentToDB(User usr, EcpayFunction.PaymentInfo pay) {
        if (pay != null) {
            System.out.println("[開出贊助] 單號: " + pay.getOrderNumber() + " 時間: " + DateUtil.getReadableTime() + " 帳號編號: " + usr.getId() + " 贊助: " + pay.getItemPrice() + "元");
            try (Connection con = DBCon.getInstance().getCon()) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO ecpay_payment (order_number, merchant_name,item_name,item_price,SubPayment,payment_expiredate,payment_No,payment_status,accountId) VALUES (?,?,?,?,?,?,?,?,?)");
                ps.setString(1, pay.getOrderNumber());
                ps.setString(2, pay.getMerchantName());
                ps.setString(3, pay.getItemName());
                ps.setInt(4, pay.getItemPrice());
                ps.setString(5, pay.getSubPayment());
                ps.setString(6, pay.getPaymentExpiryDate());
                ps.setString(7, pay.getBankCode().isEmpty() ? pay.getPaymentNo() : pay.getBankCode() + " - " + pay.getPaymentNo());
                ps.setString(8, pay.getPaymentStatus());
                ps.setInt(9, usr.getId());
                ps.executeUpdate();
                ps.close();
            } catch (Exception ex) {
                FileUtil.log("EcpayPayment.txt", "SavePaymentToDB" + ex +"OrderNumber:" + pay.getOrderNumber() + " itemPrice:" + pay.getItemPrice() + " itemName:" + pay.getItemName() + " MerchantName:" + pay.getMerchantName() + " 帳號編號:" + usr.getId());
            }
        }

    }

    public static void UpdatePaymentDB(String OrderNumber, String customField1, String TradeAmt, String PaymentType, String RtnMsg) {
        int realTradeAmount, accountId = -1;
        try {
            realTradeAmount = Integer.parseInt(TradeAmt);
            accountId = Integer.parseInt(customField1);
        } catch (Exception ex) {
            realTradeAmount = 0;
            accountId = -1;
            FileUtil.log("EcpayPayment.txt", "UpdatePaymentDB\n"+ ex.getMessage() + "OrderNumber:" + OrderNumber + " customField1:" + customField1 + " TradeAmt:" + TradeAmt + " customField1:" + customField1);
            return;
        }

        try (Connection con = DBCon.getInstance().getCon()) {
            PreparedStatement ppss = con.prepareStatement("SELECT * FROM ecpay_payment WHERE order_number = " + OrderNumber);
            ResultSet rs = ppss.executeQuery();
            try {
                String s = "";
                if (rs.next()) {
                    s = rs.getString("payment_status");
                }
                if (s.equals("尚未繳款")) {
                    PreparedStatement ps = con.prepareStatement("UPDATE ecpay_payment SET payment_status = ? WHERE order_number = ?");
                    ps.setString(1, RtnMsg + "(" + DateUtil.getReadableTime() + ")");
                    ps.setString(2, OrderNumber);
                    ps.execute();
                    ps.close();
                    ps = con.prepareStatement("INSERT INTO donate (accountId, amount, paymentMethod, payTime, payDate) VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(1, accountId);
                    ps.setInt(2, realTradeAmount);
                    ps.setString(3, PaymentType);
                    ps.setLong(4, System.currentTimeMillis());
                    ps.setString(5, DateUtil.getReadableTime());
                    ps.execute();
                    ps.close();

                    ps = con.prepareStatement("UPDATE accounts SET `total_donate` = `total_donate` + ?, `today_donate` = `today_donate` + ?, `month_total_donate` = `month_total_donate` + ? WHERE accountId = ?");
                    ps.setInt(1, realTradeAmount);
                    ps.setInt(2, realTradeAmount);
                    ps.setInt(3, realTradeAmount);
                    ps.setInt(4, accountId);
                    ps.execute();
                    ps.close();

                    ps = con.prepareStatement("SELECT * FROM donate_point WHERE accountId = ?");
                    ps.setInt(1, accountId);
                    ResultSet rss = ps.executeQuery();

                    try {
                        PreparedStatement pps;
                        if (!rss.next()) {
                            pps = con.prepareStatement("INSERT INTO donate_point (accountId, `point`, LastAttempt) VALUES (?, ?, ?)");
                            pps.setInt(1, accountId);
                            pps.setInt(2, realTradeAmount);
                            pps.setString(3, DateUtil.getReadableTime());
                            pps.execute();
                            pps.close();
                        } else {
                            pps = con.prepareStatement("UPDATE donate_point SET `point` = `point` + ? , LastAttempt = ? WHERE accountId = ?");
                            pps.setInt(1, realTradeAmount);
                            pps.setString(2, DateUtil.getReadableTime());
                            pps.setInt(3, accountId);
                            pps.execute();
                            pps.close();
                        }
                        System.out.println("[贊助入帳] 單號: " + OrderNumber + " 時間: " + DateUtil.getReadableTime() + " 帳號編號: " + accountId + " 贊助: " + realTradeAmount + "元 付款方式: " + PaymentType);
                        FileUtil.log("日誌/紀錄/贊助紀錄.txt", "時間: " + DateUtil.getReadableTime() + " 帳號編號: " + accountId + " 贊助: " + realTradeAmount + "元,贊助點數已入帳\r\n");
                    } catch (Throwable var18) {
                        if (rss != null) {
                            try {
                                rss.close();
                            } catch (Throwable var16) {
                                var18.addSuppressed(var16);
                            }
                        }
                        throw var18;
                    }
                    if (rss != null) {
                        rss.close();
                    }
                }
            } catch (Throwable var19) {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Throwable var15) {
                        var19.addSuppressed(var15);
                    }
                }

                throw var19;
            }
            if (rs != null) {
                rs.close();
            }
            ppss.close();
        } catch (Exception ex) {
            System.out.println("UpdatePaymentDB:" + ex);
        }
    }

    public static EcpayFunction.PaymentInfo genAioCheckOutATM(final User usr, final int amount, final String subPayment, final int type) {
        AioCheckOutATM obj = new AioCheckOutATM();
        obj.setMerchantTradeNo(usr.getId() + String.valueOf(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        obj.setTradeDesc("ACCOUNT:" + usr.getEmail());
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setExpireDate("6");
        obj.setChooseSubPayment(subPayment);
        obj.setRemark("ACCOUNT : " + usr.getEmail());
        obj.setCustomField1(String.valueOf(usr.getId()));
        obj.setCustomField2(String.valueOf(type));
        EcpayFunction.PaymentInfo form = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        SavePaymentToDB(usr, form);
        return form;
    }

    public static List<EcpayFunction.PaymentInfo> getAllPaymentInfo(User usr) {
        LinkedList paylist = new LinkedList();

        try (Connection con = DBCon.getInstance().getCon()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ecpay_payment WHERE accountId = ? ORDER BY `ecpay_payment`.`payment_expiredate` DESC");
            ps.setInt(1, usr.getId());
            ResultSet rs = ps.executeQuery();
            for (int i = 0; rs.next() && i < 10; ++i) {
                EcpayFunction.PaymentInfo payment = new EcpayFunction.PaymentInfo(rs.getString("order_number"), rs.getString("merchant_name"), rs.getString("item_name"), rs.getInt("item_price"), rs.getString("SubPayment"), rs.getString("payment_expiredate"), rs.getString("payment_No"), rs.getString("payment_No"), rs.getString("payment_status"));
                paylist.add(payment);
            }
            ps.close();
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx);
        }
        return paylist;
    }

    public static EcpayFunction.PaymentInfo getPaymentInfo(String paymentNo) {
        EcpayFunction.PaymentInfo payment = null;
        if (!paymentNo.isEmpty()) {
            try (Connection con = DBCon.getInstance().getCon()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM ecpay_payment WHERE payment_No LIKE ?");
                ps.setString(1, paymentNo);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    payment = new EcpayFunction.PaymentInfo(rs.getString("order_number"), rs.getString("merchant_name"), rs.getString("item_name"), rs.getInt("item_price"), rs.getString("SubPayment"), rs.getString("payment_expiredate"), rs.getString("payment_No"), rs.getString("payment_No"), rs.getString("payment_status"));
                } else {
                    ps = con.prepareStatement("select * from ecpay_payment where order_number LIKE ?");
                    ps.setString(1, paymentNo);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        payment = new EcpayFunction.PaymentInfo(rs.getString("order_number"), rs.getString("merchant_name"), rs.getString("item_name"), rs.getInt("item_price"), rs.getString("SubPayment"), rs.getString("payment_expiredate"), rs.getString("payment_No"), rs.getString("payment_No"), rs.getString("payment_status"));
                    }
                }
                ps.close();
            } catch (Exception ex) {
                System.err.println("EcpayPayMent : " + ex.getLocalizedMessage());
            }
        }
        return payment;
    }

    public static String getAllPayInfoString(List<EcpayFunction.PaymentInfo> pay) {
        StringBuilder sb = new StringBuilder();
        if (pay.isEmpty()) {
            return "您沒有任何開單紀錄哦!";
        } else {
            int i = 0;
            Iterator<EcpayFunction.PaymentInfo> iter = pay.iterator();
            while (iter.hasNext()) {
                EcpayFunction.PaymentInfo payinfo = iter.next();
                ++i;
                sb.append("#d----------------------第").append(i).append("筆訂單----------------------\r\n");
                sb.append(String.format("%-10s %-3s", "#b綠界訂單編號 :#r", payinfo.getOrderNumber() + "\r\n"));
                sb.append(String.format("%-10s %-3s", "#b實際繳費金額 :#r", payinfo.getItemPrice() + "\r\n"));
                sb.append(String.format("%-10s %-3s", "#b綠界付款方式 :#r", payinfo.getSubPayment() + "\r\n"));
                sb.append(String.format("%-10s %-3s", "#b繳費截止日期 :#r", payinfo.getPaymentExpiryDate() + "\r\n"));
                sb.append(String.format("%-10s %-3s", "#b超商繳費代碼 :#r", payinfo.getPaymentNo() + "\r\n"));
                sb.append(String.format("%-10s %-3s", "#b目前繳費狀態 :#r", payinfo.getPaymentStatus() + "\r\n"));
            }

            return sb.toString();
        }
    }

    public static String getPayInfoString(EcpayFunction.PaymentInfo pay) {
        StringBuilder sb = new StringBuilder();
        if (pay == null) {
            return "沒有此筆訂單哦!請嚴防詐騙以及現金交易!";
        } else {
            sb.append("#d--------------------此筆訂單內容---------------------\r\n");
            sb.append(String.format("%-10s %-3s", "#b綠界訂單編號 :#r", pay.getOrderNumber() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b實際繳費金額 :#r", pay.getItemPrice() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b綠界付款方式 :#r", pay.getSubPayment() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b繳費截止日期 :#r", pay.getPaymentExpiryDate() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b超商繳費代碼 :#r", pay.getPaymentNo() + "\r\n"));
            sb.append(String.format("%-10s %-3s", "#b目前繳費狀態 :#r", pay.getPaymentStatus() + "\r\n"));
            return sb.toString();
        }
    }

    public static EcpayFunction.PaymentInfo genAioCheckOutCVS(final User usr, final int amount) {
        AioCheckOutCVS obj = new AioCheckOutCVS();
        obj.setMerchantTradeNo(usr.getId() + String.valueOf(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        obj.setTradeDesc("ACCOUNT:" + usr.getEmail());
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("CVS");
        obj.setRemark("ACCOUNT:" + usr.getEmail());
        obj.setCustomField1(String.valueOf(usr.getId()));
        //obj.setCustomField2(String.valueOf(type));
        EcpayFunction.PaymentInfo pay = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        SavePaymentToDB(usr, pay);
        return pay;
    }

    public static String genAioCheckOutCVV(final User usr, final int amount) {
        AioCheckOutOneTime obj = new AioCheckOutOneTime();
        String MerchantTradeNo = usr.getId() + String.valueOf(System.currentTimeMillis());
        obj.setMerchantTradeNo(MerchantTradeNo);
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        obj.setTradeDesc("ACCOUNT:" + usr.getEmail());
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("Credit");
        obj.setRemark("ACCOUNT:" + usr.getEmail());
        obj.setCustomField1(String.valueOf(usr.getId()));
        //obj.setCustomField2(String.valueOf(type));

        EcpayFunction.PaymentInfo payment = new EcpayFunction.PaymentInfo(MerchantTradeNo, "一次性信用卡", "伺服器贊助", amount, "無", "無", "無", "無", "尚未繳款");
        String url = AllInOne.aioCheckOutHtml(obj, (InvoiceObj) null);
        SavePaymentToDB(usr, payment);
        return url;
    }

    public static boolean compareCheckMacValue(final String params) {
        String checkMacValue = "";
        try {
            checkMacValue = genCheckMacValue(params);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return checkMacValue.equals("7656C385D577DF0B8598408BE614CF904F905988BF97D6B4257F13DB907C3762");
    }

    public static boolean checkMacValue(String s) {
        Hashtable params = new Hashtable();
        boolean ret;
        try {
            String[] para = s.substring(s.lastIndexOf("\n")).trim().split("&");
            s = s.substring(s.lastIndexOf("\n")).trim();
            for (int i = 0; i < para.length; ++i) {
                String para_ = para[i];
                String key = para_.substring(0, para_.indexOf("="));
                String value = para_.substring(para_.indexOf("=")).replace("=", "");
                params.put(key, value);
            }
            ret = compareCheckMacValue(params);
            if (ret && ((String) params.get("RtnCode")).equals("1")) {
                UpdatePaymentDB((String) params.get("MerchantTradeNo"), (String) params.get("CustomField1"), (String) params.get("TradeAmt"), (String) params.get("PaymentType"), (String) params.get("RtnMsg"));
            } else {
                System.out.println("驗證失敗! 交易單號:" + params.get("MerchantTradeNo") + " 交易型態:" + (String) params.get("PaymentType") + " RtnMsg:" + (String) params.get("RtnMsg") + " CustomField1(玩家帳號):" + (String) params.get("CustomField1"));
            }
        } catch (Exception ex) {
            FileUtil.log("EcpayPayment.txt", "CheckMacValue驗證結果異常 checkMacValue\n" + ex.getMessage() + "params: " + params);
            ret = false;
        }

        return ret;
    }

    public static boolean compareCheckMacValue(final Hashtable<String, String> params) {
        String checkMacValue = "";
        if (!params.containsKey("CheckMacValue")) {
            System.out.println("compareCheckMacValue: params without CheckMacValue");
            return false;
        } else {
            try {
                checkMacValue = genCheckMacValue(params);
            } catch (Exception ex) {
                System.out.println(ex);
            }

            return checkMacValue.equals(params.get("CheckMacValue"));
        }
    }

    public static final String genCheckMacValue(final Hashtable<String, String> params) {
        Set<String> keySet = params.keySet();
        TreeSet<String> treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        treeSet.addAll(keySet);
        String[] name = (String[]) treeSet.toArray(new String[treeSet.size()]);
        String paramStr = "";

        for (int i = 0; i < name.length; ++i) {
            if (!name[i].equals("CheckMacValue")) {
                paramStr = paramStr + "&" + name[i] + "=" + (String) params.get(name[i]);
            }
        }
        String par = "Hashkey=" + EcpayConfig.getHashKey() + paramStr + "&HashIV=" + EcpayConfig.getHashIV();

        String urlEncode = urlEncode(par).toLowerCase();
        urlEncode = netUrlEncode(urlEncode);
        return hash(urlEncode.getBytes(), "SHA-256");
    }

    public static final String genCheckMacValue(final String params) {
        String urlEncode = urlEncode("Hashkey=" + EcpayConfig.getHashKey() + "&" + params + "&HashIV=" + EcpayConfig.getHashIV()).toLowerCase();
        System.out.println("paramStr: " + params.toString());
        urlEncode = netUrlEncode(urlEncode);
        return hash(urlEncode.getBytes(), "SHA-256");
    }

    public static String urlEncode(String data) {
        String result = "";
        try {
            result = URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    private static String netUrlEncode(String url) {
        String netUrlEncode = url.replaceAll("%21", "\\!").replaceAll("%28", "\\(").replaceAll("%29", "\\)");
        return netUrlEncode;
    }

    private static final String hash(byte[] data, String mode) {
        MessageDigest md = null;
        try {
            if ("MD5".equals(mode)) {
                md = MessageDigest.getInstance("MD5");
            } else if ("SHA-256".equals(mode)) {
                md = MessageDigest.getInstance("SHA-256");
            }
        } catch (NoSuchAlgorithmException ex) {
        }
        return bytesToHex(md.digest(data));
    }

    private static final String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    static {
        ReturnURL = "http://" + EcpayConfig.ECPayIp + ":" + EcpayConfig.ECPayPort;
    }
}
