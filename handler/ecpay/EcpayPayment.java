package handler.ecpay;

import Config.EcpayConfig;
import Util.DateUtil;
import Util.FileUtil;
import database.DBCon;
import handler.ecpay.payment.domain.AioCheckOutATM;
import handler.ecpay.payment.domain.AioCheckOutCVS;
import handler.ecpay.payment.domain.AioCheckOutOneTime;
import handler.ecpay.payment.domain.InvoiceObj;
import handler.ecpay.payment.ecpayOperator.EcpayFunction;

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
            obj.setTradeDesc("安安".getBytes("UTF-8").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("CVS");
        EcpayFunction.PaymentInfo pay = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        return pay;
    }

    public static EcpayFunction.PaymentInfo genAioCheckOutCVS(int amount) {
        AioCheckOutCVS obj = new AioCheckOutCVS();
        obj.setMerchantTradeNo(Long.toString(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        try{
            obj.setTradeDesc("安安".getBytes("UTF-8").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        obj.setItemName("SHOPPING");
        obj.setReturnURL(ReturnURL);
        //obj.setPaymentInfoURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("CVS");
        EcpayFunction.PaymentInfo pay = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        return pay;
    }

    public static EcpayFunction.PaymentInfo genAioCheckOutATM(final int amount, final String subPayment) {
        AioCheckOutATM obj = new AioCheckOutATM();
        obj.setMerchantTradeNo(Long.toString(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        try{
            obj.setTradeDesc("安安".getBytes("UTF-8").toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        obj.setItemName("SHOPPING");
        obj.setReturnURL(ReturnURL);
        System.out.println("ReturnURL=" + ReturnURL);
        //obj.setPaymentInfoURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setExpireDate("6");
        obj.setChooseSubPayment(subPayment);
        EcpayFunction.PaymentInfo form = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
        return form;
    }

    public static List<EcpayFunction.PaymentInfo> getAllPaymentInfo(User usr) {
        LinkedList paylist = new LinkedList();

        try (Connection con = DBCon.getConnection()) {
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
            try (Connection con = DBCon.getConnection()) {
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

    public static EcpayFunction.PaymentInfo genAioCheckOutCVS(final User usr, final int amount) {
        AioCheckOutCVS obj = new AioCheckOutCVS();
        obj.setMerchantTradeNo(usr.getId() + String.valueOf(System.currentTimeMillis()));
        obj.setMerchantTradeDate(DateUtil.getDatabaseFormatTime());
        obj.setTotalAmount(amount);
        obj.setTradeDesc("ACCOUNT:" + usr.getEmail());
        obj.setItemName("GAME DONATE");
        obj.setReturnURL(ReturnURL);
        obj.setPaymentInfoURL(ReturnURL);
        obj.setNeedExtraPaidInfo("N");
        obj.setChooseSubPayment("CVS");
        obj.setRemark("ACCOUNT:" + usr.getEmail());
        obj.setCustomField1(String.valueOf(usr.getId()));
        //obj.setCustomField2(String.valueOf(type));
        EcpayFunction.PaymentInfo pay = AllInOne.aioCheckOut(obj, (InvoiceObj) null);
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
                System.out.println("驗證成功! 交易單號:" + params.get("MerchantTradeNo") + " 交易型態:" + (String) params.get("PaymentType") + " RtnMsg:" + (String) params.get("RtnMsg") + " CustomField1(玩家帳號):" + (String) params.get("CustomField1"));
                //UpdatePaymentDB((String) params.get("MerchantTradeNo"), (String) params.get("CustomField1"), (String) params.get("TradeAmt"), (String) params.get("PaymentType"), (String) params.get("RtnMsg"));
            } else {
                System.out.println("驗證失敗! 交易單號:" + params.get("MerchantTradeNo") + " 交易型態:" + (String) params.get("PaymentType") + " RtnMsg:" + (String) params.get("RtnMsg") + " CustomField1:" + (String) params.get("CustomField1"));
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
