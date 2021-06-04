//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

import java.io.IOException;

public class DataWrite extends ClientThread {
    public DataWrite() {
    }

    public void run() {
        if (!Constants.isConnection) {
            System.out.println("����ʧ�ܣ���������...");
            this.init();
            this.login();
            ReadThread readThread = new ReadThread();
            readThread.readData();
        } else if (Constants.isBand) {
            System.out.println("�\udb9a\ude33ɹ�������ҵ����������");
            OrderImpl order = new OrderImpl();

            try {
                Constants.writer.write(order.data_Request().getBytes());
                System.out.println("ҵ����������" + order.data_Request());
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        } else {
            System.out.println("���ӳɹ�����ʧ�ܣ���˶Ժ����°\udb9a\ude21�");
        }

    }
}
