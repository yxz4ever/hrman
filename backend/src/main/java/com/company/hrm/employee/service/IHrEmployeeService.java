package com.company.hrm.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.employee.entity.HrEmployee;

import java.util.List;

/**
 * 员工服务接口
 */
public interface IHrEmployeeService extends IService<HrEmployee> {

    /**
     * 分页查询员工列表
     */
    Page<HrEmployee> selectEmployeePage(Page<HrEmployee> page, String name, String empNo, Long deptId);

    /**
     * 根据工号查询员工
     */
    HrEmployee getByEmpNo(String empNo);

    /**
     * 获取所有在职员工
     */
    List<HrEmployee> getActiveEmployees();
}
