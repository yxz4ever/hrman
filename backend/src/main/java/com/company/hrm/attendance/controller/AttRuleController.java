package com.company.hrm.attendance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hrm.common.Result;
import com.company.hrm.attendance.entity.AttRule;
import com.company.hrm.attendance.service.IAttRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤规则控制器
 */
@RestController
@RequestMapping("/attendance/rules")
public class AttRuleController {

    @Autowired
    private IAttRuleService attRuleService;

    /**
     * 获取所有规则
     */
    @GetMapping
    public Result<List<AttRule>> getList() {
        LambdaQueryWrapper<AttRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttRule::getStatus, 1);
        wrapper.orderByAsc(AttRule::getPriority);
        List<AttRule> list = attRuleService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/{id}")
    public Result<AttRule> getById(@PathVariable Long id) {
        AttRule rule = attRuleService.getById(id);
        return Result.success(rule);
    }

    /**
     * 创建规则
     */
    @PostMapping
    public Result<String> create(@RequestBody AttRule rule) {
        attRuleService.save(rule);
        return Result.success("创建成功");
    }

    /**
     * 更新规则
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody AttRule rule) {
        rule.setId(id);
        attRuleService.updateById(rule);
        return Result.success("更新成功");
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        attRuleService.removeById(id);
        return Result.success("删除成功");
    }
}
