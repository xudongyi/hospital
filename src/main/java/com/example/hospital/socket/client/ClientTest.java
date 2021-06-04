//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class ClientTest {
    public ClientTest() {
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 21002);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            String str = "MSH|^~\\&|ZHGL|MediInfo|MediII|MediInfo|20180606101015||PMU^B01^PMU_B01|4bd463c7e0544476a375f08d5925ced2|P|2.4\nEVN|B01\nSTF|0275|0275|^童芬美^^TFM^UAU^01|5|2|19730205000000|A|~327^药剂科||^^^^^^13067882839~~~||19960801000000||||||||||330227197302050687|||20180528152319";
            out.write(str.getBytes());
            out.flush();
            byte[] b = new byte[1024];
            in.read(b);
            System.out.println("收到服务器回复：" + new String(b));
            in.close();
            out.close();
            socket.close();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }
}
