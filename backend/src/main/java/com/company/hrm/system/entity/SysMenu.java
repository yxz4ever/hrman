package com.company.hrm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单 ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型：directory-目录 menu-菜单 button-按钮
     */
    private String menuType;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否显示: 0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 子菜单（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysMenu> children;
}
