package com.company.hrm.attendance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.attendance.entity.AttClockRecord;
import com.company.hrm.attendance.mapper.AttClockRecordMapper;
import com.company.hrm.attendance.service.IAttClockRecordService;
import org.springframework.stereotype.Service;

/**
 * 打卡记录服务实现
 */
@Service
public class AttClockRecordServiceImpl extends ServiceImpl<AttClockRecordMapper, AttClockRecord> implements IAttClockRecordService {
}
