package com.company.hrm.payroll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.payroll.entity.PayrollRecord;
import com.company.hrm.payroll.mapper.PayrollRecordMapper;
import com.company.hrm.payroll.service.IPayrollRecordService;
import org.springframework.stereotype.Service;

/**
 * 工资记录服务实现
 */
@Service
public class PayrollRecordServiceImpl extends ServiceImpl<PayrollRecordMapper, PayrollRecord> implements IPayrollRecordService {
}
