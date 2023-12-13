package com.refengSGL.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PageVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer queryFileId;
    private String originalFileName;
    private String resultFileName;
    private LocalDateTime updateTime;
}
