package com.refengSGL.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterBo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String email;
    private String phone;

}
