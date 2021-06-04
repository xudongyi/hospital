//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital;

import com.example.hospital.socket.server.HL7ToXmlConverter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.example.hospital.mapper"})
public class HospitalApplication {
    public HospitalApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);

        try {
            ServerSocket server = new ServerSocket(21002);
            System.out.println("lis==============:21002");

            while(true) {
                Socket socket = server.accept();
                System.out.println("socket==========in======:" + socket);
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                Thread.sleep(2000L);
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
                byte[] b = new byte[in.available()];
                System.out.println(socket.getInputStream());
                in.read(b);
                System.out.println("getClientMsg==================：" + new String(b));
                new String(b);
                String result = "";
                if (new String(b) != null && !"".equals(new String(b))) {
                    result = HL7ToXmlConverter.parseXML(new String(b));
                }

                byte[] byteArray = result.getBytes();
                out.write(byteArray);
                out.flush();
                out.close();
                in.close();
                socket.close();
                System.out.println("client====:" + socket + "=====leave====");
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }
}
