package com.zslin.core.api.tools;

import com.zslin.core.api.Explain;
import com.zslin.core.api.ExplainOperation;
import com.zslin.core.api.ExplainResult;
import com.zslin.core.api.ExplainResultField;
import com.zslin.core.api.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Api手册工具类
 */
@Component
public class ExplainTools {

    Logger log = LoggerFactory.getLogger(ExplainTools.class);

    public List<ExplainModelDto> buildAllExplain() {
//        String pn = "com/zslin/*/dao/*Service.class";

//        String pn = "com/zslin/*/dao/**Service.class";
//        buildByPn("classpath*:com/zslin/**/*Service.class");
        return build("classpath*:com/zslin/**/*Service.class");
    }

    /*private void buildByPn(String ...pns) {
        for(String pn : pns) {
            build(pn);
        }
    }*/

    private List<ExplainModelDto> build(String pn) {
        List<ExplainModelDto> result = new ArrayList<>();
        try {
            //1、创建ResourcePatternResolver资源对象
            ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
            //2、获取路径中的所有资源对象
            Resource[] ress = rpr.getResources(pn);
//            log.info("匹配到接口数："+ress.length);
            //3、创建MetadataReaderFactory来获取工程
            MetadataReaderFactory fac = new CachingMetadataReaderFactory();
            //4、遍历资源
            for(Resource res:ress) {
                MetadataReader mr = fac.getMetadataReader(res);
                String cname = mr.getClassMetadata().getClassName();
                AnnotationMetadata am = mr.getAnnotationMetadata();
                if(am.hasAnnotation(Explain.class.getName())) {
                    result.add(buildExplain(am, cname));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private ExplainModelDto buildExplain(AnnotationMetadata am, String cname) {
        ExplainModelDto model = new ExplainModelDto();
//        System.out.println("======="+cname);
        Map<String, Object> mapp = am.getAnnotationAttributes(Service.class.getName());
        Map<String, Object> classRes = am.getAnnotationAttributes(Explain.class.getName()); //类

        String serviceName = (String) mapp.get("value");
        String tempServiceName = cname.substring(cname.lastIndexOf(".")+1, cname.length());
        if(serviceName==null || "".equals(serviceName)) {
            serviceName = tempServiceName.substring(0, 1).toLowerCase() + tempServiceName.substring(1, tempServiceName.length());
        }

        ExplainDto explain = new ExplainDto();
        explain.setName(classRes.get("name").toString());
        explain.setNotes(classRes.get("notes").toString());
        String expValue = classRes.get("value").toString();
        if(expValue==null || "".equals(expValue.trim())) {expValue = serviceName;}
        explain.setValue(expValue);

        List<ExplainOperationDto> operationList = new ArrayList<>();

//        log.info("接口名称："+serviceName);

        Set<MethodMetadata> set = am.getAnnotatedMethods(ExplainOperation.class.getName());
        for(MethodMetadata mm : set) {
            Map<String, Object> methodRes = mm.getAnnotationAttributes(ExplainOperation.class.getName());
            String methodName = mm.getMethodName();

            ExplainOperationDto operation = new ExplainOperationDto();
            operation.setName(methodRes.get("name").toString());
            operation.setNotes(methodRes.get("notes").toString());
            String val = methodRes.get("value").toString();
            if(val==null || "".equals(val)) {val = serviceName + "." + methodName;}
            else {val = serviceName + "." +val;}
            operation.setValue(val);
            AnnotationAttributes [] parAttr = (AnnotationAttributes[]) methodRes.get("params");
            List<ExplainParamDto> paramList = new ArrayList<>();
            for(AnnotationAttributes attr : parAttr) {
                ExplainParamDto par = new ExplainParamDto();
                par.setExample(attr.getString("example"));
                par.setName(attr.getString("name"));
                par.setRequire(attr.getBoolean("require"));
                par.setType(attr.getString("type"));
                par.setValue(attr.getString("value"));
//                System.out.println(attr.getString("name"));
//                System.out.println(attr);
                paramList.add(par);
            }
            operation.setParamList(paramList);

            AnnotationAttributes [] returnAttr = (AnnotationAttributes[]) methodRes.get("back");
            List<ExplainReturnDto> returnList = new ArrayList<>();
            for(AnnotationAttributes attr : returnAttr) {
                ExplainReturnDto par = new ExplainReturnDto();
                par.setField(attr.getString("field"));
                par.setType(attr.getString("type"));
                par.setNotes(attr.getString("notes"));
                returnList.add(par);
            }
            operation.setReturnList(returnList);
//            System.out.println(operation);
            operationList.add(operation);
        }
        model.setExplain(explain);
        model.setOperationList(operationList);
        return model;
    }

    ///////////////////////////////////////////////////////

    /**
     * 构建API结果数据
     * 类名必须是以“Result”结尾
     * @return
     */
    public List<ERModelDto> buildExplainResult() {
        return buildResult("classpath*:com/zslin/**/*Result.class");
    }

    private List<ERModelDto> buildResult(String pn) {
        List<ERModelDto> result = new ArrayList<>();
        try {
            //1、创建ResourcePatternResolver资源对象
            ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
            //2、获取路径中的所有资源对象
            Resource[] ress = rpr.getResources(pn);
//            log.info("匹配到接口数："+ress.length);
            //3、创建MetadataReaderFactory来获取工程
            MetadataReaderFactory fac = new CachingMetadataReaderFactory();
            //4、遍历资源
            for(Resource res:ress) {
                MetadataReader mr = fac.getMetadataReader(res);
                AnnotationMetadata am = mr.getAnnotationMetadata();
                String cname = mr.getClassMetadata().getClassName();
//                System.out.println("---------->"+cname);
                if(am.hasAnnotation(ExplainResult.class.getName())) {
                    result.add(buildExplainResult(am, cname));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ERModelDto buildExplainResult(AnnotationMetadata am, String cname) throws Exception {
        ERModelDto model = new ERModelDto();
        Class clz = Class.forName(cname);
//        System.out.println("======="+clz.getSimpleName());
        Map<String, Object> classRes = am.getAnnotationAttributes(ExplainResult.class.getName()); //类

        ERDto erDto = new ERDto();
        erDto.setName(classRes.get("name").toString());
        erDto.setNotes(classRes.get("notes").toString());

        List<ERFieldDto> fieldList = new ArrayList<>();

        Field [] fields = clz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 这个是检查类中属性是否含有注解
            if (fields[i].isAnnotationPresent(ExplainResultField.class)) {
                // 获取注解
                ExplainResultField annotation = fields[i].getAnnotation(ExplainResultField.class);
                ERFieldDto fieldDto = new ERFieldDto();
                fieldDto.setName(annotation.name());
                fieldDto.setNotes(annotation.notes());
                fieldDto.setType(annotation.type());
                String value = annotation.value();
                fieldDto.setValue((value==null || "".equals(value.trim()))?fields[i].getName():value);
                fieldList.add(fieldDto);
            }
        }

//        System.out.println(fieldList);

        model.setResult(erDto);
        model.setFieldList(fieldList);
        return model;
    }
}
