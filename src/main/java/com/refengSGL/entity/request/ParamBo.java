package com.refengSGL.entity.request;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ParamBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;

    private Integer type;

    private ArrayList<String> paramList;
}
