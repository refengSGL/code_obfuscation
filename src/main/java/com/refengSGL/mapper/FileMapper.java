package com.refengSGL.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refengSGL.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * 用户注册数统计（当日）
     */
    @Select("select count(1) from user where date(create_time) = #{day}")
    Integer selectRegisterNumByDay(String day);

    /**
     * 上传文件数统计（当日）
     */
    @Select("select count(1) from file where date(create_time) = #{day} and type = 'c'")
    Integer selectUploadFileNumByDay(String day);

    /**
     * 保护文件数统计（当日）
     */
    @Select("select count(1) from file where date(create_time) = #{day} and type in ('ELF 64-bit LSB executable', 'ASCII text')")
    Integer selectProtectFileNumByDay(String day);
}
