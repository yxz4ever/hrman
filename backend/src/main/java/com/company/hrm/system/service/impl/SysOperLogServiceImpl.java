package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.system.entity.SysOperLog;
import com.company.hrm.system.mapper.SysOperLogMapper;
import com.company.hrm.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志 Service 实现
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Override
    public void saveLog(SysOperLog log) {
        save(log);
    }

    @Override
    public Map<String, Object> getOperLogList(Map<String, Object> params) {
        Integer pageNum = (Integer) params.getOrDefault("pageNum", 1);
        Integer pageSize = (Integer) params.getOrDefault("pageSize", 10);
        String title = (String) params.get("title");
        String operName = (String) params.get("operName");
        Integer businessType = (Integer) params.get("businessType");
        Integer status = (Integer) params.get("status");

        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SysOperLog::getOperTime);

        if (title != null && !title.isEmpty()) {
            wrapper.like(SysOperLog::getTitle, title);
        }
        if (operName != null && !operName.isEmpty()) {
            wrapper.like(SysOperLog::getOperName, operName);
        }
        if (businessType != null) {
            wrapper.eq(SysOperLog::getBusinessType, businessType);
        }
        if (status != null) {
            wrapper.eq(SysOperLog::getStatus, status);
        }

        Page<SysOperLog> page = new Page<>(pageNum, pageSize);
        Page<SysOperLog> resultPage = page(page, wrapper);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", resultPage.getRecords());
        resultMap.put("total", resultPage.getTotal());
        resultMap.put("pageNum", pageNum);
        resultMap.put("pageSize", pageSize);

        return resultMap;
    }
}
