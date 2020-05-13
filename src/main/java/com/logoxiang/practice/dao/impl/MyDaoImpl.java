package com.logoxiang.practice.dao.impl;

import com.logoxiang.practice.dao.MyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author: liuXiang
 * @Date: 2020/4/24 16:05
 */
@Repository
public class MyDaoImpl implements MyDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getCommonQuestionList() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from T_COMMON_QUESTION ");
        return jdbcTemplate.queryForList(sql.toString());
    }

    @Override
    public List<Map<String, Object>> getCommonQuestionList2(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select question_id \"questionId\",question_title \"questionTitle\", question_answer \"questionAnswer\" ");
        sql.append(" from T_COMMON_QUESTION ");
        return jdbcTemplate.queryForList(sql.toString());
    }

}
