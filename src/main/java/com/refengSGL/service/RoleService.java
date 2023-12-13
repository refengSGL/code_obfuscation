package com.refengSGL.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.refengSGL.entity.Role;

/**
 * @author 修雯天
 */
public interface RoleService extends IService<Role> {
    /**
     * 根据flag查询角色id
     */
    Integer getByFlag(String role);

}
