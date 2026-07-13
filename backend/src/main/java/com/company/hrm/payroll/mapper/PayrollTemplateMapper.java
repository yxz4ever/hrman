package com.company.hrm.payroll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.payroll.entity.PayrollTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工资模板Mapper
 */
@Mapper
public interface PayrollTemplateMapper extends BaseMapper<PayrollTemplate> {
}