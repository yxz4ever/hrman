package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色的菜单权限
     */
    List<Long> getRoleMenus(Long roleId);
}
