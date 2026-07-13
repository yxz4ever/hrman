package com.company.hrm.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.company.hrm.system.entity.SysDictItem;

import java.util.List;

/**
 * 字典数据 Service 接口
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    /**
     * 根据字典类型 ID 获取字典数据列表
     * @param dictTypeId 字典类型 ID
     * @return 字典数据列表
     */
    List<SysDictItem> getByDictTypeId(Long dictTypeId);

    /**
     * 根据字典类型编码获取字典数据列表
     * @param dictCode 字典类型编码
     * @return 字典数据列表
     */
    List<SysDictItem> getByDictCode(String dictCode);

    /**
     * 获取所有启用的字典数据
     * @return 字典数据列表
     */
    List<SysDictItem> getEnabledDictItems();
}
