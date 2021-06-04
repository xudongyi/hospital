//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientThread extends Thread {
    private Socket socket;

    public ClientThread() {
    }

    protected boolean login() {
        OrderImpl login = new OrderImpl();
        System.out.println("���Ͱ�����" + login.link_Request());

        try {
            Constants.writer.write(login.link_Request().getBytes());
            Constants.writer.flush();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return false;
    }

    protected void init() {
        try {
            this.socket = new Socket("127.0.0.1", 8007);
            Constants.reader = this.socket.getInputStream();
            Constants.writer = this.socket.getOutputStream();
            Constants.isConnection = true;
        } catch (UnknownHostException var2) {
            Constants.isConnection = false;
            System.out.println("����ʧ�ܣ���Ч����˵�ַ��");
        } catch (IOException var3) {
            Constants.isConnection = false;
            System.out.println("����ʧ�ܣ�IO�˿��쳣���������˿��Ƿ�����");
        }

    }

    protected void closeSocket() {
        Constants.isConnection = false;

        try {
            if (Constants.reader != null) {
                Constants.reader.close();
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        try {
            if (Constants.writer != null) {
                Constants.writer.close();
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
