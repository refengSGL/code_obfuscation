package com.refengSGL.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.Daily;
import com.refengSGL.mapper.DailyMapper;
import com.refengSGL.service.DailyService;
import org.springframework.stereotype.Service;

@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService{
    /**
     * 统计当日记录数据
     */
    @Override
    public void createStatisticsByDay(String day) {
        // 如果当日统计记录已存在，则删除重新统计
        LambdaQueryWrapper<Daily> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Daily::getDateCalculated, day);
        baseMapper.delete(queryWrapper);
    }
}
