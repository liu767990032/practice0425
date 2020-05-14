package com.logoxiang.practice;

import com.logoxiang.practice.dao.MyDao;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@SpringBootTest
class PracticeApplicationTests {

    @Autowired
    private MyDao myDao;
    @Test
    void contextLoads() {
    }

    @Test
    public void test22(){
        System.out.println(111);
    }

    @Test
    public void test0023(){
        List<Map<String, Object>> commonQuestionList = myDao.getCommonQuestionList();
        System.out.println(commonQuestionList);
    }

    @Test
    public void test4(){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("liuxiang");
        //要加密的数据（数据库的用户名或密码）
        String password = textEncryptor.encrypt("123456");
        System.out.println("password:"+password);
    }

}
