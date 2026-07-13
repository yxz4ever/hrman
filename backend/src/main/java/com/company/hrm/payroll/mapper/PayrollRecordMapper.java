package com.company.hrm.payroll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.payroll.entity.PayrollRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工资记录Mapper
 */
@Mapper
public interface PayrollRecordMapper extends BaseMapper<PayrollRecord> {
}