package com.logoxiang.practice;

import com.logoxiang.practice.dao.MyDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

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

}
