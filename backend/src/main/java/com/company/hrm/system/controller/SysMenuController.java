package com.company.hrm.system.controller;

import com.company.hrm.system.entity.SysMenu;
import com.company.hrm.system.service.ISysMenuService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 菜单管理
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单树
     */
    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> tree = menuService.getMenuTree();
        return Result.success(tree);
    }

    /**
     * 获取当前用户的菜单树
     */
    @Operation(summary = "获取当前用户的菜单树")
    @GetMapping("/user/tree")
    public Result<List<SysMenu>> userTree() {
        // TODO: 从当前登录用户获取 userId
        Long userId = 1L; // 临时使用 admin 用户 ID
        List<SysMenu> tree = menuService.getUserMenuTree(userId);
        return Result.success(tree);
    }

    /**
     * 获取所有菜单
     */
    @Operation(summary = "获取所有菜单")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        List<SysMenu> list = menuService.list();
        return Result.success(list);
    }

    /**
     * 获取菜单详情
     */
    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public Result<SysMenu> get(@PathVariable Long id) {
        SysMenu menu = menuService.getById(id);
        return Result.success(menu);
    }

    /**
     * 添加菜单
     */
    @Operation(summary = "添加菜单")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> update(@RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查是否有子菜单
        List<SysMenu> children = menuService.getChildren(id);
        if (children != null && !children.isEmpty()) {
            return Result.error("请先删除子菜单");
        }
        menuService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除菜单
     */
    @Operation(summary = "批量删除菜单")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        menuService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 获取子菜单
     */
    @Operation(summary = "获取子菜单")
    @GetMapping("/{id}/children")
    public Result<List<SysMenu>> getChildren(@PathVariable Long id) {
        List<SysMenu> children = menuService.getChildren(id);
        return Result.success(children);
    }

    /**
     * 导入菜单（JSON）
     */
    @Operation(summary = "导入菜单")
    @PostMapping("/import")
    public Result<?> importMenus(@RequestBody List<SysMenu> menus) {
        try {
            menuService.saveBatch(menus);
            return Result.success();
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导出菜单（Excel）
     */
    @Operation(summary = "导出菜单")
    @GetMapping("/export")
    public void exportMenus(
            @RequestParam(defaultValue = "all") String scope,
            HttpServletResponse response) {
        
        try {
            List<SysMenu> menus = menuService.getMenuTree();
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("菜单数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            try (Workbook workbook = new XSSFWorkbook();
                 OutputStream os = response.getOutputStream()) {
                
                Sheet sheet = workbook.createSheet("菜单");
                
                // 表头
                Row header = sheet.createRow(0);
                String[] headers = {"菜单名称", "类型", "父级菜单", "路由路径", "组件路径", "权限标识", "图标", "排序", "状态"};
                for (int i = 0; i < headers.length; i++) {
                    header.createCell(i).setCellValue(headers[i]);
                }
                
                // 数据
                int rowNum = 1;
                for (SysMenu menu : menus) {
                    rowNum = writeMenuRow(workbook, sheet, menu, rowNum, "");
                }
                
                workbook.write(os);
            }
            // 注意：不要调用 Result.success()，因为已经写入了文件流
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }
    
    private int writeMenuRow(Workbook workbook, Sheet sheet, SysMenu menu, int rowNum, String prefix) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(prefix + menu.getMenuName());
        row.createCell(1).setCellValue(menu.getMenuType());
        row.createCell(2).setCellValue(menu.getParentId() != null ? menu.getParentId().toString() : "");
        row.createCell(3).setCellValue(menu.getPath() != null ? menu.getPath() : "");
        row.createCell(4).setCellValue(menu.getComponent() != null ? menu.getComponent() : "");
        row.createCell(5).setCellValue(menu.getPermission() != null ? menu.getPermission() : "");
        row.createCell(6).setCellValue(menu.getIcon() != null ? menu.getIcon() : "");
        row.createCell(7).setCellValue(menu.getSortOrder() != null ? menu.getSortOrder() : 0);
        row.createCell(8).setCellValue(menu.getStatus());
        
        // 递归写入子菜单
        if (menu.getChildren() != null) {
            for (SysMenu child : menu.getChildren()) {
                rowNum = writeMenuRow(workbook, sheet, child, rowNum, prefix + "  ");
            }
        }
        
        return rowNum;
    }
}
