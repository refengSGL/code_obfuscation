package com.refengSGL.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refengSGL.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据flag获取角色id
     */
    @Select("select id from role where flag=#{flag}")
    Integer getByFlag(@Param("flag") String role);
}
