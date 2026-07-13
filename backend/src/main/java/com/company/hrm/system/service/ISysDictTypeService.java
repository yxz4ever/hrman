package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysDictType;

import java.util.List;

/**
 * 字典类型 Service 接口
 */
public interface ISysDictTypeService extends IService<SysDictType> {

    /**
     * 获取所有字典类型
     */
    List<SysDictType> getAllDictTypes();

    /**
     * 根据字典编码获取字典类型
     */
    SysDictType getByDictCode(String dictCode);
}
