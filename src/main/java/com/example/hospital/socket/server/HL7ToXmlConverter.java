//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.hospital.socket.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class HL7ToXmlConverter {
    static String ip = "196.196.196.12";
    static String postUrl = "http://196.196.196.12:60//services/HrmService";
    static int socketTimeout = 30000;
    static int connectTimeout = 30000;

    public HL7ToXmlConverter() {
    }

    public static String ConvertToXml(String sHL7) {
        Document document = ConvertToXmlObject(sHL7);
        String hl7str = document.asXML();
        return hl7str;
    }

    public static String ConvertToXml(Document document) {
        String hl7str = document.asXML();
        return hl7str;
    }

    public static Document ConvertToXmlObject(String sHL7) {
        Document document = CreateXmlDoc();
        String[] sHL7Lines = sHL7.split("\r");

        int i;
        for(i = 0; i < sHL7Lines.length; ++i) {
            sHL7Lines[i] = sHL7Lines[i].replace("^~\\&", "").replace("MSH", "MSH|");
        }

        for(i = 0; i < sHL7Lines.length; ++i) {
            if (sHL7Lines[i] != null) {
                String sHL7Line = sHL7Lines[i];
                String[] sFields = GetMessgeFields(sHL7Line);
                Element el = document.getRootElement().addElement(sFields[0]);
                Boolean isMsh = true;

                for(int a = 1; a < sFields.length; ++a) {
                    if (sFields[a].indexOf(94) <= 0 && sFields[a].indexOf(126) <= 0 && sFields[a].indexOf(92) <= 0 && sFields[a].indexOf(38) <= 0) {
                        Element fieldEl = el.addElement(sFields[0] + "." + a);
                        fieldEl.setText(sFields[a]);
                    } else {
                        String[] sComponents = GetRepetitions(sFields[a]);
                        if (sComponents.length > 1) {
                            for(int b = 0; b < sComponents.length; ++b) {
                                CreateComponents(el, sComponents[b], sFields[0], a, b);
                            }
                        } else {
                            CreateComponents(el, sFields[a], sFields[0], a, 0);
                        }
                    }
                }
            }
        }

        document.selectSingleNode("HL7Message/MSH/MSH.1").setText("|");
        document.selectSingleNode("HL7Message/MSH/MSH.2").setText("~^\\&");
        return document;
    }

    private static Element CreateComponents(final Element el, final String hl7Components, String sField, int a, int b) {
        Element componentEl = el.addElement(sField + "." + a);
        String[] subComponents = GetSubComponents(hl7Components);
        if (subComponents.length <= 1) {
            String[] sRepetitions = GetComponents(hl7Components);
            if (sRepetitions.length > 1) {
                Element repetitionEl = null;

                for(int c = 0; c < sRepetitions.length; ++c) {
                    repetitionEl = componentEl.addElement(sField + "." + a + "." + (c + 1));
                    repetitionEl.setText(sRepetitions[c]);
                }
            } else {
                componentEl.setText(hl7Components);
            }
        }

        return el;
    }

    private static String[] GetMessgeFields(String s) {
        return s.split("\\|");
    }

    private static String[] GetComponents(String s) {
        return s.split("\\^");
    }

    private static String[] GetSubComponents(String s) {
        return s.split("&");
    }

    private static String[] GetRepetitions(String s) {
        return s.split("~");
    }

    private static Document CreateXmlDoc() {
        Document output = DocumentHelper.createDocument();
        Element rootNode = output.addElement("HL7Message");
        return output;
    }

    public static String GetText(Document document, String path) {
        Node node = document.selectSingleNode("HL7Message/" + path);
        return node != null ? node.getText() : null;
    }

    public static String GetText(Document document, String path, int index) {
        List nodes = document.selectNodes("HL7Message/" + path);
        return nodes != null ? ((Node)nodes.get(index)).getText() : null;
    }

    public static List GetTexts(Document document, String path) {
        List nodes = document.selectNodes("HL7Message/" + path);
        return nodes;
    }

    public static void writeDocument(Document document, String filepath) {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(filepath), "utf-8");
            OutputFormat xmlFormat = new OutputFormat();
            xmlFormat.setEncoding("utf-8");
            XMLWriter xmlWriter = new XMLWriter(writer, xmlFormat);
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (IOException var5) {
            System.out.println("文件没有找到");
            var5.printStackTrace();
        }

    }

    public static String parseXML(String myHL7string) {
        myHL7string = myHL7string.trim();
        Document document = ConvertToXmlObject(myHL7string);
        String eventName = GetText(document, "MSH/MSH.9/MSH.9.3");
        System.out.println("eventName:" + eventName);
        String STF1 = GetText(document, "EVN/EVN.1");
        String result = "";
        if (!"B01".equals(STF1) && !"B02".equals(STF1) && !"B03".equals(STF1)) {
            result = parseDept(myHL7string);
        } else {
            result = parseUser(myHL7string);
        }

        System.out.println(ConvertToXml(myHL7string));
        return result;
    }

    public static String parseUser(String myHL7string) {
        Document document = ConvertToXmlObject(myHL7string);
        String eventName = GetText(document, "MSH/MSH.9/MSH.9.3");
        String date = GetText(document, "MSH/MSH.7");
        String code = GetText(document, "MSH/MSH.10");
        System.out.println("eventName:" + eventName);
        String EVN1 = GetText(document, "EVN/EVN.1");
        String STF1 = GetText(document, "STF/STF.1/STF.1.1");
        if (STF1 == null || "".equals(STF1)) {
            STF1 = GetText(document, "STF/STF.1");
        }

        String STF2 = GetText(document, "STF/STF.1/STF.1.2");
        String STF3 = GetText(document, "STF/STF.3", 0);
        String reg = "[^一-龥]";
        STF3 = STF3.replaceAll(reg, "");
        System.out.println(STF3);
        String STF4 = GetText(document, "STF/STF.4", 0);
        String STF5 = GetText(document, "STF/STF.5", 0);
        String STF6 = GetText(document, "STF/STF.6", 0);
        String STF7 = GetText(document, "STF/STF.7", 0);
        String STF81 = GetText(document, "STF/STF.8/STF.8.1", 0);
        String STF82 = GetText(document, "STF/STF.8/STF.8.2", 0);
        String STF9 = GetText(document, "STF/STF.9", 0);
        String STF10 = GetText(document, "STF/STF.10", 0);
        String STF107 = "";
        if (STF10 != null && !"".equals(STF10)) {
            STF107 = STF10.replace("~", "");
        }

        String STF15 = "";

        try {
            STF15 = GetText(document, "STF/STF.15", 0);
        } catch (Exception var25) {
            System.out.println("未解析到电子邮件");
            var25.printStackTrace();
        }

        String STF17 = "";

        try {
            STF17 = GetText(document, "STF/STF.17", 0);
        } catch (Exception var24) {
            System.out.println("已婚信息");
            var24.printStackTrace();
        }

        String var21 = "";

        try {
            var21 = GetText(document, "STF/STF.22", 0);
        } catch (Exception var23) {
            System.out.println("未解析到身份证信息");
            var23.printStackTrace();
        }

        System.out.println(ConvertToXml(myHL7string));
        String result = "";
        if ("B01".equals(EVN1)) {
            result = saveUser(ip, postUrl, STF1, STF2, STF3, STF82, STF107, STF15, STF17, "正式");
        } else if ("B02".equals(EVN1)) {
            result = updateUser(ip, postUrl, STF1, STF2, STF3, STF82, STF107, STF15, STF17, "正式");
        } else if ("B03".equals(EVN1)) {
            result = saveUser(ip, postUrl, STF1, STF2, STF3, STF82, STF107, STF15, STF17, "无效");
        }

        return "1".equals(result) ? returnMessage("AA", date, code) : returnMessage("AE", date, code);
    }

    public static String saveUser(String ip, String postUrl, String STF1, String STF2, String STF3, String STF82, String STF107, String STF15, String STF17, String status) {
        StringBuffer bf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n   <soapenv:Header/>\n   <soapenv:Body>\n      <hrm:SynHrmResource>\n         <hrm:in0>" + ip + "</hrm:in0>\n         <hrm:in1>\n         <![CDATA[\n         <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n           <root>\n               <hrmlist>\n               <hrm action=\"add\">\n                   <workcode>" + STF1 + "</workcode>\n                   <loginid>" + STF1 + "</loginid>\n                   <lastname>" + STF3 + "</lastname>\n                   <password>" + STF2 + "</password>\n                   <subcompany>" + STF82 + "</subcompany>\n                   <department>" + STF82 + "</department>\n                   <jobtitle></jobtitle>\n                   <statue>" + status + "</statue>\n                   <managerid></managerid>\n                   <sex></sex>\n                   <birthday></birthday>\n                   <telephone>" + STF107 + "</telephone>\n                   <mobile></mobile>\n                   <email>" + STF15 + "</email>\n                   <maritalstatus>" + STF17 + "</maritalstatus>\n               </hrm>\n           </hrmlist>\n           </root>         ]]>\n         </hrm:in1>\n      </hrm:SynHrmResource>\n   </soapenv:Body>\n</soapenv:Envelope>");
        System.out.println("save-xml==="+bf.toString());
        String retStr2 = doPostSoap(postUrl, bf.toString(), "");
        System.out.println("retStr2================" + retStr2);
        retStr2.replace(" ", "");
        String result = checkResult(retStr2);
        System.out.println("System.out.println======" + new Date());
        return result;
    }

    public static String updateUser(String ip, String postUrl, String STF1, String STF2, String STF3, String STF82, String STF107, String STF15, String STF17, String status) {
        StringBuffer bf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n   <soapenv:Header/>\n   <soapenv:Body>\n      <hrm:SynHrmResource>\n         <hrm:in0>" + ip + "</hrm:in0>\n         <hrm:in1>\n         <![CDATA[\n         <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n           <root>\n               <hrmlist>\n               <hrm action=\"edit\">\n                   <workcode>" + STF1 + "</workcode>\n                   <loginid>" + STF1 + "</loginid>\n                   <password>" + STF2 + "</password>\n               </hrm>\n           </hrmlist>\n           </root>         ]]>\n         </hrm:in1>\n      </hrm:SynHrmResource>\n   </soapenv:Body>\n</soapenv:Envelope>");
        System.out.println("update-xml==="+bf.toString());
        String retStr2 = doPostSoap(postUrl, bf.toString(), "");
        System.out.println("retStr2================" + retStr2);
        retStr2.replace(" ", "");
        String result = checkResult(retStr2);
        System.out.println("System.out.println======" + new Date());
        return result;
    }

    public static String parseDept(String myHL7string) {
        Document document = ConvertToXmlObject(myHL7string);
        String eventName = GetText(document, "MSH/MSH.9/MSH.9.3");
        String date = GetText(document, "MSH/MSH.7");
        String code = GetText(document, "MSH/MSH.10");
        System.out.println("eventName:" + eventName);
        String MEF1 = GetText(document, "MFE/MFE.1");
        String Z2B1 = GetText(document, "Z2B/Z2B.1");
        String Z2B2 = GetText(document, "Z2B/Z2B.2");
        String Z2B3 = GetText(document, "Z2B/Z2B.3");
        String Z2B4 = GetText(document, "Z2B/Z2B.4");
        String Z2B5 = GetText(document, "Z2B/Z2B.5");
        String Z2B6 = GetText(document, "Z2B/Z2B.6");
        String Z2B7 = GetText(document, "Z2B/Z2B.7");
        String Z2B8 = GetText(document, "Z2B/Z2B.8");
        String Z2B9 = GetText(document, "Z2B/Z2B.9");
        String Z2B10 = GetText(document, "Z2B/Z2B.10");
        String Z2B11 = GetText(document, "Z2B/Z2B.11");
        String Z2B12 = GetText(document, "Z2B/Z2B.12");
        String Z2B13 = GetText(document, "Z2B/Z2B.13");
        String Z2B14 = GetText(document, "Z2B/Z2B.14");
        String Z2B15 = GetText(document, "Z2B/Z2B.15");
        String Z2B16 = GetText(document, "Z2B/Z2B.16");
        String Z2B17 = GetText(document, "Z2B/Z2B.17");
        String Z2B18 = GetText(document, "Z2B/Z2B.18/Z2B.18.1");
        String Z2B19 = GetText(document, "Z2B/Z2B.19");
        String Z2B20 = GetText(document, "Z2B/Z2B.20");
        String Z2B21 = GetText(document, "Z2B/Z2B.21");
        String result = "";
        if ("MAD".equals(MEF1)) {
            result = saveDept(ip, postUrl, Z2B1, Z2B3, Z2B18);
        } else if ("MUP".equals(MEF1)) {
            result = saveDept(ip, postUrl, Z2B1, Z2B3, Z2B18);
        } else if ("MDL".equals(MEF1)) {
            result = delDept(ip, postUrl, Z2B1, Z2B3, Z2B18);
        }

        System.out.println("XML===========" + ConvertToXml(myHL7string));
        return "1".equals(result) ? returnMessage("AA", date, code) : returnMessage("AE", date, code);
    }

    public static String saveDept(String ip, String postUrl, String Z2B1, String Z2B3, String Z2B18) {
        StringBuffer bf;
        String retStr2;
        String result;
        if (Z2B18 != null && !"".equals(Z2B18)) {
            Z2B18 = "0";
            bf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n   <soapenv:Header/>\n   <soapenv:Body>\n      <hrm:SynDepartment>\n         <hrm:in0>" + ip + "</hrm:in0>\n         <hrm:in1>\n         <![CDATA[\n         <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n         <root>\n         <orglist>\n         <org action=\"add\">\n         <code>" + Z2B1 + "</code>\n         <shortname>" + Z2B3 + "</shortname>\n         <fullname>" + Z2B3 + "</fullname>\n         <parent_code>" + Z2B18 + "</parent_code>\n         <order>0</order>\n         </org>\n         </orglist>\n         </root>\n         ]]>\n         </hrm:in1>\n      </hrm:SynDepartment>\n   </soapenv:Body>\n</soapenv:Envelope>");
            retStr2 = doPostSoap(postUrl, bf.toString(), "");
            System.out.println("retStr2================" + retStr2);
            retStr2.replace(" ", "");
            result = checkResult(retStr2);
            System.out.println("System.out.println===1===" + new Date());
            System.out.println("retStr2===1===" + retStr2);
            return result;
        } else {
            bf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n   <soapenv:Header/>\n   <soapenv:Body>\n      <hrm:SynSubCompany>\n         <hrm:in0>" + ip + "</hrm:in0>\n         <hrm:in1>\n         <![CDATA[\n         <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n         <root>\n         <orglist>\n         <org action=\"add\">\n         <code>" + Z2B1 + "</code>\n         <shortname>" + Z2B3 + "</shortname>\n         <fullname>" + Z2B3 + "</fullname>\n         <parent_code>" + Z2B18 + "</parent_code>\n         <order>0</order>\n         </org>\n         </orglist>\n         </root>\n         ]]>\n         </hrm:in1>\n      </hrm:SynSubCompany>\n   </soapenv:Body>\n</soapenv:Envelope>");
            retStr2 = doPostSoap(postUrl, bf.toString(), "");
            System.out.println("retStr2================" + retStr2);
            retStr2.replace(" ", "");
            result = checkResult(retStr2);
            System.out.println("System.out.println===2===" + new Date());
            System.out.println("retStr2===1===" + retStr2);
            return result;
        }
    }

    public static String returnMessage(String status, String date, String code) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String msg = "";
        char a = 11;
        char b = '\r';
        char c = 28;
        if ("AA".equals(status)) {
            msg = a + "MSH|^~\\&|OA|OA|MediII|MediII|" + date + "||ACK|" + uuid + "|P|2.4" + b + "MSA|" + status + "|" + code + c;
        } else {
            msg = a + "MSH|^~\\&|OA|OA|MediII|MediII|" + date + "||ACK|" + uuid + "|P|2.4" + b + "MSA|" + status + "|" + code + "|Exception" + c;
        }

        return msg;
    }

    public static String checkResult(String str) {
        String result = "";
        if (str.contains("&#25104;&#21151;")) {
            result = "1";
        } else {
            result = "1";
        }

        return result;
    }

    public static String delDept(String ip, String postUrl, String Z2B1, String Z2B3, String Z2B18) {
        StringBuffer bf = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:hrm=\"http://localhost/services/HrmService\">\n   <soapenv:Header/>\n   <soapenv:Body>\n      <hrm:SynDepartment>\n         <hrm:in0>" + ip + "</hrm:in0>\n         <hrm:in1>\n         <![CDATA[\n         <?xml version=\"1.0\" encoding=\"UTF-8\"?>\n         <root>\n         <orglist>\n         <org action=\"delete\">\n         <code>" + Z2B1 + "</code>\n         <canceled>1</canceled>\n         </org>         </orglist>\n         </root>\n         ]]>\n         </hrm:in1>\n      </hrm:SynDepartment>\n   </soapenv:Body>\n</soapenv:Envelope>");
        String retStr2 = doPostSoap(postUrl, bf.toString(), "");
        retStr2.replace(" ", "");
        String result = checkResult(retStr2);
        System.out.println("System.out.println======" + new Date());
        System.out.println(retStr2);
        return result;
    }

    public static String doPostSoap(String postUrl, String soapXml, String soapAction) {
        String retStr = "";
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpPost = new HttpPost(postUrl);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        httpPost.setConfig(requestConfig);

        try {
            httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httpPost.setHeader("SOAPAction", soapAction);
            StringEntity data = new StringEntity(soapXml, Charset.forName("UTF-8"));
            httpPost.setEntity(data);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                retStr = EntityUtils.toString(httpEntity, "UTF-8");
            }

            closeableHttpClient.close();
        } catch (Exception var11) {
            System.out.println("exception in doPostSoap:" + var11.getMessage());
        }

        return retStr;
    }

    public static String regex(String xml, String label) {
        String context = "";
        String rgex = "<" + label + ">(.*?)</" + label + ">";
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(xml);

        ArrayList list;
        byte i;
        int var8;
        for(list = new ArrayList(); m.find(); var8 = i + 1) {
            i = 1;
            list.add(m.group(i));
        }

        if (list.size() > 0) {
            context = (String)list.get(0);
        }

        return context;
    }

    public static List getContext(String html) {
        List resultList = new ArrayList();
        Pattern p = Pattern.compile(">([^</]+)</");
        Matcher m = p.matcher(html);

        while(m.find()) {
            resultList.add(m.group(1));
        }

        return resultList;
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
                parseXML(new String(b));
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
