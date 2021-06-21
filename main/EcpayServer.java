package main;

import Config.EcpayConfig;
import handler.ecpay.EcpayPayment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EcpayServer {

    private static payServer server;
    private static ServerSocket socketServer;
    private static Socket socket;
    private static boolean OutServer = false;

    public final static void main(final String args[]) {
        startServer();
    }

    public static void startServer() {
        EcpayConfig.loadSetting();
        if (server == null) {
            server = new payServer();
            server.start();
        }
    }

    public static void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }

    static class payServer extends Thread {

        public void shutdown() {
            OutServer = true;
            try {
                if (socket != null) {
                    socket.close();
                }
                if (socketServer != null) {
                    socketServer.close();
                }
            } catch (IOException ex) {
                System.out.println("[服務端] Socket關閉問題 !");
                System.out.println("[服務端] IOException :" + ex.toString());
            }
            System.out.println("伺服器已關閉 !");
        }

        public void run() {
            try {
                socketServer = new ServerSocket(EcpayConfig.ECPayPort);
                System.out.println("<綠界金流伺服器> - 已啟動, 端口: " + EcpayConfig.ECPayPort);
                while (!OutServer) {
                    socket = null;
                    try {
                        synchronized (socketServer) {
                            socket = socketServer.accept();
                        }
                        socket.setSoTimeout(5000);
                        BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                        byte[] b = new byte[1024];
                        String data = "";
                        socket.shutdownOutput();

                        int length;
                        String s;
                        for (s = ""; (length = in.read(b)) > 0; s = data.trim()) {
                            data = data + new String(b, 0, length, "UTF-8");
                        }
                        System.out.println("接收到連線, data[" + data + "] s: [" + s + "]");

                        if (s.contains("CheckMacValue")) {
                            EcpayPayment.checkMacValue(s);
                        }
                        in.close();
                        in = null;
                        socket.close();
                    } catch (IOException ioException) {
//                    System.out.println("Socket連線有問題 !");
                    }
                }
            } catch (IOException ioException) {
                System.out.println("Socket啟動有問題 !");
                ioException.printStackTrace();
            }
        }
    }
}
