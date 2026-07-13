package com.company.hrm.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.hrm.attendance.entity.AttRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤规则Mapper
 */
@Mapper
public interface AttRuleMapper extends BaseMapper<AttRule> {
}
