package com.refengSGL.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.Menu;
import com.refengSGL.entity.RoleMenu;
import com.refengSGL.mapper.RoleMenuMapper;
import com.refengSGL.service.MenuService;
import com.refengSGL.service.RoleMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    private final MenuService menuService;

    @Override
    public void setRoleMenu(Integer rid, ArrayList<Integer> mids) {
        // 删除当前角色id对应所有的菜单绑定关系
        baseMapper.deleteByRoleId(rid);

        ArrayList<Integer> midsCopy = CollUtil.newArrayList(mids);
        // 将菜单id列表绑定到角色id
        for (Integer mid : mids) {
            Menu menu = menuService.getById(mid);
            if (!Objects.isNull(menu.getPid()) && !midsCopy.contains(menu.getPid())) {
                // 设置二级菜单的父级id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRid(rid);
                roleMenu.setMid(mid);
                baseMapper.insert(roleMenu);
                midsCopy.add(menu.getPid());
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRid(rid);
            roleMenu.setMid(mid);
            baseMapper.insert(roleMenu);
        }
    }

    @Override
    public ArrayList<Integer> getRoleMenuList(Integer rid) {
        return baseMapper.getRoleMenuList(rid);
    }
}
