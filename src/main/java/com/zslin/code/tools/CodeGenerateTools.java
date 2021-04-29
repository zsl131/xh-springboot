package com.zslin.code.tools;

import com.zslin.code.dto.EntityDto;
import com.zslin.code.dto.FieldDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成工具类
 */
public class CodeGenerateTools {

    public static void generateCode(String basePck, List<EntityDto> entList) {
        for(EntityDto ed : entList) {
            String pck = basePck+"."+ed.getPck();
            generateModel(basePck, pck, ed);
            generateDao(basePck, pck, ed);
            generateService(basePck, pck, ed);
        }
    }
    //生成model
    private static void generateModel(String basePck, String pck, EntityDto ed) {
        try {
            String filePath = buildPck(basePck, ed.getPck(), "model");
            CodeGenerateModelTools.generate(filePath, pck, ed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //生成dao
    private static void generateDao(String basePck, String pck, EntityDto ed) {
        String daoClsName = "I"+ed.getCls()+"Dao";
        String filePath = buildPck(basePck, ed.getPck(), "dao");
        if(!existsFile(filePath, daoClsName)) { //如果不存在则生成
            try {
                CodeGenerateDaoTools.generate(filePath, pck, ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //生成service
    private static void generateService(String basePck, String pck, EntityDto ed) {
        String daoClsName = ed.getCls()+"Service";
        String filePath = buildPck(basePck, ed.getPck(), "service");
        if(!existsFile(filePath, daoClsName)) { //如果不存在则生成
            try {
                CodeGenerateServiceTools.generate(filePath, pck, ed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /** 检测是否存在 */
    private static boolean existsFile(String filePath, String javaName) {
        File f = new File(filePath+ File.separator + javaName+".java");
        return f.exists();
    }

    /**
     * 生成功能包，如：model、controller等
     * @param basePck 绝对路径，定位到具体包的路径下
     * @param pck 如：model
     * @return
     */
    private static String buildFunPck(String basePck, String pck) {
        StringBuffer sb = new StringBuffer(basePck);
        sb.append(pck).append(File.separator);
        String res = sb.toString();
        File file = new File(res);
        if(!file.exists()) {
            file.mkdirs();
        }
        return res;
    }

    /**
     *
     * @param basePck 基础包名，如：com.zslin
     * @param pck 业务包名，如：business.app
     * @param funPck 功能包名，如：model
     * @return
     */
    public static String buildPck(String basePck, String pck, String funPck) {
        return buildFunPck(buildPackage(basePck, pck), funPck);
    }

    public static String buildPackage(String basePck, String pck) {
        String rootPath = getRoot(basePck);
        StringBuffer sb = new StringBuffer(rootPath);
        sb.append(exchangePackage(pck));
        return sb.toString();
    }

    private static String getRoot(String basePackage) {
        //src\main\java
        StringBuffer sb = new StringBuffer(System.getProperty("user.dir"));
//        sb.append(File.separator).append("src").append(File.separator).append("main").
//                append(File.separator).append("java").append(File.separator);

        sb.append(File.separator).append(exchangePackage("src.main.java"));
        sb.append(exchangePackage(basePackage));
        return sb.toString();
    }

    private static String exchangePackage(String pck) {
        String [] array = pck.split("\\.");
        StringBuffer sb = new StringBuffer();
        for(String a : array) {
            sb.append(a).append(File.separator);
        }
        return sb.toString();
    }
}
