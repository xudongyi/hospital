//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
    public ServerTest() {
    }

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(21002);
            System.out.println("正在监听端口:21002");

            while(true) {
                Socket socket = server.accept();
                System.out.println("客户端:" + socket + "进入");
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                byte[] b = new byte[in.available()];
                System.out.println(socket.getInputStream());
                in.read(b);
                System.out.println("接收到客户端的数据：" + new String(b));
                out.write(b);
                out.flush();
                out.close();
                in.close();
                socket.close();
                System.out.println("客户端:" + socket + "离开");
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }
}
