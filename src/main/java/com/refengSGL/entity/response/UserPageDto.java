package com.refengSGL.entity.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;

    private String username;

    private String phone;

    private String password;

    private String email;

    private Integer status;
}
