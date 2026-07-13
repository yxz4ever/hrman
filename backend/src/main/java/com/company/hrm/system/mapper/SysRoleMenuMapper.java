package com.company.hrm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色菜单 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色 ID 删除
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
