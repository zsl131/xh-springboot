package com.zslin.core.tools;

import com.zslin.core.annotations.AdminAuth;
import com.zslin.core.dao.IAdminMenuDao;
import com.zslin.core.model.AdminMenu;
import com.zslin.core.repository.SimpleSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zsl on 2018/7/5.
 */
@Component
public class BuildAdminMenuTools {

    @Autowired
    private IAdminMenuDao menuDao;

    public void buildAdminMenusOrderNo() {
        Sort sort = SimpleSortBuilder.generateSort("orderNo_a");
        List<AdminMenu> root = menuDao.findRootMenu(sort);
        Integer index = 1;
        for(AdminMenu r : root) {
            menuDao.updateOrderNo(index++, r.getId());
            buildAdminMenusOrderNo(r.getId(), sort);
        }
    }

    private void buildAdminMenusOrderNo(Integer pid, Sort sort) {
        List<AdminMenu> list = menuDao.findByParent(pid, sort);
        if(list!=null && list.size()>0) {
            int index = 1;
            for(AdminMenu m : list) {
                menuDao.updateOrderNo(index++, m.getId());
                buildAdminMenusOrderNo(m.getId(), sort);
            }
        }
    }

    public void buildAdminMenus() {
//        String pn = "com/zslin/*/dao/*Service.class";

//        String pn = "com/zslin/*/dao/**Service.class";
        buildByPn("classpath*:com/zslin/**/service/*Service.class");
    }

    private void buildByPn(String ...pns) {
        for(String pn : pns) {
            build(pn);
        }
    }

    private void build(String pn) {
        try {
            //1、创建ResourcePatternResolver资源对象
            ResourcePatternResolver rpr = new PathMatchingResourcePatternResolver();
            //2、获取路径中的所有资源对象
            Resource[] ress = rpr.getResources(pn);
            //3、创建MetadataReaderFactory来获取工程
            MetadataReaderFactory fac = new CachingMetadataReaderFactory();
            //4、遍历资源
            for(Resource res:ress) {
                MetadataReader mr = fac.getMetadataReader(res);
                String cname = mr.getClassMetadata().getClassName();
                AnnotationMetadata am = mr.getAnnotationMetadata();
                if(am.hasAnnotation(AdminAuth.class.getName())) {
//                    addMenu(am, cname);
                    buildMenus(am, cname);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildMenus(AnnotationMetadata am, String cname) {
//        System.out.println("======="+cname);
        Map<String, Object> mapp = am.getAnnotationAttributes(Service.class.getName());
        Map<String, Object> classRes = am.getAnnotationAttributes(AdminAuth.class.getName()); //类

        String serviceName = (String) mapp.get("value");
        String tempServiceName = cname.substring(cname.lastIndexOf(".")+1, cname.length());
        if(serviceName==null || "".equals(serviceName)) {
            serviceName = tempServiceName.substring(0, 1).toLowerCase() + tempServiceName.substring(1, tempServiceName.length());
        }

        AdminMenu clsMenu = buildClassMenu(serviceName, classRes);

        Set<MethodMetadata> set = am.getAnnotatedMethods(AdminAuth.class.getName());
        for(MethodMetadata mm : set) {
            Map<String, Object> methodRes = mm.getAnnotationAttributes(AdminAuth.class.getName());
            String methodName = mm.getMethodName();
            buildMethodMenu(clsMenu, methodName, methodRes);
        }
    }

    private AdminMenu buildClassMenu(String serviceName, Map<String, Object> classRes) {
        String cpsn = (String) classRes.get("psn");
        boolean isEnglish = cpsn.getBytes().length==cpsn.length(); //无汉字
//        String cpsnEn = isEnglish?cpsn: PinyinToolkit.cn2Spell(cpsn, "_"); //类上的父菜单SN
        String cpsnEn = PinyinToolkit.cn2Spell(cpsn, "_");
        AdminMenu cpm = menuDao.findBySn(cpsnEn);
        if(cpm==null) {
            cpm = new AdminMenu();
            cpm.setOrderNo(1);
            cpm.setDisplay(1);
            cpm.setHref("#");
            cpm.setIcon("");
            cpm.setName(cpsn);
            cpm.setSn(cpsnEn);
            cpm.setType("1");
            menuDao.save(cpm);
        }
        ///以上是处理类上的父菜单
        AdminMenu cm = menuDao.findBySn(serviceName); //类菜单
        if(cm==null) {
            cm = new AdminMenu();
            cm.setOrderNo((Integer) classRes.get("orderNum"));
            cm.setDisplay(1);
            String href = (String) classRes.get("url");
            if(href!=null && !"#".equals(href) && !href.startsWith("/")) {href = "/" + href;}
            cm.setHref(href);
            cm.setIcon((String) classRes.get("icon"));
            cm.setName((String) classRes.get("name"));
            cm.setSn(serviceName);
            cm.setType((String) classRes.get("type"));
            cm.setPid(cpm.getId());
            cm.setPname(cpm.getName());
            cm.setPsn(cpm.getSn());
            menuDao.save(cm);
        }

        return cm;
    }

    private void buildMethodMenu(AdminMenu cm, String methodName, Map<String, Object> methodRes) {
        String sn = cm.getSn()+"."+methodName;
        AdminMenu m = menuDao.findBySn(sn);
        if(m==null) {
            m = new AdminMenu();
            m.setPsn(cm.getSn());
            m.setPname(cm.getName());
            m.setPid(cm.getId());
            m.setSn(sn);
            m.setName((String) methodRes.get("name"));
            m.setIcon((String) methodRes.get("icon"));
            m.setDisplay(1);
            String href = (String) methodRes.get("url");
            if(href!=null && !"#".equals(href) && !href.startsWith("/")) {href = "/" + href;}

            m.setHref(href);
            m.setOrderNo((Integer) methodRes.get("orderNum"));
            m.setType((String) methodRes.get("type"));
            menuDao.save(m);
        }
    }
}
