package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.Daily;

public interface DailyService extends IService<Daily> {

    /**
     * 统计当日记录数据
     */
    void createStatisticsByDay(String day);
}
