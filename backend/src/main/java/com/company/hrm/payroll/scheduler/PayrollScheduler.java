package com.company.hrm.payroll.scheduler;

import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.mapper.AttDailyResultMapper;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.mapper.HrEmployeeMapper;
import com.company.hrm.payroll.entity.PayrollBatch;
import com.company.hrm.payroll.entity.PayrollRecord;
import com.company.hrm.payroll.mapper.PayrollBatchMapper;
import com.company.hrm.payroll.mapper.PayrollRecordMapper;
import com.company.hrm.payroll.service.IPayrollCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 薪资定时任务
 */
@Component
public class PayrollScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PayrollScheduler.class);

    @Autowired
    private IPayrollCalculationService payrollCalculationService;

    @Autowired
    private HrEmployeeMapper employeeMapper;

    @Autowired
    private AttDailyResultMapper dailyResultMapper;

    @Autowired
    private PayrollBatchMapper payrollBatchMapper;

    @Autowired
    private PayrollRecordMapper payrollRecordMapper;

    /**
     * 每月工资计算
     * 每月5号凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 5 * ?")
    public void calculateMonthlySalary() {
        logger.info("开始执行每月工资计算任务");
        try {
            // 1. 获取上个月的日期范围
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = now.withDayOfMonth(1).minusDays(1);

            // 2. 创建工资批次
            PayrollBatch batch = new PayrollBatch();
            batch.setBatchName(startDate.getMonthValue() + "月工资");
            batch.setStartDate(startDate);
            batch.setEndDate(endDate);
            batch.setStatus(0); // 计算中
            batch.setCreateTime(LocalDateTime.now());
            payrollBatchMapper.insert(batch);

            // 3. 获取所有在职员工
            List<HrEmployee> employees = employeeMapper.selectActiveEmployees();

            if (employees == null || employees.isEmpty()) {
                logger.info("没有在职员工，跳过工资计算");
                batch.setStatus(2); // 已完成
                payrollBatchMapper.updateById(batch);
                return;
            }

            // 4. 为每个员工计算工资
            int successCount = 0;
            int failCount = 0;
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (HrEmployee employee : employees) {
                try {
                    // 5. 查询员工上个月的考勤记录
                    List<AttDailyResult> dailyResults = dailyResultMapper.selectByEmployeeAndDateRange(
                            employee.getId(), startDate, endDate);

                    if (dailyResults == null || dailyResults.isEmpty()) {
                        logger.warn("员工{}没有考勤记录，跳过工资计算", employee.getEmpNo());
                        failCount++;
                        continue;
                    }

                    // 6. 计算工资
                    PayrollRecord record = payrollCalculationService.calculatePayroll(
                            employee.getId(), batch.getId(), startDate, endDate);

                    if (record != null) {
                        // 7. 保存工资记录
                        payrollRecordMapper.insert(record);
                        totalAmount = totalAmount.add(record.getNetSalary());
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    logger.error("计算员工{}的工资失败", employee.getEmpNo(), e);
                    failCount++;
                }
            }

            // 8. 更新批次状态
            batch.setStatus(2); // 已完成
            batch.setTotalAmount(totalAmount);
            batch.setEmployeeCount(successCount);
            batch.setUpdateTime(LocalDateTime.now());
            payrollBatchMapper.updateById(batch);

            logger.info("每月工资计算任务执行完成，成功{}人，失败{}人，总金额{}元",
                    successCount, failCount, totalAmount);
        } catch (Exception e) {
            logger.error("每月工资计算任务执行失败", e);
        }
    }

    /**
     * 每月社保汇总
     * 每月10号凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 10 * ?")
    public void calculateMonthlyInsurance() {
        logger.info("开始执行每月社保汇总任务");
        try {
            // 1. 获取上个月的日期范围
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.minusMonths(1).withDayOfMonth(1);
            LocalDate endDate = now.withDayOfMonth(1).minusDays(1);

            // 2. 获取所有在职员工
            List<HrEmployee> employees = employeeMapper.selectActiveEmployees();

            if (employees == null || employees.isEmpty()) {
                logger.info("没有在职员工，跳过社保计算");
                return;
            }

            // 3. 为每个员工计算社保
            int successCount = 0;
            int failCount = 0;

            for (HrEmployee employee : employees) {
                try {
                    // 4. 查询员工上个月的考勤记录
                    List<AttDailyResult> dailyResults = dailyResultMapper.selectByEmployeeAndDateRange(
                            employee.getId(), startDate, endDate);

                    if (dailyResults == null || dailyResults.isEmpty()) {
                        logger.warn("员工{}没有考勤记录，跳过社保计算", employee.getEmpNo());
                        failCount++;
                        continue;
                    }

                    // 5. 计算社保（这里可以调用社保计算服务）
                    // TODO: 实现社保计算逻辑
                    successCount++;
                } catch (Exception e) {
                    logger.error("计算员工{}的社保失败", employee.getEmpNo(), e);
                    failCount++;
                }
            }

            logger.info("每月社保汇总任务执行完成，成功{}人，失败{}人", successCount, failCount);
        } catch (Exception e) {
            logger.error("每月社保汇总任务执行失败", e);
        }
    }
}
