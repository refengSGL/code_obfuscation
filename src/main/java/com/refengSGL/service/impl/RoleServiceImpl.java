package com.refengSGL.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.refengSGL.entity.Role;
import com.refengSGL.mapper.RoleMapper;
import com.refengSGL.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public Integer getByFlag(String role) {
        return baseMapper.getByFlag(role);
    }
}
