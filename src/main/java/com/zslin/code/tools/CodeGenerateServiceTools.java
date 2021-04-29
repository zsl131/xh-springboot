package com.zslin.code.tools;

import com.zslin.code.dto.EntityDto;
import com.zslin.core.common.NormalTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Properties;

/**
 * 生成Service
 */
public class CodeGenerateServiceTools {

    /**
     * 生成Service文件
     * @param path Java文件生成路径
     * @param pck package包名
     * @param ed EntityDto对象
     * @throws Exception
     */
    public static void generate(String path, String pck, EntityDto ed) throws Exception {
//        String sep = File.separator;
        if(NormalTools.isNullOr(ed.getPModuleName())) {return ;} //如果没有父模块则不生成Service
        String outPath = CodeGenerateCommon.getVelocityPath();
        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, outPath);
        VelocityEngine ve = new VelocityEngine(pro);

        String clsName = ed.getCls();
        String clsDesc = ed.getDesc();
        String daoName = buildDaoName(clsName);

        VelocityContext context = new VelocityContext();
        context.put("pck",pck+".service");
        context.put("author", ed.getAuthor());

        context.put("clsName", clsName);
        context.put("date", NormalTools.curDate());
        context.put("serviceClsName", buildClsName(clsName));
        context.put("entityPck", pck+".model");
        context.put("clsDesc", clsDesc);
        context.put("pModuleName", ed.getPModuleName());
        context.put("url", ed.getUrl());
        context.put("daoClsName", "I"+clsName+"Dao");
        context.put("daoClsPck", pck+".dao");
        context.put("daoName", daoName);

        context.put("functions", buildFuncStr(ve, ed.getFuns(), clsDesc, clsName, daoName));

        Template t = ve.getTemplate("serviceTemplate.vm", "UTF-8");

        File file = new File(path+buildClsName(ed.getCls())+".java");

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
    }

    private static String buildFuncStr(VelocityEngine ve, String funs, String clsDesc, String clsName, String daoName) {
        StringBuffer sb = new StringBuffer();
        VelocityContext context = new VelocityContext();
        context.put("clsDesc", clsDesc);
        context.put("clsName", clsName);
        context.put("daoName", daoName);
        String sep = "\n\n";
        if(funs.contains("L")) { //如果包含List
            sb.append(buildFunc(ve,"service-list.vm", context));
            sb.append(sep);
        }
        if(funs.contains("C")) { //如果包含C，即add
            sb.append(buildFunc(ve, "service-add.vm", context));
            sb.append(sep);
        }
        if(funs.contains("U")) { //如果包含U，即update
            sb.append(buildFunc(ve, "service-update.vm", context));
            sb.append(sep);
        }
        if(funs.contains("R")) { //如果包含R，即loadOne
            sb.append(buildFunc(ve, "service-load.vm", context));
            sb.append(sep);
        }
        if(funs.contains("D")) { //如果包含D,即delete
            sb.append(buildFunc(ve, "service-delete.vm", context));
            sb.append(sep);
        }
        return sb.toString();
    }

    public static String buildFunc(VelocityEngine ve, String tmpName, VelocityContext context) {
//        Properties pro = new Properties();

        Template t = ve.getTemplate(tmpName, "UTF-8");
        // 输出渲染后的结果
        StringWriter sw = new StringWriter();

        /*VelocityContext context = new VelocityContext();
        for(ContextDto cd : ctx) {context.put(cd.getKey(), cd.getValue());}*/

        t.merge(context, sw);
        //System.out.println(sw.toString());
        return sw.toString();
    }

    /**
     * 生成类名
     * @param clsName 实体类类名
     * @return
     */
    private static String buildClsName(String clsName) {
        StringBuffer sb = new StringBuffer();
        sb.append(clsName).append("Service");
        return sb.toString();
    }

    private static String buildDaoName(String clsName) {
        String res = clsName.substring(0,1).toLowerCase()+clsName.substring(1) + "Dao";
        return res;
    }
}
