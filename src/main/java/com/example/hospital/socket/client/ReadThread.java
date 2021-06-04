//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

import java.io.IOException;

public class ReadThread extends ClientThread {
    public ReadThread() {
    }

    public void run() {
        if (!Constants.isConnection) {
            System.out.println("���ڳ�ʼ�����ӣ���...");
            this.init();
            this.login();
            this.readData();
        }

    }

    public void readData() {
        while(true) {
            byte[] bindrcbyte = new byte[9999];

            try {
                Constants.reader.read(bindrcbyte);
                System.out.println("���շ����Ӧ����Ϣ��" + new String(bindrcbyte));
                String bindrc = CalculateUtil.ChangeByteToString(bindrcbyte);
                bindrc = bindrc.substring(0, 4);
                int type = CalculateUtil.getType(bindrc);
                this.handleGetData(type);
            } catch (IOException var5) {
                Constants.isConnection = false;
                Constants.isBand = false;
                var5.printStackTrace();

                try {
                    sleep(30000L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }

                this.init();
                this.login();
            }
        }
    }

    public void handleGetData(int type) throws IOException {
        OrderImpl login = new OrderImpl();
        if (type == 1) {
            Constants.writer.write(login.link_Response().getBytes());
        } else if (type == 2) {
            Constants.isBand = true;
        } else if (type == 3) {
            Constants.writer.write(login.data_Response().getBytes());
        } else if (type != 4 && type == 5) {
        }

    }
}
