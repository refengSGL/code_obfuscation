package com.refengSGL.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.refengSGL.entity.request.UserVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String phone;

    private String role;

    private String email;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer isLogin;

    private Integer status;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;


    public UserVo getUserVo() {
        UserVo userVo = new UserVo();
        userVo.setUsername(username);
        userVo.setEmail(email);
        userVo.setPhone(phone);
        userVo.setCreateTime(createTime);
        userVo.setUpdateTime(updateTime);
        return userVo;
    }
}
