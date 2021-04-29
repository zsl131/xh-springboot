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
 * 生成Model
 */
public class CodeGenerateModelTools {

    /**
     * 生成Model文件
     * @param path Java文件生成路径
     * @param pck package包名
     * @param ed EntityDto对象
     * @throws Exception
     */
    public static void generate(String path, String pck, EntityDto ed) throws Exception {
//        String sep = File.separator;
        String outPath = CodeGenerateCommon.getVelocityPath();
        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, outPath);
        VelocityEngine ve = new VelocityEngine(pro);


        VelocityContext context = new VelocityContext();
        context.put("pck",pck+".model");
        context.put("name", ed.getDesc());
        context.put("author", ed.getAuthor());
        context.put("validatePck", CodeGenerateCommon.buildValidatePck(ed.getFields()));
        context.put("tableName", CodeGenerateCommon.buildTableName(pck, ed.getCls()));
        context.put("clsName", ed.getCls());
        context.put("fields", CodeGenerateCommon.buildFields(ed.getFields()));
        context.put("date", NormalTools.curDate());
//        context.put("packageName",packageName);

        Template t = ve.getTemplate("entityTemplate.vm", "UTF-8");

        File file = new File(path+ed.getCls()+".java");

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
    }
}
