package com.refengSGL.entity.response;

import lombok.Data;

/**
 * 用户登录成功返回结果类
 */
@Data
public class LoginVo {
    private Integer uid;
    private String username;
    private String token;
    private String role;
}
