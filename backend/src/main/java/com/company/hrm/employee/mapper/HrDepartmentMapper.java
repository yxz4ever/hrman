package com.company.hrm.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.employee.entity.HrDepartment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门Mapper
 */
@Mapper
public interface HrDepartmentMapper extends BaseMapper<HrDepartment> {
}