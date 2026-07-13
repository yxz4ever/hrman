package com.company.hrm.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.attendance.entity.AttClockRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttClockRecordMapper extends BaseMapper<AttClockRecord> {

    List<AttClockRecord> selectByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("workDate") LocalDate workDate);
}