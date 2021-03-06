package com.zslin.business.wx.tools;

import com.zslin.business.wx.annotations.HasTemplateMessage;
import com.zslin.business.wx.annotations.TemplateMessageAnnotation;
import com.zslin.business.wx.dao.ITemplateMessageRelationDao;
import com.zslin.business.wx.dto.TemplateMessageDto;
import com.zslin.business.wx.model.TemplateMessageRelation;
import com.zslin.core.tools.PinyinToolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by zsl on 2018/8/28.
 */
@Component
public class TemplateMessageAnnotationTools {

    @Autowired
    private ITemplateMessageRelationDao templateMessageRelationDao;

    private String [] packages = new String []{"classpath*:com/zslin/**/service/*.class", "classpath*:com/zslin/**/*.class"};

    public Map<String, String> checkTemplateMessage() {
        Map<String, String> result = new HashMap<>();
        for(String pn : packages) {
            result.putAll(buildTemplateMessageAnnotation(pn));
        }
        return result;
    }

    public List<TemplateMessageDto> findNoConfigTemplateMessage() {
        List<TemplateMessageDto> result = new ArrayList<>();
        Map<String, String> all = checkTemplateMessage();
        for(String key : all.keySet()) {
            TemplateMessageRelation tmr = templateMessageRelationDao.findByTemplatePinyin(PinyinToolkit.cn2Spell(key, ""));
            if(tmr==null) {
//                result.put(key, all.get(key));
                result.add(new TemplateMessageDto(key, all.get(key)));
            }
        }
        return result;
    }

    /**
     * ?????????????????????????????????AdminAuth????????????
     * @param pn Controller??????????????????????????????
     */
    private Map<String, String> buildTemplateMessageAnnotation(String pn) {
        Map<String, String> result = new HashMap<>();
        try {
            //??????????????????Annotation?????????????????????????????????
//			String pn = "com/zslin/*/controller/*/*Controller.class";
            //1?????????ResourcePatternResolver????????????
            ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
            //2???????????????????????????????????????
            Resource[] ress = rpr.getResources(pn);
            //3?????????MetadataReaderFactory???????????????
            MetadataReaderFactory fac = new CachingMetadataReaderFactory();
            //4???????????????
            for(Resource res:ress) {
                MetadataReader mr = fac.getMetadataReader(res);
                String cname = mr.getClassMetadata().getClassName();
//                System.out.println("---------"+cname);

                AnnotationMetadata am = mr.getAnnotationMetadata();
                if(am.hasAnnotation(HasTemplateMessage.class.getName())) {
//                    System.out.println("===="+cname);
                    Set<MethodMetadata> set = am.getAnnotatedMethods(TemplateMessageAnnotation.class.getName());
                    for(MethodMetadata m : set) {
                        Map<String, Object> tma = m.getAnnotationAttributes(TemplateMessageAnnotation.class.getName());
//                        System.out.println(tma.get("name")+"--"+tma.get("keys"));
                        result.put(tma.get("name").toString(), tma.get("keys").toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
