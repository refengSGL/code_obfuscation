package com.refengSGL.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.Menu;
import com.refengSGL.mapper.MenuMapper;
import com.refengSGL.service.MenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Override
    public List<Menu> findMenus(String name) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Menu::getName, name);
        }
        // 查询menu中所有的记录
        List<Menu> list = baseMapper.selectList(queryWrapper);
        // 查询一级菜单
        List<Menu> parentNode = list
                .stream().
                filter(menu -> Objects.isNull(menu.getPid()))
                .collect(Collectors.toList());
        // 封装一级菜单的子菜单
        for (Menu menu : parentNode) {
            menu.setChildren(list
                    .stream()
                    .filter(tmp -> menu.getId().equals(tmp.getPid()))
                    .collect(Collectors.toList())
            );
        }
        return parentNode;
    }
}
