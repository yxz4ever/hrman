package com.company.hrm.attendance.engine;

import com.company.hrm.attendance.entity.AttRule;
import com.company.hrm.attendance.engine.impl.FixedTimeRuleCalculator;
import com.company.hrm.attendance.engine.impl.FlexibleRuleCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤计算器工厂
 */
@Component
public class AttendanceCalculatorFactory {

    @Autowired
    private FixedTimeRuleCalculator fixedTimeRuleCalculator;

    @Autowired
    private FlexibleRuleCalculator flexibleRuleCalculator;

    private Map<Integer, AttendanceCalculator> calculatorMap = new HashMap<>();

    /**
     * 根据规则类型获取计算器
     */
    public AttendanceCalculator getCalculator(Integer ruleType) {
        if (calculatorMap.isEmpty()) {
            initCalculators();
        }
        return calculatorMap.get(ruleType);
    }

    /**
     * 初始化计算器映射
     */
    private void initCalculators() {
        calculatorMap.put(1, fixedTimeRuleCalculator); // 固定班制
        calculatorMap.put(2, flexibleRuleCalculator); // 弹性工时
        // 可以继续添加其他规则类型的计算器
    }
}
