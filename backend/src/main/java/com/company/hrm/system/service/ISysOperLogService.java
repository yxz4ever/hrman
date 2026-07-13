package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysOperLog;

import java.util.Map;

/**
 * 操作日志 Service 接口
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 记录操作日志
     */
    void saveLog(SysOperLog log);

    /**
     * 获取操作日志列表（带分页和条件）
     */
    Map<String, Object> getOperLogList(Map<String, Object> params);
}
