package com.zslin.test;

import com.zslin.code.dto.EntityDto;
import com.zslin.code.tools.CodeGenerateCommon;
import com.zslin.code.tools.CodeGenerateServiceTools;
import com.zslin.code.tools.CodeGenerateTools;
import com.zslin.core.common.NormalTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;
import java.util.Properties;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class GenerateTest {

    @Test
    public void test06() {
        System.out.println(NormalTools.isNumeric("123"));
        System.out.println(NormalTools.isNumeric("123.45"));
        System.out.println(NormalTools.isNumeric("12df"));
        System.out.println(NormalTools.isNumeric("s12"));
    }

    @Test
    public void test05() {
//        String str = "@NotBlank-message=产品名称不能为空；@Length-min=4-message=至少4个字";
        String str = "@NotBlank-产品内容不能为空";
        str = str.replaceAll("；", ";")
                .replaceAll("_", "-")
                .replaceAll("“", "\"");
        String [] array = str.split(";");
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        boolean isFirstRule = true;
        for(String valid: array) {
            String [] singleArray = valid.split("-");
            for(String sa : singleArray) {
                if(sa.startsWith("@")) {
                    if(isFirst) {
                        sb.append(sa).append("(");
                        isFirst = false;
                    } else {
                        sb.append(")\n").append(sa).append("(");
                    }
                    isFirstRule = true;
                } else {
                    if(!isFirstRule) {sb.append(", ");}
                    isFirstRule = false;
                    String[]rule = sa.split("=");
                    if(rule.length==1) {
                        //@NotEmpty(message = "msgUrl不能为空")
                        sb.append("message=").append(buildStr(rule[0]));
                    } else if(rule.length==2) {
                        sb.append(rule[0]).append("=").append(buildStr(rule[1]));
                    }
                }
            }
        }
        sb.append(")");
        System.out.println(sb.toString());
    }

    private String buildStr(String rule) {
        boolean isNumber = NormalTools.isNumeric(rule);
        return isNumber?rule:("\""+rule+"\"");
    }

    @Test
    public void test04() {
//        String res = CodeGenerateServiceTools.buildAdd("测试管理", "Test", "testDao");
//        System.out.println("---->" + res);
    }

    @Test
    public void test03() throws Exception {
        String outPath = "D:\\temp\\velocity\\";
        String tmpFile = "TemplateEntity.java";
        String targetFile = "ResultEntity.java";

        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, outPath);
        VelocityEngine ve = new VelocityEngine(pro);


        VelocityContext context = new VelocityContext();
        context.put("pck","com.zslin.test.export1");
        context.put("name", "产品信息测试测试");
        context.put("author", "钟述林");
        context.put("tableName", "t_test_table");
        context.put("clsName", "TestTableEntity");
        context.put("fields", buildFields());
//        context.put("packageName",packageName);

        Template t = ve.getTemplate(tmpFile, "UTF-8");

        File file = new File(outPath, targetFile);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
        System.out.println("成功生成Java文件:"
                + (outPath + targetFile).replaceAll("/", "\\\\"));
    }

    private String buildFields() {
        StringBuffer sb = new StringBuffer();
        sb.append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("/**").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("* ").append("菜单名称").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("* @remark ").append("中文显示名称").append(CodeGenerateCommon.getLine())
            .append(CodeGenerateCommon.getTab()).append("*/").append(CodeGenerateCommon.getLine());
        sb.append(CodeGenerateCommon.getTab()).append("private String name;");
        return sb.toString();
    }

    @Test
    public void test02() {
        //E:\idea\2020\z_mall\src\main\java\com\zslin\business\
        String pck1 = "business";
        String basePck = "com.zslin";
        System.out.println(CodeGenerateTools.buildPackage(basePck,pck1));
        String pck2 = "app.business";
        System.out.println(CodeGenerateTools.buildPackage(basePck,pck2));
        System.out.println(CodeGenerateTools.buildPck(basePck, pck2, "controller"));
    }

    @Test
    public void test01() {
        String dir = System.getProperty("user.dir");

        System.out.println(dir);
    }

    @Test
    public void buildCode() throws Exception {
//        String projectPath = System.getProperty("user.dir");
//        System.out.println("projectPath==" + projectPath);
        String dir = System.getProperty("user.dir");
        String fileName = dir+File.separator+"model.xlsx";
//        String oldName = "G:\\钟述林\\X项目\\T特产\\model.xlsx";
        FileInputStream fis = new FileInputStream(new File(fileName));
        List<EntityDto> res = CodeGenerateCommon.generate(fis, 0, 2);
        CodeGenerateTools.generateCode("com.zslin", res);
        System.out.println(res);
    }
}
