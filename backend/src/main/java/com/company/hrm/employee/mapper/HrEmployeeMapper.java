package com.company.hrm.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.employee.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工Mapper
 */
@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {

    /**
     * 分页查询员工列表
     */
    IPage<HrEmployee> selectEmployeePage(Page<HrEmployee> page, @Param("name") String name,
                                          @Param("empNo") String empNo, @Param("deptId") Long deptId);

    /**
     * 查询在职员工列表
     */
    List<HrEmployee> selectActiveEmployees();
}
