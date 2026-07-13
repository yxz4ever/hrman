package com.company.hrm.employee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.employee.entity.HrPosition;
import com.company.hrm.employee.mapper.HrPositionMapper;
import com.company.hrm.employee.service.IHrPositionService;
import org.springframework.stereotype.Service;

/**
 * 岗位服务实现
 */
@Service
public class HrPositionServiceImpl extends ServiceImpl<HrPositionMapper, HrPosition> implements IHrPositionService {
}
