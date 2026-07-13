package com.company.hrm.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /**
     * 日志 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 业务类型：0-其它 1-新增 2-修改 3-删除 4-导出 5-导入
     */
    private Integer businessType;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式：GET/POST/PUT/DELETE
     */
    private String requestMethod;

    /**
     * 操作类型：0-其它 1-后台用户 2-手机端用户
     */
    private Integer operatorType;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 请求 URL
     */
    private String operUrl;

    /**
     * 主机地址
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 请求参数
     */
    private String param;

    /**
     * 返回数据
     */
    private String jsonResult;

    /**
     * 操作状态：0-正常 1-异常
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    /**
     * 消耗时间 (毫秒)
     */
    private Long costTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
