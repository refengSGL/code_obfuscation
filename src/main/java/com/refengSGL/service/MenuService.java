package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> findMenus(String name);
}
