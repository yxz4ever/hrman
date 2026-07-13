package com.company.hrm.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.mapper.HrEmployeeMapper;
import com.company.hrm.employee.service.IHrEmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工服务实现
 */
@Service
public class HrEmployeeServiceImpl extends ServiceImpl<HrEmployeeMapper, HrEmployee> implements IHrEmployeeService {

    @Override
    public Page<HrEmployee> selectEmployeePage(Page<HrEmployee> page, String name, String empNo, Long deptId) {
        IPage<HrEmployee> result = baseMapper.selectEmployeePage(page, name, empNo, deptId);
        // 将IPage转换为Page
        if (result instanceof Page) {
            return (Page<HrEmployee>) result;
        }
        // 如果不是Page实例，创建新的Page对象
        Page<HrEmployee> newPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        newPage.setRecords(result.getRecords());
        return newPage;
    }

    @Override
    public HrEmployee getByEmpNo(String empNo) {
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrEmployee::getEmpNo, empNo);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<HrEmployee> getActiveEmployees() {
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrEmployee::getEmployeeStatus, 1); // 在职状态
        return baseMapper.selectList(wrapper);
    }
}
