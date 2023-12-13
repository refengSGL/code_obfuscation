package com.refengSGL.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;

    private String phone;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
