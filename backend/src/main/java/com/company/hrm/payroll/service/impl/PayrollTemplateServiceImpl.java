package com.company.hrm.payroll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.payroll.entity.PayrollTemplate;
import com.company.hrm.payroll.mapper.PayrollTemplateMapper;
import com.company.hrm.payroll.service.IPayrollTemplateService;
import org.springframework.stereotype.Service;

/**
 * 工资模板服务实现
 */
@Service
public class PayrollTemplateServiceImpl extends ServiceImpl<PayrollTemplateMapper, PayrollTemplate> implements IPayrollTemplateService {
}
