//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

import java.io.UnsupportedEncodingException;

public class CalculateUtil {
    public CalculateUtil() {
    }

    public static String ChangeByteToString(byte[] bytes) {
        try {
            return new String(bytes, "ASCII");
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Integer getType(String str) {
        int c = 0;
        if ("0001".equals(str)) {
            c = 1;
        } else if ("0002".equals(str)) {
            c = 2;
        } else if ("0003".equals(str)) {
            c = 3;
        } else if ("0004".equals(str)) {
            c = 4;
        } else if ("0005".equals(str)) {
            c = 5;
        }

        return Integer.valueOf(c);
    }
}
