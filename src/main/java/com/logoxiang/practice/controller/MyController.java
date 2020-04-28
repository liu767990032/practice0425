package com.logoxiang.practice.controller;

import com.alibaba.fastjson.JSON;
import com.logoxiang.practice.dao.MyDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuXiang
 * @Date: 2020/4/24 16:58
 */
@RestController
public class MyController {
    @Autowired
    private MyDao myDao;

    private Logger logger = LoggerFactory.getLogger(MyController.class);

    @GetMapping("getCommonQuestionList")
    public List<Map<String, Object>> getCommonQuestionList() {
        List<Map<String, Object>> commonQuestionList = myDao.getCommonQuestionList();
        logger.info("获取问题信息列表："+ JSON.toJSONString(commonQuestionList));
        return commonQuestionList;
    }

    @GetMapping("getCommonQuestionList2")
    public List<Map<String, Object>> getCommonQuestionList2() {
        List<Map<String, Object>> commonQuestionList = myDao.getCommonQuestionList2();
        logger.info("获取问题信息列表："+ JSON.toJSONString(commonQuestionList));
        return commonQuestionList;
    }
}
