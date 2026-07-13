package com.company.hrm.employee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.employee.entity.HrEmployeeDetail;
import com.company.hrm.employee.mapper.HrEmployeeDetailMapper;
import com.company.hrm.employee.service.IHrEmployeeDetailService;
import org.springframework.stereotype.Service;

/**
 * 员工详情服务实现
 */
@Service
public class HrEmployeeDetailServiceImpl extends ServiceImpl<HrEmployeeDetailMapper, HrEmployeeDetail> implements IHrEmployeeDetailService {
}
