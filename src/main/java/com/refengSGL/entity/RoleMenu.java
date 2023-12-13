package com.refengSGL.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("role_menu")
public class RoleMenu implements Serializable {
    private static final long serialVersionUID = -6873361285235199651L;

    private Integer rid;

    private Integer mid;
}
