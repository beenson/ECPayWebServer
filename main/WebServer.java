package main;

import Controller.UserController;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebServer {
    public static HttpServer server;
    public static boolean enable = false;

    public static void start() {
        try {
            long startNow = System.currentTimeMillis();
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/user", new UserController());
            //server.createContext("/userX", new UserController());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            enable = true;
            System.out.println("網頁伺服器初始化成功 占用Port:" + 8080+ "  花費時間:"+(System.currentTimeMillis()-startNow)+"毫秒");
        } catch (Exception ex) {
            System.out.println("網頁伺服器初始化失敗");
            ex.printStackTrace();
        }
    }
    public static void shutdown(){
        try {
            server.stop(1);
            enable = false;
            System.out.println("網頁伺服器已關閉");
        }catch (Exception ex){
            System.out.println("網頁伺服器關閉時發生異常");
            ex.printStackTrace();
        }
    }

}