package com.company.hrm.attendance.engine;

import com.company.hrm.attendance.entity.AttClockRecord;
import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.entity.AttRule;
import com.company.hrm.attendance.engine.impl.FixedTimeRuleCalculator;
import com.company.hrm.attendance.engine.impl.FlexibleRuleCalculator;
import com.company.hrm.attendance.mapper.AttClockRecordMapper;
import com.company.hrm.attendance.mapper.AttDailyResultMapper;
import com.company.hrm.attendance.service.IAttRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤计算引擎
 */
@Service
public class AttendanceEngine {
    
    @Autowired
    private IAttRuleService ruleService;
    
    @Autowired
    private AttClockRecordMapper clockRecordMapper;
    
    @Autowired
    private AttDailyResultMapper dailyResultMapper;
    
    @Autowired
    private FixedTimeRuleCalculator fixedTimeRuleCalculator;
    
    @Autowired
    private FlexibleRuleCalculator flexibleRuleCalculator;
    
    private final Map<String, AttendanceCalculator> calculators = new HashMap<>();
    
    public AttendanceEngine() {
        // 注册计算器 - 使用Integer类型作为key
        calculators.put("1", new FixedTimeRuleCalculator()); // 1-固定班制
        calculators.put("2", new FlexibleRuleCalculator()); // 2-弹性工时
    }
    
    /**
     * 计算员工某日的考勤结果
     * 核心逻辑：员工当日打卡只要满足规则组中的任意一条规则，即判定"正常出勤"
     */
    public AttDailyResult calculateDailyAttendance(Long employeeId, LocalDate workDate) {
        // 1. 获取员工当日打卡记录
        List<AttClockRecord> clockRecords = clockRecordMapper.selectByEmployeeAndDate(employeeId, workDate);
        
        // 2. 获取员工生效中的规则组
        List<AttRule> rules = ruleService.getEffectiveRules(employeeId, workDate.toString());
        
        if (rules.isEmpty()) {
            return buildFailResult(employeeId, workDate, "未配置考勤规则");
        }
        
        // 3. 遍历规则组中的所有规则，逐个计算是否满足
        AttendanceResult finalResult = null;
        for (AttRule rule : rules) {
            AttendanceCalculator calculator = getCalculator(rule.getRuleType());
            if (calculator == null) {
                continue;
            }
            
            AttendanceResult result = calculator.calculate(rule, clockRecords, workDate.toString());
            
            // 若任一规则通过，则该日考勤结果为"正常"
            if (result.isPass()) {
                finalResult = result;
                break;
            }
        }
        
        // 4. 若全部规则都不通过，构建失败结果
        if (finalResult == null) {
            finalResult = AttendanceResult.fail("异常", "所有规则均未通过");
        }
        
        // 5. 构建并保存考勤结果
        return buildDailyResult(employeeId, workDate, clockRecords, finalResult);
    }
    
    /**
     * 根据规则类型获取对应的计算器
     */
    private AttendanceCalculator getCalculator(Integer ruleType) {
        if (ruleType == null) {
            return null;
        }
        // 将Integer类型的ruleType转换为String
        String ruleTypeStr = ruleType.toString();
        return calculators.get(ruleTypeStr);
    }
    
    /**
     * 构建每日考勤结果
     */
    private AttDailyResult buildDailyResult(Long employeeId, LocalDate workDate,
                                           List<AttClockRecord> clockRecords,
                                           AttendanceResult result) {
        AttDailyResult dailyResult = new AttDailyResult();
        dailyResult.setEmployeeId(employeeId);
        dailyResult.setWorkDate(workDate);
        dailyResult.setAttendanceStatus(result.getAttendanceStatus());
        dailyResult.setRemark(result.getExplanation()); // 使用remark字段存储说明
        dailyResult.setHitRuleId(result.getHitRuleId());
        dailyResult.setWorkMinutes(result.getWorkMinutes());
        dailyResult.setLateMinutes(result.getLateMinutes());
        dailyResult.setEarlyLeaveMinutes(result.getEarlyLeaveMinutes());
        dailyResult.setAbsentMinutes(result.getAbsentMinutes());
        dailyResult.setOvertimeMinutes(result.getOvertimeMinutes());
        dailyResult.setCalculatedAt(LocalDateTime.now());

        // 设置首次打卡和最后打卡时间
        if (!clockRecords.isEmpty()) {
            AttClockRecord firstClockIn = clockRecords.stream()
                    .filter(r -> "上班".equals(r.getClockType()))
                    .min((r1, r2) -> r1.getClockTime().compareTo(r2.getClockTime()))
                    .orElse(null);

            AttClockRecord lastClockOut = clockRecords.stream()
                    .filter(r -> "下班".equals(r.getClockType()))
                    .max((r1, r2) -> r1.getClockTime().compareTo(r2.getClockTime()))
                    .orElse(null);

            if (firstClockIn != null) {
                dailyResult.setFirstClockIn(firstClockIn.getClockTime());
            }
            if (lastClockOut != null) {
                dailyResult.setLastClockOut(lastClockOut.getClockTime());
            }
        }

        return dailyResult;
    }
    
    /**
     * 构建失败结果
     */
    private AttDailyResult buildFailResult(Long employeeId, LocalDate workDate, String reason) {
        AttDailyResult dailyResult = new AttDailyResult();
        dailyResult.setEmployeeId(employeeId);
        dailyResult.setWorkDate(workDate);
        dailyResult.setAttendanceStatus(5); // 旷工
        dailyResult.setRemark(reason);
        dailyResult.setCalculatedAt(LocalDateTime.now());
        return dailyResult;
    }
}