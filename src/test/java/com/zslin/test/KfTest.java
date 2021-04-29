package com.zslin.test;

import com.zslin.business.wx.tools.KfTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class KfTest {

    @Autowired
    private KfTools kfTools;

    @Test
    public void test01() {
        kfTools.listAll();
    }
}
