package com.refengSGL.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageBo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer page;
    private Integer limit;
    private String fileName;
}
