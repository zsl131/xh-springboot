package com.zslin.business.mini.tools;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 微信支付结果通知工具类
 */
public class PayNotifyTools {

    public static Map<String, String> payRequest2Map(HttpServletRequest request) {
        String strxml = getNotifyStr(request);
        if("".equals(strxml)) {
            return null;
        }

        try {
            Map map = new HashMap();

            InputStream in = String2Inputstream(strxml);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if(children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = getChildrenText(children);
                }
                map.put(k, v);
            }
            in.close();
            return map;
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {sb.append(getChildrenText(list));}
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }

    /** 转换成字符串 */
    private static String getNotifyStr(HttpServletRequest request) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
            String line ;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            //sb为微信返回的xml
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}
