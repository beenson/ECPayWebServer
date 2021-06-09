package main;

import Config.Config;
import Util.FileUtil;
import handler.ecpay.EcpayPayment;
import handler.jwt.AuthVerify;
import handler.jwt.JwtHandler;
import model.Category;
import model.Order;
import model.Product;
import model.User;

import java.util.Date;


public class test {

    public static void main(String [] argv) {
        System.out.println("目前執行路徑" + FileUtil.getWorkingPath());
        System.out.println("========================================");
        System.out.println("正在載入設定");
        Config.loadSetting();
        JwtHandler.init();

        Product.loadAllFromDB();
        Category.loadAllFromDB();

        WebServer.start();
        //testECPayCVS();
        //testECPayATM();
        //testCreateUser();
        //EcpayServer.startServer();
        //testloadProduct();
        //testCreateProduct();
        //testloadOrder();
        //testCreateOrder();
        //testJWT();

    }

    public static void testJWT() {
        User usr = new User(2);
        String token = AuthVerify.generateToken(usr);
        User usrtoken = AuthVerify.getAuth(token);
        System.out.println(token);
        System.out.println(usrtoken.toString());
    }

    public static void testECPayCVS() {
        Thread th = new Thread(() -> {
            EcpayPayment.genAioCheckOutCVS(1000);
        });
        th.start();
    }

    public static void testECPayATM() {
        Thread th = new Thread(() -> {
            EcpayPayment.genAioCheckOutATM(1000, "BOT");
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
        Product product = new Product(-1, "newProduct_"+(int)(Math.floor(Math.random() * 300)), (int)(Math.floor(Math.random() * 300) + 100), "description", (int)(Math.floor(Math.random() * 5) + 5), (int)(Math.floor(Math.random() * 10) + 10), true, "https://picsum.photos/800", 2);
        product.saveToDB();
        System.out.println(product.toString());
    }

    public static void testloadOrder() {
        Order.loadAllFromDB();
        for(Order order : Order.getOrders().values()) {
            System.out.println(order.toString());
        }
    }

    public static void testCreateOrder() {
        Order order = new Order(-1, 1, 1000, 0, new Date());
        order.saveToDB();
        System.out.println(order.toString());
    }


}
