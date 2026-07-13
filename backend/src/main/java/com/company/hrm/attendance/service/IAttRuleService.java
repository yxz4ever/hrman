package com.company.hrm.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.attendance.entity.AttRule;

import java.util.List;

/**
 * 考勤规则服务接口
 */
public interface IAttRuleService extends IService<AttRule> {

    /**
     * 获取员工生效中的规则列表
     */
    List<AttRule> getEffectiveRules(Long employeeId, String workDate);

    /**
     * 创建规则
     */
    boolean createRule(AttRule rule);

    /**
     * 更新规则
     */
    boolean updateRule(AttRule rule);

    /**
     * 删除规则
     */
    boolean deleteRule(Long id);

    /**
     * 获取所有规则
     */
    List<AttRule> getAllRules();
}
