package com.company.hrm.payroll.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.payroll.entity.PayrollBatch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工资批次Mapper
 */
@Mapper
public interface PayrollBatchMapper extends BaseMapper<PayrollBatch> {
}