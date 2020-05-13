package com.logoxiang.practice.taskDao;

import com.logoxiang.practice.entity.TimingInterface;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: liuXiang
 * @Date: 2020/4/29 14:53
 */
public interface TimingConfigDao {
    /**
     * 根据类型查询定时任务配置列表
     * @param type 类型
     * @return 结果
     */
    public List<TimingInterface> getTimingInterfaceList(@Param("type") String type);

}