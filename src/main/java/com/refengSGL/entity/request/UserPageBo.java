package com.refengSGL.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageBo implements Serializable {
    private static final long serialVersionUID = 1130078468326369415L;
    private Integer pageNum;
    private Integer pageSize;
    private String userName;
}
