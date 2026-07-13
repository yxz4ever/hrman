package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.system.entity.SysRole;
import com.company.hrm.system.entity.SysRoleMenu;
import com.company.hrm.system.mapper.SysRoleMapper;
import com.company.hrm.system.mapper.SysRoleMenuMapper;
import com.company.hrm.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 删除原有菜单权限
        roleMenuMapper.deleteByRoleId(roleId);
        
        // 添加新的菜单权限
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)
        );
        return roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
