package com.company.hrm.attendance.scheduler;

import com.company.hrm.attendance.engine.AttendanceEngine;
import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.mapper.AttDailyResultMapper;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.mapper.HrEmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤定时任务
 */
@Component
public class AttendanceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceScheduler.class);

    @Autowired
    private AttendanceEngine attendanceEngine;

    @Autowired
    private HrEmployeeMapper employeeMapper;

    @Autowired
    private AttDailyResultMapper dailyResultMapper;

    /**
     * 每日考勤结算
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void calculateDailyAttendance() {
        logger.info("开始执行每日考勤结算任务");
        try {
            // 1. 获取所有在职员工
            List<HrEmployee> employees = employeeMapper.selectActiveEmployees();

            if (employees == null || employees.isEmpty()) {
                logger.info("没有在职员工，跳过考勤结算");
                return;
            }

            // 2. 计算昨天的考勤结果
            LocalDate workDate = LocalDate.now().minusDays(1);
            int successCount = 0;
            int failCount = 0;

            for (HrEmployee employee : employees) {
                try {
                    // 3. 使用考勤引擎计算考勤结果
                    AttDailyResult result = attendanceEngine.calculateDailyAttendance(
                            employee.getId(), workDate);

                    // 4. 保存或更新考勤结果
                    AttDailyResult existResult = dailyResultMapper.selectByEmployeeAndDate(
                            employee.getId(), workDate);
                    if (existResult != null) {
                        result.setId(existResult.getId());
                        dailyResultMapper.updateById(result);
                    } else {
                        dailyResultMapper.insert(result);
                    }

                    successCount++;
                } catch (Exception e) {
                    logger.error("计算员工{}的考勤失败", employee.getEmpNo(), e);
                    failCount++;
                }
            }

            logger.info("每日考勤结算任务执行完成，成功{}人，失败{}人", successCount, failCount);
        } catch (Exception e) {
            logger.error("每日考勤结算任务执行失败", e);
        }
    }

    /**
     * 每月考勤汇总
     * 每月1号凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void calculateMonthlyAttendance() {
        logger.info("开始执行每月考勤汇总任务");
        try {
            // 1. 获取上个月的日期范围
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = now.withDayOfMonth(1).minusDays(1);

            // 2. 获取所有在职员工
            List<HrEmployee> employees = employeeMapper.selectActiveEmployees();

            if (employees == null || employees.isEmpty()) {
                logger.info("没有在职员工，跳过考勤汇总");
                return;
            }

            // 3. 为每个员工汇总月度考勤数据
            int successCount = 0;
            int failCount = 0;

            for (HrEmployee employee : employees) {
                try {
                    // 4. 查询员工在指定日期范围内的考勤记录
                    List<AttDailyResult> dailyResults = dailyResultMapper.selectByEmployeeAndDateRange(
                            employee.getId(), startDate, endDate);

                    if (dailyResults != null && !dailyResults.isEmpty()) {
                        // 5. 汇总考勤数据
                        int totalDays = dailyResults.size();
                        int normalDays = (int) dailyResults.stream()
                                .filter(r -> r.getAttendanceStatus() == 1)
                                .count();
                        int lateDays = (int) dailyResults.stream()
                                .filter(r -> r.getAttendanceStatus() == 2)
                                .count();
                        int earlyLeaveDays = (int) dailyResults.stream()
                                .filter(r -> r.getAttendanceStatus() == 3)
                                .count();
                        int absentDays = (int) dailyResults.stream()
                                .filter(r -> r.getAttendanceStatus() == 5)
                                .count();

                        int totalWorkMinutes = dailyResults.stream()
                                .mapToInt(r -> r.getWorkMinutes() != null ? r.getWorkMinutes() : 0)
                                .sum();
                        int totalLateMinutes = dailyResults.stream()
                                .mapToInt(r -> r.getLateMinutes() != null ? r.getLateMinutes() : 0)
                                .sum();
                        int totalEarlyLeaveMinutes = dailyResults.stream()
                                .mapToInt(r -> r.getEarlyLeaveMinutes() != null ? r.getEarlyLeaveMinutes() : 0)
                                .sum();
                        int totalOvertimeMinutes = dailyResults.stream()
                                .mapToInt(r -> r.getOvertimeMinutes() != null ? r.getOvertimeMinutes() : 0)
                                .sum();

                        logger.info("员工{}月度考勤汇总：总天数{}，正常{}，迟到{}，早退{}，旷工{}，工作时长{}分钟",
                                employee.getEmpNo(), totalDays, normalDays, lateDays, earlyLeaveDays,
                                absentDays, totalWorkMinutes);
                    }

                    successCount++;
                } catch (Exception e) {
                    logger.error("汇总员工{}的月度考勤失败", employee.getEmpNo(), e);
                    failCount++;
                }
            }

            logger.info("每月考勤汇总任务执行完成，成功{}人，失败{}人", successCount, failCount);
        } catch (Exception e) {
            logger.error("每月考勤汇总任务执行失败", e);
        }
    }
}
