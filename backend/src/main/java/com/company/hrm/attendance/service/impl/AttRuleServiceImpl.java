package com.company.hrm.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.attendance.entity.AttRule;
import com.company.hrm.attendance.entity.AttRuleGroup;
import com.company.hrm.attendance.mapper.AttRuleGroupMapper;
import com.company.hrm.attendance.mapper.AttRuleMapper;
import com.company.hrm.attendance.service.IAttRuleService;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.mapper.HrEmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 考勤规则服务实现
 */
@Service
public class AttRuleServiceImpl extends ServiceImpl<AttRuleMapper, AttRule> implements IAttRuleService {

    @Autowired
    private AttRuleMapper attRuleMapper;

    @Autowired
    private AttRuleGroupMapper attRuleGroupMapper;

    @Autowired
    private HrEmployeeMapper employeeMapper;

    @Override
    public List<AttRule> getEffectiveRules(Long employeeId, String workDate) {
        try {
            // 1. 查询员工信息
            HrEmployee employee = employeeMapper.selectById(employeeId);
            if (employee == null) {
                return new ArrayList<>();
            }

            // 2. 解析工作日期
            LocalDate date = LocalDate.parse(workDate);

            // 3. 查询员工绑定的规则组
            Long ruleGroupId = employee.getRuleGroupId();
            if (ruleGroupId == null) {
                // 如果员工没有绑定规则组，查询默认规则组
                LambdaQueryWrapper<AttRuleGroup> groupWrapper = new LambdaQueryWrapper<>();
                groupWrapper.eq(AttRuleGroup::getStatus, 1);
                groupWrapper.eq(AttRuleGroup::getApplicableScope, 1); // 全部员工
                groupWrapper.le(AttRuleGroup::getEffectiveStartDate, date);
                groupWrapper.and(wrapper -> wrapper.isNull(AttRuleGroup::getEffectiveEndDate)
                        .or().ge(AttRuleGroup::getEffectiveEndDate, date));
                groupWrapper.orderByDesc(AttRuleGroup::getPriority);

                AttRuleGroup defaultGroup = attRuleGroupMapper.selectOne(groupWrapper);
                if (defaultGroup != null) {
                    ruleGroupId = defaultGroup.getId();
                } else {
                    // 如果没有默认规则组，返回所有启用的规则
                    return getAllEnabledRules();
                }
            }

            // 4. 查询规则组中的规则
            if (ruleGroupId != null) {
                LambdaQueryWrapper<AttRule> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(AttRule::getStatus, 1);
                wrapper.orderByAsc(AttRule::getPriority);
                return list(wrapper);
            }

            return new ArrayList<>();
        } catch (Exception e) {
            // 发生异常时返回所有启用的规则
            return getAllEnabledRules();
        }
    }

    /**
     * 获取所有启用的规则
     */
    private List<AttRule> getAllEnabledRules() {
        LambdaQueryWrapper<AttRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttRule::getStatus, 1);
        wrapper.orderByAsc(AttRule::getPriority);
        return list(wrapper);
    }

    @Override
    public boolean createRule(AttRule rule) {
        return save(rule);
    }

    @Override
    public boolean updateRule(AttRule rule) {
        return updateById(rule);
    }

    @Override
    public boolean deleteRule(Long id) {
        return removeById(id);
    }

    @Override
    public List<AttRule> getAllRules() {
        return list();
    }
}
