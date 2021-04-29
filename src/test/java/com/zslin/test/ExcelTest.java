package com.zslin.test;

import com.alibaba.fastjson.JSONObject;
import com.zslin.business.xzqh.dto.DivisionResultDto;
import com.zslin.business.xzqh.tools.BuildJsonTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class ExcelTest {

    @Test
    public void test03() {
        List<DivisionResultDto> list = BuildJsonTools.buildDivisionJson("publicFile/xzqh.xlsx");
        for(DivisionResultDto dto : list) {
            System.out.println(dto);
        }
        String str = JSONObject.toJSONString(list);
        System.out.println("==============================================");
        System.out.println(str);
    }

    @Test
    public void test02() {
//        Map<DivisionSingleDto, Map> map = BuildJsonTools.buildResult("publicFile/xzqh.xlsx");
//        System.out.println(map);
    }

    @Test
    public void test01() {
        System.out.println(System.getProperty("user.dir"));
        String path = ClassLoader.getSystemResource("publicFile/xzqh.xlsx").getPath();
        System.out.println(path);

//        String str = BuildJsonTools.buildDivisionJson("publicFile/xzqh.xlsx");
//        System.out.println(str);
    }
}
