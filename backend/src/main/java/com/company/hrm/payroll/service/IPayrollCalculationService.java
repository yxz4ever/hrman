package com.company.hrm.payroll.service;

import com.company.hrm.payroll.entity.PayrollBatch;
import com.company.hrm.payroll.entity.PayrollRecord;

import java.time.LocalDate;

/**
 * 工资批次服务接口
 */
public interface IPayrollCalculationService {

    /**
     * 计算工资批次
     * @param batchId 批次ID
     */
    void calculateBatch(Long batchId);

    /**
     * 计算员工工资
     * @param batchId 批次ID
     * @param employeeId 员工ID
     */
    void calculateEmployeeSalary(Long batchId, Long employeeId);

    /**
     * 计算员工工资（返回工资记录）
     * @param employeeId 员工ID
     * @param batchId 批次ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 工资记录
     */
    PayrollRecord calculatePayroll(Long employeeId, Long batchId, LocalDate startDate, LocalDate endDate);

    /**
     * 创建工资批次
     * @param salaryMonth 工资月份
     * @return 批次ID
     */
    Long createBatch(String salaryMonth);
}