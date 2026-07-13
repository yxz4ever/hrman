package com.company.hrm.payroll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.payroll.entity.InsurancePolicy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社保政策Mapper
 */
@Mapper
public interface InsurancePolicyMapper extends BaseMapper<InsurancePolicy> {
}