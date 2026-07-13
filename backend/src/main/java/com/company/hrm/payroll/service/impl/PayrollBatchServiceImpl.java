package com.company.hrm.payroll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.payroll.entity.PayrollBatch;
import com.company.hrm.payroll.mapper.PayrollBatchMapper;
import com.company.hrm.payroll.service.IPayrollBatchService;
import org.springframework.stereotype.Service;

/**
 * 工资批次服务实现
 */
@Service
public class PayrollBatchServiceImpl extends ServiceImpl<PayrollBatchMapper, PayrollBatch> implements IPayrollBatchService {
}
