package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.User;
import com.refengSGL.entity.request.LoginBo;
import com.refengSGL.entity.request.RegisterBo;
import com.refengSGL.entity.request.UserPageBo;
import com.refengSGL.entity.request.UserVo;
import com.refengSGL.entity.response.UserPageDto;


public interface UserService extends IService<User> {
    /**
     * 用户登录
     */
    User userLogin(LoginBo loginBo);

    /**
     * 用户注册
     */
    int userRegister(RegisterBo registerBo);

    /**
     * 根据ID获取用户信息
     */
    User getUserInfoById(Long id);

    /**
     * 获取当前登录用户信息
     */
    UserVo getCurrentUserInfo();

    /**
     * 更新用户信息（用户ID）
     */
    boolean updateByUserId(User user);

    /**
     * 封装UserPage
     */
    Page<UserPageDto> getUserPage(UserPageBo userPageBo);

    /**
     * 切换用户状态
     */
    boolean updateStatus(Integer id);

}
