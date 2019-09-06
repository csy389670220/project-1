package com.cn.cms.mapper;

import com.cn.cms.model.Permission;
import com.cn.cms.modelVo.PermissionVo;

import java.util.List;

public interface PermissionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);

    List<PermissionVo> selectAllPer(PermissionVo record);
}