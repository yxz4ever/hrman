package com.company.hrm.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.system.entity.SysRole;
import com.company.hrm.system.service.ISysRoleService;
import com.company.hrm.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<IPage<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName) {

        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (roleName != null && !roleName.isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName);
        }

        wrapper.orderByDesc(SysRole::getCreatedAt);
        IPage<SysRole> result = roleService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有角色
     */
    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        List<SysRole> list = roleService.list();
        return Result.success(list);
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 添加角色
     */
    @Operation(summary = "添加角色")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (roleService.count(wrapper) > 0) {
            return Result.error("角色编码已存在");
        }
        roleService.save(role);
        return Result.success();
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色")
    @PutMapping
    public Result<Void> update(@RequestBody SysRole role) {
        // 检查角色编码是否已被其他角色使用
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        wrapper.ne(SysRole::getId, role.getId());
        if (roleService.count(wrapper) > 0) {
            return Result.error("角色编码已存在");
        }
        roleService.updateById(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        roleService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 分配菜单权限
     */
    @Operation(summary = "分配菜单权限")
    @PostMapping("/{roleId}/menus")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.success();
    }

    /**
     * 获取角色的菜单权限
     */
    @Operation(summary = "获取角色的菜单权限")
    @GetMapping("/{roleId}/menus")
    public Result<List<Long>> getRoleMenus(@PathVariable Long roleId) {
        List<Long> menuIds = roleService.getRoleMenus(roleId);
        return Result.success(menuIds);
    }

    /**
     * 导入角色（Excel）
     */
    @Operation(summary = "导入角色")
    @PostMapping("/import")
    public Result<?> importRoles(@RequestParam("file") MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            int successCount = 0;
            int failCount = 0;
            
            // 跳过表头
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    SysRole role = new SysRole();
                    role.setRoleName(getCellValue(row.getCell(0)));
                    role.setRoleCode(getCellValue(row.getCell(1)));
                    role.setDescription(getCellValue(row.getCell(2)));
                    role.setStatus((int) getCellValue(row.getCell(3), 1));
                    
                    // 检查角色编码是否已存在
                    LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
                    if (roleService.count(wrapper) == 0) {
                        roleService.save(role);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            return Result.success(new Object[]{successCount, failCount});
        } catch (IOException e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.toString().trim();
    }
    
    private Object getCellValue(Cell cell, Object defaultValue) {
        if (cell == null) return defaultValue;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            return cell.toString().trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 导出角色（Excel）
     */
    @Operation(summary = "导出角色")
    @GetMapping("/export")
    public void exportRoles(
            @RequestParam(required = false) String ids,
            @RequestParam(defaultValue = "all") String scope,
            @RequestParam(required = false) String fields,
            HttpServletResponse response) {
        
        try {
            List<SysRole> roles = new ArrayList<>();
            
            if ("selected".equals(scope) && ids != null) {
                String[] idArray = ids.split(",");
                for (String id : idArray) {
                    roles.add(roleService.getById(Long.parseLong(id)));
                }
            } else {
                roles = roleService.list();
            }
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("角色数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            try (Workbook workbook = new XSSFWorkbook();
                 OutputStream os = response.getOutputStream()) {
                
                Sheet sheet = workbook.createSheet("角色");
                
                // 表头
                Row header = sheet.createRow(0);
                String[] headers = {"角色名称", "角色编码", "描述", "状态", "创建时间"};
                for (int i = 0; i < headers.length; i++) {
                    header.createCell(i).setCellValue(headers[i]);
                }
                
                // 数据
                for (int i = 0; i < roles.size(); i++) {
                    SysRole role = roles.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(role.getRoleName());
                    row.createCell(1).setCellValue(role.getRoleCode());
                    row.createCell(2).setCellValue(role.getDescription() != null ? role.getDescription() : "");
                    row.createCell(3).setCellValue(role.getStatus());
                    row.createCell(4).setCellValue(role.getCreatedAt() != null ? role.getCreatedAt().toString() : "");
                }
                
                workbook.write(os);
            }
            // 注意：不要调用 Result.success()，因为已经写入了文件流
        } catch (IOException e) {
            throw new RuntimeException("导出失败", e);
        }
    }
}
