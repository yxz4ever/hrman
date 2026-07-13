package com.company.hrm.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_rule_group")
public class AttRuleGroup extends BaseEntity {
    
    @TableId
    private Long id;
    
    private String groupCode;
    
    private String groupName;

    /**
     * 优先级
     */
    private Integer priority;

    private String applicableScope;
    
    private Integer status;
    
    private LocalDate effectiveStartDate;
    
    private LocalDate effectiveEndDate;
    
    private String description;
}