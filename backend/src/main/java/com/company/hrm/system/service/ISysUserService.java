package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysUser;

/**
 * 用户服务接口
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser findByUsername(String username);

    /**
     * 用户登录
     */
    String login(String username, String password);
}
