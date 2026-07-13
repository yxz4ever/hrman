package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.system.entity.SysMenu;
import com.company.hrm.system.mapper.SysMenuMapper;
import com.company.hrm.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = list();
        List<SysMenu> rootMenus = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                buildMenuTree(menu, allMenus);
                rootMenus.add(menu);
            }
        }

        // 按排序排序
        return rootMenus.stream()
                .sorted((a, b) -> (a.getSortOrder() != null ? a.getSortOrder() : 0) - (b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());
    }

    @Override
    public List<SysMenu> getChildren(Long parentId) {
        return list().stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .sorted((a, b) -> (a.getSortOrder() != null ? a.getSortOrder() : 0) - (b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());
    }

    @Override
    public boolean saveBatch(List<SysMenu> menus) {
        return saveBatch(menus);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        // 根据用户 ID 获取菜单
        List<SysMenu> allMenus = sysMenuMapper.selectByUserId(userId);
        List<SysMenu> rootMenus = new ArrayList<>();

        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                buildMenuTree(menu, allMenus);
                rootMenus.add(menu);
            }
        }

        // 按排序排序
        return rootMenus.stream()
                .sorted((a, b) -> (a.getSortOrder() != null ? a.getSortOrder() : 0) - (b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());
    }

    private void buildMenuTree(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .sorted((a, b) -> (a.getSortOrder() != null ? a.getSortOrder() : 0) - (b.getSortOrder() != null ? b.getSortOrder() : 0))
                .collect(Collectors.toList());

        for (SysMenu child : children) {
            buildMenuTree(child, allMenus);
        }

        parent.setChildren(children);
    }
}
