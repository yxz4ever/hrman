package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.system.entity.SysDictType;
import com.company.hrm.system.mapper.SysDictTypeMapper;
import com.company.hrm.system.service.ISysDictTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典类型 Service 实现
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    @Override
    public List<SysDictType> getAllDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDeleted, 0)
               .orderByAsc(SysDictType::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public SysDictType getByDictCode(String dictCode) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictCode, dictCode)
               .eq(SysDictType::getDeleted, 0);
        return getOne(wrapper);
    }
}
