package com.company.hrm.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.company.hrm.system.entity.SysDictItem;
import com.company.hrm.system.entity.SysDictType;
import com.company.hrm.system.mapper.SysDictItemMapper;
import com.company.hrm.system.service.ISysDictItemService;
import com.company.hrm.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据 Service 实现
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Override
    public List<SysDictItem> getByDictTypeId(Long dictTypeId) {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getDictTypeId, dictTypeId)
               .eq(SysDictItem::getDeleted, 0)
               .orderByAsc(SysDictItem::getSort);
        return list(wrapper);
    }

    @Override
    public List<SysDictItem> getByDictCode(String dictCode) {
        SysDictType dictType = dictTypeService.getByDictCode(dictCode);
        if (dictType == null) {
            return List.of();
        }
        return getByDictTypeId(dictType.getId());
    }

    @Override
    public List<SysDictItem> getEnabledDictItems() {
        LambdaQueryWrapper<SysDictItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictItem::getStatus, 1)
               .eq(SysDictItem::getDeleted, 0)
               .orderByAsc(SysDictItem::getSort);
        return list(wrapper);
    }
}
