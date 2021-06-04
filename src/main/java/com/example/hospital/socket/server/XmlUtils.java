//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class XmlUtils {
    public XmlUtils() {
    }

    public Document getXml(String xml) throws DocumentException {
        Document document = DocumentHelper.parseText(xml);
        return document;
    }
}
