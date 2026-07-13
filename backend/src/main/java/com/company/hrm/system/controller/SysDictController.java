package com.company.hrm.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.common.Result;
import com.company.hrm.system.entity.SysDictItem;
import com.company.hrm.system.entity.SysDictType;
import com.company.hrm.system.service.ISysDictItemService;
import com.company.hrm.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典管理 Controller
 */
@Tag(name = "字典管理", description = "字典类型和字典数据管理")
@RestController
@RequestMapping("/system/dict")
public class SysDictController {
    
    @Autowired
    private ISysDictTypeService dictTypeService;
    
    @Autowired
    private ISysDictItemService dictItemService;
    
    /**
     * 获取所有字典类型
     */
    @Operation(summary = "获取所有字典类型")
    @GetMapping("/type/list")
    public Result<List<SysDictType>> getTypeList() {
        List<SysDictType> list = dictTypeService.getAllDictTypes();
        return Result.success(list);
    }
    
    /**
     * 获取字典类型详情
     */
    @Operation(summary = "获取字典类型详情")
    @GetMapping("/type/{id}")
    public Result<SysDictType> getType(@PathVariable Long id) {
        SysDictType dictType = dictTypeService.getById(id);
        return Result.success(dictType);
    }
    
    /**
     * 新增字典类型
     */
    @Operation(summary = "新增字典类型")
    @PostMapping("/type")
    public Result<Boolean> addType(@RequestBody SysDictType dictType) {
        boolean success = dictTypeService.save(dictType);
        return Result.success(success);
    }
    
    /**
     * 更新字典类型
     */
    @Operation(summary = "更新字典类型")
    @PutMapping("/type/{id}")
    public Result<Boolean> updateType(@PathVariable Long id, @RequestBody SysDictType dictType) {
        dictType.setId(id);
        boolean success = dictTypeService.updateById(dictType);
        return Result.success(success);
    }
    
    /**
     * 删除字典类型
     */
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    public Result<Boolean> deleteType(@PathVariable Long id) {
        boolean success = dictTypeService.removeById(id);
        return Result.success(success);
    }
    
    /**
     * 获取字典数据列表
     */
    @Operation(summary = "获取字典数据列表")
    @GetMapping("/item/list")
    public Result<List<SysDictItem>> getItemList(@RequestParam Long dictTypeId) {
        List<SysDictItem> list = dictItemService.getByDictTypeId(dictTypeId);
        return Result.success(list);
    }
    
    /**
     * 根据字典编码获取字典数据
     */
    @Operation(summary = "根据字典编码获取字典数据")
    @GetMapping("/item/by-code/{dictCode}")
    public Result<List<SysDictItem>> getItemByCode(@PathVariable String dictCode) {
        List<SysDictItem> list = dictItemService.getByDictCode(dictCode);
        return Result.success(list);
    }
    
    /**
     * 获取字典详情
     */
    @Operation(summary = "获取字典详情")
    @GetMapping("/item/{id}")
    public Result<SysDictItem> getItem(@PathVariable Long id) {
        SysDictItem dictItem = dictItemService.getById(id);
        return Result.success(dictItem);
    }
    
    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据")
    @PostMapping("/item")
    public Result<Boolean> addItem(@RequestBody SysDictItem dictItem) {
        boolean success = dictItemService.save(dictItem);
        return Result.success(success);
    }
    
    /**
     * 更新字典数据
     */
    @Operation(summary = "更新字典数据")
    @PutMapping("/item/{id}")
    public Result<Boolean> updateItem(@PathVariable Long id, @RequestBody SysDictItem dictItem) {
        dictItem.setId(id);
        boolean success = dictItemService.updateById(dictItem);
        return Result.success(success);
    }
    
    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @DeleteMapping("/item/{id}")
    public Result<Boolean> deleteItem(@PathVariable Long id) {
        boolean success = dictItemService.removeById(id);
        return Result.success(success);
    }
    
    /**
     * 获取所有启用的字典数据（用于前端缓存）
     */
    @Operation(summary = "获取所有启用的字典数据")
    @GetMapping("/item/enabled")
    public Result<List<SysDictItem>> getEnabledItems() {
        List<SysDictItem> list = dictItemService.getEnabledDictItems();
        return Result.success(list);
    }
}
