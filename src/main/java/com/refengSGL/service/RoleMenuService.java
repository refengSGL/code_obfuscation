package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.RoleMenu;

import java.util.ArrayList;

public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 设置角色菜单关系
     */
    void setRoleMenu(Integer rid, ArrayList<Integer> mids);

    /**
     * 查询角色绑定的菜单
     */
    ArrayList<Integer> getRoleMenuList(Integer rid);
}
