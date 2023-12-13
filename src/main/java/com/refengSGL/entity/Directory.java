package com.refengSGL.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Directory implements Serializable {
    private static final long serialVersionUID = -6873361285213799651L;

    private String name;

    private String value;

    private String type;
}
