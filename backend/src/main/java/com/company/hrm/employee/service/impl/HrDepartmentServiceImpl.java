package com.company.hrm.employee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.employee.entity.HrDepartment;
import com.company.hrm.employee.mapper.HrDepartmentMapper;
import com.company.hrm.employee.service.IHrDepartmentService;
import org.springframework.stereotype.Service;

/**
 * 部门服务实现
 */
@Service
public class HrDepartmentServiceImpl extends ServiceImpl<HrDepartmentMapper, HrDepartment> implements IHrDepartmentService {
}
