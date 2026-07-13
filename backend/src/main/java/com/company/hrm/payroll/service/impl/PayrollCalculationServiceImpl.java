package com.company.hrm.payroll.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.hrm.attendance.entity.AttDailyResult;
import com.company.hrm.attendance.service.IAttDailyResultService;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.service.IHrEmployeeService;
import com.company.hrm.payroll.entity.PayrollBatch;
import com.company.hrm.payroll.entity.PayrollRecord;
import com.company.hrm.payroll.entity.PayrollTemplate;
import com.company.hrm.payroll.mapper.PayrollBatchMapper;
import com.company.hrm.payroll.mapper.PayrollRecordMapper;
import com.company.hrm.payroll.service.IPayrollBatchService;
import com.company.hrm.payroll.service.IPayrollCalculationService;
import com.company.hrm.payroll.service.IPayrollRecordService;
import com.company.hrm.payroll.service.IPayrollTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 工资计算服务实现
 */
@Slf4j
@Service
public class PayrollCalculationServiceImpl implements IPayrollCalculationService {

    @Autowired
    private PayrollBatchMapper payrollBatchMapper;

    @Autowired
    private PayrollRecordMapper payrollRecordMapper;

    @Autowired
    private IHrEmployeeService employeeService;

    @Autowired
    private IPayrollTemplateService templateService;

    @Autowired
    private IAttDailyResultService dailyResultService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateBatch(Long batchId) {
        log.info("开始计算工资批次，批次ID: {}", batchId);

        PayrollBatch batch = payrollBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new RuntimeException("工资批次不存在");
        }

        // 获取所有在职员工
        List<HrEmployee> employees = employeeService.getActiveEmployees();
        log.info("找到 {} 个在职员工", employees.size());

        // 计算每个员工的工资
        for (HrEmployee employee : employees) {
            try {
                calculateEmployeeSalary(batchId, employee.getId());
            } catch (Exception e) {
                log.error("计算员工工资失败，员工ID: {}, 错误: {}", employee.getId(), e.getMessage(), e);
            }
        }

        // 更新批次状态
        batch.setStatus(1); // 已锁定
        payrollBatchMapper.updateById(batch);

