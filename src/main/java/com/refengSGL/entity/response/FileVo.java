package com.refengSGL.entity.response;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class FileVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private String memory;

    private Integer originalFileId;

    private String originalFileName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
