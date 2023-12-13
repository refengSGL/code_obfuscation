package com.refengSGL.entity.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilePageDto {
    private Integer id;
    private String fileName;
    private String type;
    private String fileSize;
    private String fileLocation;
    private LocalDateTime createTime;
}
