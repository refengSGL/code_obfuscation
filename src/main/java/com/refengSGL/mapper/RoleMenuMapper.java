package com.refengSGL.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.refengSGL.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    /**
     * 删除当前角色id的所有绑定菜单关系
     */
    @Delete("delete from role_menu where rid=#{rid}")
    int deleteByRoleId(@Param("rid") Integer rid);

    /**
     * 查询当前角色id绑定的菜单
     */
    @Select("select mid from role_menu where rid=#{rid}")
    ArrayList<Integer> getRoleMenuList(@Param("rid") Integer rid);
}
