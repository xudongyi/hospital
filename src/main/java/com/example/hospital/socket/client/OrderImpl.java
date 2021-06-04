//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.client;

public class OrderImpl {
    public OrderImpl() {
    }

    public String link_Request() {
        StringBuffer length = new StringBuffer("0001");
        StringBuffer order = new StringBuffer();
        order.append(length);
        return order.toString();
    }

    public String link_Response() {
        StringBuffer length = new StringBuffer("0002");
        StringBuffer order = new StringBuffer();
        order.append(length);
        return order.toString();
    }

    public String data_Request() {
        StringBuffer length = new StringBuffer("0003");
        StringBuffer order = new StringBuffer();
        order.append(length);
        return order.toString();
    }

    public String data_Response() {
        StringBuffer length = new StringBuffer("0004");
        StringBuffer order = new StringBuffer();
        order.append(length);
        return order.toString();
    }
}
