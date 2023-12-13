package com.refengSGL.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.refengSGL.entity.response.FileVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("file")
public class File implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private String memory;

    private String store;

    private Integer userId;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private Integer originalFile;

    private String originalFileName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    public FileVo getFileVo() {
        FileVo fileVo = new FileVo();
        fileVo.setName(name);
        fileVo.setCreateTime(createTime);
        fileVo.setMemory(memory);
        fileVo.setType(type);
        fileVo.setOriginalFileId(originalFile);
        return fileVo;
    }

}
