package com.zslin.core.common;

import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 通用工具类
 */
@Slf4j
public class NormalTools {

    /**
     * 转换mb４字条串
     * @param con
     * @return
     */
    public static String rebuildUTF8MB4(String con) {
        try {
            con = con.replaceAll("[^\\u0000-\\uFFFF]", ""); //替换utf8mb4字条
        } catch (Exception e) {
        }
        return con;
    }

    public static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
//            return s.matches("^[0-9]*$");
            return s.matches("([0-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
        else
            return false;
    }

    public static String getNow(String pattern) {
        /*SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String res =  sdf.format(new Date());
        return res;*/
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        String res = df.format(LocalDateTime.now());
        return res;
    }

    /**
     * 字段串转long类型
     * @param time 日期格式的字符串
     * @param pattern 日期格式
     * @return
     */
    public static Long str2Long(String time, String pattern) {
        Date date = getDate(time, pattern);
        return date.getTime();
    }

    public static String getMonth(String pattern) {
        return getMonth(pattern, 0);
    }

    public static String getMonth(String pattern, int plus) {
        LocalDate ld = LocalDate.now();
        if(plus!=0) {
            ld = ld.plusMonths(plus);
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return df.format(ld);
    }

    public static Date getDate(String dateStr, String pattern) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime ldt = LocalDateTime.parse(dateStr, df);
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {return null;}
    }

    public static String curDatetime() {
        return getNow("yyyy-MM-dd HH:mm:ss");
    }

    public static String curDate() {
        return getNow("yyyy-MM-dd");
        /*LocalDate localDate = LocalDate.now();
        return localDate.toString();*/
    }

    public static String getFileType(String fileName) {
        if(fileName!=null && fileName.indexOf(".")>=0) {
            return fileName.substring(fileName.lastIndexOf("."), fileName.length());
        }
        return "";
    }

    /**
     * 判断文件是否为图片文件
     * @param fileName
     * @return
     */
    public static Boolean isImageFile(String fileName) {
        String [] img_type = new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"};
        if(fileName==null) {return false;}
        fileName = fileName.toLowerCase();
        for(String type : img_type) {
            if(fileName.endsWith(type)) {return true;}
        }
        return false;
    }

    /**
     * 生成两位小数的数字
     * @param d double类型的数字
     * @return
     */
    public static double buildPoint(double d) {
        /*BigDecimal bg = new BigDecimal(d);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        f1 = Math.rint(f1);
        return f1;*/
        return Math.ceil(d);
    }

    /** 判断是否为空 */
    public static boolean isNull(String val) {
        return (val==null || "".equals(val));
    }

    /** 有一个为空则为空 */
    public static boolean isNullOr(String ...values) {
        boolean res = false;
        for(String val : values) {
            if(isNull(val)) {res = true; break;}
        }
        return res;
    }

    /**
     * 保留2位小数
     * @param value 需要转换的数值
     * @return
     */
    public static Float retain2Decimal(Double value) {
        if(value==null) {return 0f;}
        DecimalFormat df = new DecimalFormat("#.00");
        return Float.parseFloat(df.format(value));
    }
}
