package com.company.hrm.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.company.hrm.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_clock_record")
public class AttClockRecord extends BaseEntity {
    
    @TableId
    private Long id;
    
    private Long employeeId;
    
    private LocalDateTime workDate;
    
    private LocalDateTime clockTime;
    
    private String clockType;
    
    private String sourceType;
    
    private String deviceNo;
    
    private String location;
    
    private String photoUrl;
    
    private Integer isValid;
    
    private String rawPayload;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}