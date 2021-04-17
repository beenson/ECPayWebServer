package main;

import Config.Config;
import Util.FileUtil;
import handler.ecpay.EcpayPayment;
import model.Product;
import model.User;


public class test {

    public static void main(String [] argv) {
        System.out.println("目前執行路徑" + FileUtil.getWorkingPath());
        System.out.println("========================================");
        System.out.println("正在載入設定");
        Config.loadSetting();

        //testECPay();
        //testCreateUser();
        //EcpayServer.startServer();
        testloadProduct();
        testCreateProduct();

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

    public static void testloadProduct() {
        Product.loadAllFromDB();
        for(Product product : Product.getProducts().values()) {
            System.out.println(product.toString());
        }
    }

    public static void testCreateProduct() {
        Product product = new Product(-1, "newProduct_"+(int)(Math.floor(Math.random() * 300)), (int)(Math.floor(Math.random() * 300) + 100), "description", (int)(Math.floor(Math.random() * 5) + 5), (int)(Math.floor(Math.random() * 10) + 10), true);
        product.saveToDB();
        System.out.println(product.toString());
    }


}
