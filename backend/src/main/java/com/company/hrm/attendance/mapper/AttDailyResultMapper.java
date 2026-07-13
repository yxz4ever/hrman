package com.company.hrm.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.attendance.entity.AttDailyResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日考勤结果Mapper
 */
@Mapper
public interface AttDailyResultMapper extends BaseMapper<AttDailyResult> {

    /**
     * 根据员工ID和日期查询考勤结果
     */
    AttDailyResult selectByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("workDate") LocalDate workDate);

    /**
     * 根据员工ID和日期范围查询考勤结果
     */
    List<AttDailyResult> selectByEmployeeAndDateRange(@Param("employeeId") Long employeeId,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}
