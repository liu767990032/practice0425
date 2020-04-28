package com.logoxiang.practice.dao;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuXiang
 * @Date: 2020/4/24 16:04
 */
public interface MyDao {

    List<Map<String,Object>> getCommonQuestionList();

    List<Map<String, Object>> getCommonQuestionList2();
}
