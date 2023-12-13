package com.refengSGL.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tokenName;
    private String tokenValue;
}
