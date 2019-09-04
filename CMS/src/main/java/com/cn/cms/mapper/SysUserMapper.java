package com.cn.cms.mapper;


import com.cn.cms.model.Permission;
import com.cn.cms.model.SysUser;

import java.util.List;
public interface SysUserMapper {
    /**
     * 删除用户
     *
     * @param id 主键
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入用户数据
     *
     * @param record
     * @return
     */
    int insert(SysUser record);

    /**
     * 插入用户数据
     *
     * @param record
     * @return
     */
    int insertSelective(SysUser record);

    /**
     * 根据主键查询用户
     *
     * @param id 主键
     * @return
     */
    SysUser selectByPrimaryKey(Integer id);

    /**
     * 更新用户数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SysUser record);

    /**
     * 更新用户数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(SysUser record);

    /**
     * 根据登录名查询数据
     *
     * @param loginName
     * @return
     */
    SysUser selectByLoginName(String loginName);

    /**
     * 根据查询条件查询列表
     *
     * @param record
     * @return
     */
    List<SysUser> selectSysUsers(SysUser record);

    /**
     * 查询数据总数
     *
     * @param record
     * @return
     */
    int getTotal(SysUser record);

    /**
     * 根据登录名，查询用户所有权限
     *
     * @param loginName
     * @return
     */
    List<Permission> selectAllPermission(String loginName);
}