        log.info("工资批次计算完成，批次ID: {}", batchId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateEmployeeSalary(Long batchId, Long employeeId) {
        log.info("开始计算员工工资，批次ID: {}, 员工ID: {}", batchId, employeeId);

        HrEmployee employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        PayrollBatch batch = payrollBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new RuntimeException("工资批次不存在");
        }

        // 调用calculatePayroll方法
        calculatePayroll(employeeId, batchId, batch.getStartDate(), batch.getEndDate());

        log.info("员工工资计算完成，员工ID: {}", employeeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayrollRecord calculatePayroll(Long employeeId, Long batchId, LocalDate startDate, LocalDate endDate) {
        log.info("开始计算员工工资，员工ID: {}, 批次ID: {}, 日期范围: {} - {}", employeeId, batchId, startDate, endDate);

        HrEmployee employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        PayrollBatch batch = payrollBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new RuntimeException("工资批次不存在");
        }

        // 获取工资模板
        PayrollTemplate template = templateService.getById(employee.getSalaryTemplateId());
        if (template == null) {
            throw new RuntimeException("工资模板不存在");
        }

        // 创建工资记录
        PayrollRecord record = new PayrollRecord();
        record.setBatchId(batchId);
        record.setEmployeeId(employeeId);
        record.setEmpNo(employee.getEmpNo());
        record.setEmpName(employee.getName());
        record.setSalaryMonth(batch.getSalaryMonth());
        record.setStartDate(startDate);
        record.setEndDate(endDate);

        // 计算基本工资
        BigDecimal baseSalary = template.getBaseSalary() != null ? template.getBaseSalary() : BigDecimal.ZERO;
        record.setBaseSalary(baseSalary);

        // 计算岗位工资
        BigDecimal postSalary = template.getPostSalary() != null ? template.getPostSalary() : BigDecimal.ZERO;
        record.setPostSalary(postSalary);

        // 计算绩效工资（这里简化处理，实际应根据绩效考核结果）
        BigDecimal performanceSalary = template.getPerformanceSalary() != null ? template.getPerformanceSalary() : BigDecimal.ZERO;
        record.setPerformanceSalary(performanceSalary);

        // 计算津贴补贴
        BigDecimal allowance = template.getAllowance() != null ? template.getAllowance() : BigDecimal.ZERO;
        record.setAllowance(allowance);

        // 计算加班费
        BigDecimal overtimeAmount = calculateOvertimeAmount(employeeId, startDate, endDate);
        record.setOvertimeAmount(overtimeAmount);

        // 计算应发合计
        BigDecimal grossSalary = baseSalary.add(postSalary)
                .add(performanceSalary)
                .add(allowance)
                .add(overtimeAmount);
        record.setGrossSalary(grossSalary);

        // 计算考勤扣款
        BigDecimal attendanceDeduction = calculateAttendanceDeduction(employeeId, startDate, endDate);
        record.setLateDeduction(attendanceDeduction);

        // 计算社保个人承担
        BigDecimal socialPersonalAmount = calculateSocialPersonalAmount(employee, template);
        record.setSocialPersonalAmount(socialPersonalAmount);

        // 计算公积金个人承担
        BigDecimal housingFundPersonalAmount = calculateHousingFundPersonalAmount(employee, template);
        record.setHousingFundPersonalAmount(housingFundPersonalAmount);

        // 计算个人所得税（简化处理）
        BigDecimal taxAmount = calculateTaxAmount(grossSalary.subtract(socialPersonalAmount).subtract(housingFundPersonalAmount));
        record.setTaxAmount(taxAmount);

        // 计算扣款合计
        BigDecimal totalDeduction = attendanceDeduction.add(socialPersonalAmount)
                .add(housingFundPersonalAmount)
                .add(taxAmount);
        record.setTotalDeduction(totalDeduction);

        // 计算实发工资
        BigDecimal netSalary = grossSalary.subtract(totalDeduction);
        record.setNetSalary(netSalary);

        record.setStatus(0); // 草稿状态

        // 保存工资记录
        payrollRecordMapper.insert(record);

        log.info("员工工资计算完成，员工ID: {}, 实发工资: {}", employeeId, netSalary);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBatch(String salaryMonth) {
        log.info("创建工资批次，工资月份: {}", salaryMonth);

        // 解析工资月份
        YearMonth yearMonth = YearMonth.parse(salaryMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 创建工资批次
        PayrollBatch batch = new PayrollBatch();
        batch.setBatchName(salaryMonth + "工资批次");
        batch.setBatchCode("BATCH_" + salaryMonth.replace("-", ""));
        batch.setSalaryMonth(yearMonth.atDay(1));
        batch.setStartDate(startDate);
        batch.setEndDate(endDate);
        batch.setStatus(0); // 草稿状态

        payrollBatchMapper.insert(batch);

        log.info("工资批次创建成功，批次ID: {}", batch.getId());
        return batch.getId();
    }

    /**
     * 计算加班费
     */
    private BigDecimal calculateOvertimeAmount(Long employeeId, LocalDate startDate, LocalDate endDate) {
        // 查询考勤结果，计算加班时长
        LambdaQueryWrapper<AttDailyResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttDailyResult::getEmployeeId, employeeId);
        wrapper.ge(AttDailyResult::getWorkDate, startDate);
        wrapper.le(AttDailyResult::getWorkDate, endDate);

        List<AttDailyResult> results = dailyResultService.list(wrapper);
        int totalOvertimeMinutes = results.stream()
                .mapToInt(r -> r.getOvertimeMinutes() != null ? r.getOvertimeMinutes() : 0)
                .sum();

        // 简化处理：按每小时50元计算加班费
        return BigDecimal.valueOf(totalOvertimeMinutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(50));
    }

    /**
     * 计算考勤扣款
     */
    private BigDecimal calculateAttendanceDeduction(Long employeeId, LocalDate startDate, LocalDate endDate) {
        // 查询考勤结果，计算迟到、早退、旷工扣款
        LambdaQueryWrapper<AttDailyResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttDailyResult::getEmployeeId, employeeId);
        wrapper.ge(AttDailyResult::getWorkDate, startDate);
        wrapper.le(AttDailyResult::getWorkDate, endDate);

        List<AttDailyResult> results = dailyResultService.list(wrapper);

        // 简化处理：迟到每次扣50元，早退每次扣50元，旷工每天扣200元
        BigDecimal deduction = BigDecimal.ZERO;
        for (AttDailyResult result : results) {
            if (result.getLateMinutes() != null && result.getLateMinutes() > 0) {
                deduction = deduction.add(BigDecimal.valueOf(50));
            }
            if (result.getEarlyLeaveMinutes() != null && result.getEarlyLeaveMinutes() > 0) {
                deduction = deduction.add(BigDecimal.valueOf(50));
            }
            if (result.getAbsentMinutes() != null && result.getAbsentMinutes() > 0) {
                deduction = deduction.add(BigDecimal.valueOf(200));
            }
        }

        return deduction;
    }

    /**
     * 计算社保个人承担
     */
    private BigDecimal calculateSocialPersonalAmount(HrEmployee employee, PayrollTemplate template) {
        if (template.getSocialPersonalRate() == null) {
            return BigDecimal.ZERO;
        }

        // 简化处理：按基本工资的固定比例计算
        return template.getBaseSalary().multiply(template.getSocialPersonalRate())
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算公积金个人承担
     */
    private BigDecimal calculateHousingFundPersonalAmount(HrEmployee employee, PayrollTemplate template) {
        if (template.getHousingFundPersonalRate() == null) {
            return BigDecimal.ZERO;
        }

        // 简化处理：按基本工资的固定比例计算
        return template.getBaseSalary().multiply(template.getHousingFundPersonalRate())
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算个人所得税（简化处理）
     */
    private BigDecimal calculateTaxAmount(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            return BigDecimal.ZERO;
        }

        // 简化处理：按10%税率计算
        BigDecimal excess = taxableIncome.subtract(BigDecimal.valueOf(5000));
        return excess.multiply(BigDecimal.valueOf(0.1))
                .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
    }
}