package main;

import Config.Config;
import Util.FileUtil;
import handler.ecpay.EcpayPayment;
import model.User;


public class test {

    public static void main(String [] argv) {
        System.out.println("目前執行路徑" + FileUtil.getWorkingPath());
        System.out.println("========================================");
        System.out.println("正在載入設定");
        Config.loadSetting();

        //testECPay();
        EcpayServer.startServer();

    }

    public static void testECPay() {
        Thread th = new Thread(() -> {
            EcpayPayment.genAioCheckOutTEST(1000);
        });
        th.start();
    }

    public static void testCreateUser() {
        User usr = new User();
        usr.setAdmin(1);
        usr.setEmail("email2");
        usr.setName("name");
        usr.setPassword("pass");
        usr.setPhone("phone");
        usr.saveToDB();
        System.out.println(usr.toString());
    }


}
