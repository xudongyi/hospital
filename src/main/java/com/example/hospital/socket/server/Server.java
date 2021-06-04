//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int SERVER_PORT = 8007;
    private static InputStream serverreader;
    private static OutputStream serverwriter;

    public Server() {
    }

    public void CreateServerThread() throws IOException {
        ServerSocket server = new ServerSocket(8007);
        Socket socket = server.accept();
        serverreader = socket.getInputStream();
        serverwriter = socket.getOutputStream();
        byte[] link_request = new byte[9999];
        serverreader.read(link_request);
        String link_request_str = new String(link_request);
        System.out.println("�ͻ�����������" + link_request_str);
        if ("0001".equals(link_request_str.trim())) {
            String link_response_str = "0002";
            byte[] link_response = link_response_str.getBytes("ASCII");
            serverwriter.write(link_response);
            System.out.println("��ͻ�������Ӧ��" + new String(link_response));

            while(true) {
                byte[] data = new byte[9999];
                serverreader.read(data);
                System.out.println("���տͻ�����������ݣ�" + new String(data));
                String data_str = (new String(data)).trim();

                while(data_str.length() != 0) {
                    int len = 4;
                    data_str = data_str.substring(len);

                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException var12) {
                        var12.printStackTrace();
                    }

                    String data_response_str = "0004";
                    byte[] data_response = data_response_str.getBytes("ASCII");
                    serverwriter.write(data_response);
                    System.out.println("��ͻ���Ӧ��" + new String(data_response));
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.CreateServerThread();
        System.out.println("server启动成功");
    }
}
