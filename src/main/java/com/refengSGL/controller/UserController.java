package com.refengSGL.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.refengSGL.entity.Menu;
import com.refengSGL.entity.User;
import com.refengSGL.entity.request.LoginBo;
import com.refengSGL.entity.request.RegisterBo;
import com.refengSGL.entity.request.UserPageBo;
import com.refengSGL.entity.request.UserVo;
import com.refengSGL.entity.response.LoginVo;
import com.refengSGL.entity.response.UserPageDto;
import com.refengSGL.exception.CodeException;
import com.refengSGL.service.MenuService;
import com.refengSGL.service.RoleMenuService;
import com.refengSGL.service.RoleService;
import com.refengSGL.service.UserService;
import com.refengSGL.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.refengSGL.constant.ResultCode.*;
import static com.refengSGL.constant.UserConstant.IS_LOGOUT;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    private final MenuService menuService;

    private final RoleMenuService roleMenuService;

    /**
     *
     * @return
     */
    @GetMapping("/identity")
    public SaResult adminIdentity(){
        Map<String, String> success = new HashMap<>();
        success.put("identity", "yes");
        Map<String, String> error = new HashMap<>();
        error.put("identity", "no");
        Integer userId = TokenUtils.getCurrentUser().getId();
        log.info("userId:{}",userId);

        if (userId==1){
            return SaResult.ok("管理员").setData(success);
        }
        return SaResult.error("普通用户").setData(error);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public SaResult userRegister(@RequestBody RegisterBo registerBo) {
        int count = userService.userRegister(registerBo);
        if (count != 1) {
            return SaResult.error("注册失败").setCode(ERROR);
        }
        return SaResult.ok("注册成功").setCode(SUCCESS);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody LoginBo loginBo) {
        if (Objects.isNull(loginBo)) {
            log.info("LoginBo is null");
            throw new CodeException("用户登录信息接收对象为空");
        }
        User user = userService.userLogin(loginBo);
        if (!Objects.isNull(user)) {
            String token = TokenUtils.createToken(user.getId().toString(), user.getPassword());
            LoginVo loginVo = new LoginVo();
            loginVo.setUid(user.getId());
            String role = user.getRole();
         //   List<Menu> roleMenus = getRoleMenus(role);
            loginVo.setRole(role);
         //   loginVo.setMenus(roleMenus);
            loginVo.setUsername(user.getUsername());
            loginVo.setToken(token);
            return SaResult.ok("登录成功").setData(loginVo).setCode(SUCCESS);
        }
        return SaResult.error("登录失败").setCode(ERROR);
    }

    /**
     * 获取对应角色的菜单
     */
    private List<Menu> getRoleMenus(String role) {
        // 获取角色id
        Integer rid = roleService.getByFlag(role);
        // 根据rid查询菜单id列表
        ArrayList<Integer> mids = roleMenuService.getRoleMenuList(rid);
        // 查询数据库中所有的菜单
        List<Menu> menus = menuService.findMenus("");
        // 筛选当前用户角色菜单
        ArrayList<Menu> roleMenus = new ArrayList<>();
        for (Menu menu : menus) {
            if (mids.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            children.removeIf(child -> !mids.contains(child.getId()));
        }
        return roleMenus;
    }

    /**
     * 用户退出
     */
    @GetMapping("/logout")
    public SaResult userLogout() {
        User currentUser = TokenUtils.getCurrentUser();
        assert currentUser != null;
        Integer id = currentUser.getId();
        currentUser.setIsLogin(IS_LOGOUT);
        userService.updateById(currentUser);
        return SaResult.ok("退出成功").setCode(SUCCESS);
    }


    @GetMapping("/query/{id}")
    public SaResult getUserInfoById(@PathVariable Long id) {
        User user = userService.getUserInfoById(id);
        return SaResult.ok("获取成功").setData(user.getUserVo()).setCode(SUCCESS);
    }

    /**
     * 获取当前登录的用户信息
     */
    @GetMapping("/query/current")
    public SaResult getCurrentLoginUser() {
        UserVo userVo = userService.getCurrentUserInfo();
        if (!Objects.isNull(userVo)) {
            return SaResult.ok("获取成功").setData(userVo).setCode(SUCCESS);
        }
        return SaResult.ok("获取失败").setCode(ERROR);
    }

    /**
     * 更新用户信息（用户id）
     */
    @PutMapping("/update")
    public SaResult updateById(@RequestBody User user) {
        boolean result = userService.updateByUserId(user);
        if (result) {
            return SaResult.ok("修改成功").setCode(SUCCESS);
        }
        return SaResult.error("修改失败").setCode(ERROR);
    }

    /**
     * 用户分页查询
     */
    @GetMapping("/page")
    public SaResult page(UserPageBo userPageBo) {
        if (Objects.isNull(userPageBo)) {
            log.info("用户分页查询反序列化类为空");
            throw new CodeException("分页参数接收对象为空");
        }
        Page<UserPageDto> pageInfo = userService.getUserPage(userPageBo);
        if (!Objects.isNull(pageInfo)) {
            return SaResult.ok("获取成功").setData(pageInfo).setCode(SUCCESS);
        }
        return SaResult.error("获取失败").setCode(ERROR);
    }

    /**
     * 修改用户状态
     */
    @PutMapping("/status/{id}")
    public SaResult updateStatus(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            log.info("用户id为空");
            throw new CodeException("用户id为空");
        }
        boolean success = userService.updateStatus(id);
        if (success) {
            return SaResult.ok("切换成功").setCode(SUCCESS);
        }
        return SaResult.error("切换失败").setCode(ERROR);
    }
}
