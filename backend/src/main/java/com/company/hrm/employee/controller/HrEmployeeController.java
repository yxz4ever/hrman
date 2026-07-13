package com.company.hrm.employee.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.hrm.common.PageResult;
import com.company.hrm.common.Result;
import com.company.hrm.employee.entity.HrEmployee;
import com.company.hrm.employee.service.IHrEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/employees")
public class HrEmployeeController {

    @Autowired
    private IHrEmployeeService hrEmployeeService;

    /**
     * 分页查询员工列表
     */
    @GetMapping
    public Result<PageResult<HrEmployee>> getList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String empNo,
            @RequestParam(required = false) Long deptId) {

        Page<HrEmployee> page = new Page<>(current, size);
        Page<HrEmployee> result = hrEmployeeService.selectEmployeePage(page, name, empNo, deptId);

        PageResult<HrEmployee> pageResult = PageResult.of(
                result.getTotal(),
                result.getRecords(),
                result.getCurrent(),
                result.getSize()
        );

        return Result.success(pageResult);
    }

    /**
     * 根据ID查询员工详情
     */
    @GetMapping("/{id}")
    public Result<HrEmployee> getById(@PathVariable Long id) {
        HrEmployee employee = hrEmployeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 根据工号查询员工
     */
    @GetMapping("/emp-no/{empNo}")
    public Result<HrEmployee> getByEmpNo(@PathVariable String empNo) {
        HrEmployee employee = hrEmployeeService.getByEmpNo(empNo);
        return Result.success(employee);
    }

    /**
     * 新增员工
     */
    @PostMapping
    public Result<String> create(@RequestBody HrEmployee employee) {
        hrEmployeeService.save(employee);
        return Result.success("创建成功");
    }

    /**
     * 更新员工
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody HrEmployee employee) {
        employee.setId(id);
        hrEmployeeService.updateById(employee);
        return Result.success("更新成功");
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        hrEmployeeService.removeById(id);
        return Result.success("删除成功");
    }
}
