package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树
     */
    List<SysMenu> getMenuTree();

    /**
     * 获取子菜单
     */
    List<SysMenu> getChildren(Long parentId);

    /**
     * 批量保存
     */
    boolean saveBatch(List<SysMenu> menus);

    /**
     * 根据用户 ID 获取菜单树（根据角色过滤）
     */
    List<SysMenu> getUserMenuTree(Long userId);
}
