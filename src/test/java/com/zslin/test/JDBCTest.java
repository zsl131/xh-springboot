package com.zslin.test;

import com.zslin.test.tools.JDBCHandleTools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(value = "zsl")
public class JDBCTest {

    @Test
    public void test02() throws Exception {
        JDBCHandleTools jdbcHandleTools = new JDBCHandleTools();
        jdbcHandleTools.handleProTitle();
    }

    @Test
    public void test01() throws Exception {
        JDBCHandleTools jdbcHandleTools = new JDBCHandleTools();
        jdbcHandleTools.run();
    }
}