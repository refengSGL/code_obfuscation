package com.refengSGL.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.refengSGL.constant.MenuConstant;
import com.refengSGL.entity.Directory;
import com.refengSGL.entity.Menu;
import com.refengSGL.exception.CodeException;
import com.refengSGL.service.DirectoryService;
import com.refengSGL.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.refengSGL.constant.ResultCode.ERROR;
import static com.refengSGL.constant.ResultCode.SUCCESS;

@Slf4j
@Validated
@CrossOrigin
@RestController
@RequestMapping("menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    private final DirectoryService directoryService;

    /** 添加菜单 */
    @PostMapping("/save")
    public SaResult save(@RequestBody Menu menu) {
        boolean res = menuService.save(menu);
        if (res) {
            return SaResult.ok("添加成功").setCode(SUCCESS);
        }
        return SaResult.ok("添加失败").setCode(ERROR);

    }

    /**
     * 批量添加菜单
     */
    @PostMapping("/saveBatch")
    public SaResult saveBatch(@RequestBody ArrayList<Menu> menuList) {
        log.info(menuList.toString());
        boolean res = menuService.saveBatch(menuList);
        if (res) {
            return SaResult.ok("添加成功").setCode(SUCCESS);
        }
        return SaResult.ok("添加失败").setCode(ERROR);
    }

    /**
     * 修改菜单
     */
    @PutMapping("/update")
    public SaResult update(@RequestBody Menu menu) {
        boolean res = menuService.saveOrUpdate(menu);
        if (res) {
            return SaResult.ok("操作成功").setCode(SUCCESS);
        }
        return SaResult.ok("操作失败").setCode(ERROR);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/remove/{id}")
    public SaResult remove(@PathVariable Integer id) {
        boolean res = menuService.removeById(id);
        if (res) {
            return SaResult.ok("删除成功").setCode(SUCCESS);
        }
        return SaResult.ok("删除失败").setCode(ERROR);
    }

    /**
     * 批量删除菜单
     */
    @DeleteMapping("/removeBatch")
    public SaResult removeBatch(@RequestBody ArrayList<Integer> menuList) {
        boolean res = menuService.removeByIds(menuList);
        if (res) {
            return SaResult.ok("删除成功").setCode(SUCCESS);
        }
        return SaResult.ok("删除失败").setCode(ERROR);
    }

    /**
     * 根据id查询菜单
     */
    @GetMapping("/{id}")
    public SaResult getById(@PathVariable Integer id) {
        Menu byId = menuService.getById(id);
        if (Objects.isNull(byId)) {
            log.info("目标菜单不存在");
            throw new CodeException("目标菜单不存在");
        }
        return SaResult.ok("获取成功").setData(byId).setCode(SUCCESS);
    }

    /**
     * 查询所有菜单
     */
    @GetMapping("/list")
    public SaResult list(@RequestParam(defaultValue = "") String name) {
        List<Menu> parentNode = menuService.findMenus(name);
        return SaResult.ok("获取成功").setData(parentNode).setCode(SUCCESS);
    }

    /**
     * 查询菜单总数
     */
    @GetMapping("/count")
    public SaResult count() {
        int count = menuService.count();
        return SaResult.ok("获取成功").setData(count).setCode(SUCCESS);
    }

    /**
     * 获取图标列表
     */
    @GetMapping("/icons")
    public SaResult icons() {
        LambdaQueryWrapper<Directory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Directory::getType, MenuConstant.MENU_TYPE_ICON);
        List<Directory> list = directoryService.list(queryWrapper);
        return SaResult.ok("获取成功").setData(list).setCode(SUCCESS);
    }
}
