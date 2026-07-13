package com.company.hrm.payroll.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.payroll.entity.InsurancePolicy;
import com.company.hrm.payroll.mapper.InsurancePolicyMapper;
import com.company.hrm.payroll.service.IInsurancePolicyService;
import org.springframework.stereotype.Service;

/**
 * 社保政策服务实现
 */
@Service
public class InsurancePolicyServiceImpl extends ServiceImpl<InsurancePolicyMapper, InsurancePolicy> implements IInsurancePolicyService {
}
