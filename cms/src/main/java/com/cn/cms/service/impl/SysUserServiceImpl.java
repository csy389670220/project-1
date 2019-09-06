package com.cn.cms.service.impl;

import com.cn.cms.mapper.RoleMapper;
import com.cn.cms.mapper.SysUserMapper;
import com.cn.cms.mapper.UserRoleMapper;
import com.cn.cms.model.SysUser;
import com.cn.cms.model.UserRole;
import com.cn.cms.modelVo.RoleVo;
import com.cn.cms.modelVo.SysUserVo;
import com.cn.cms.result.Constant;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.SysUserService;
import com.cn.cms.util.BeanConversionUtil;
import com.cn.cms.util.CheckUtil;
import com.cn.cms.validator.ValidationResult;
import com.cn.cms.validator.ValidatorImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {
    private static final Logger logger = LoggerFactory.getLogger(SysUserService.class);

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public Map<String, Object> selectByLoginName(String loginName) {
        Map<String, Object> resultMap;
        try {
            SysUser sysUser = sysUserMapper.selectByLoginName(loginName);
            resultMap = ResultMapUtil.successData(sysUser);
        } catch (Exception e) {
            logger.error("selectByLoginName系统错误", e);
            resultMap = ResultMapUtil.failData("查询失败");
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> selectSysUsers(SysUserVo sysUserVo) {
        Map<String, Object> resultMap;
        try {
            PageHelper.startPage(sysUserVo.getPageNum(), sysUserVo.getPageSize());
            SysUser sysUser=BeanConversionUtil.toSysUser(sysUserVo);
            List<SysUser> sysUsers = sysUserMapper.selectSysUsers(sysUser);
            PageInfo<SysUser> pageInfo = new PageInfo<SysUser>(sysUsers);
            pageInfo.setPageNum(sysUserVo.getPageNum());//当前页码
            pageInfo.setPageSize(sysUserVo.getPageSize());

            int total = sysUserMapper.getTotal(sysUser);//总数
            int pages;
            if (total == 0) {
                pages = 1;//查询结果为0时，默认总页数为1。返回0，前端分页插件出错
            } else {
                pages = (total + sysUserVo.getPageSize() - 1) / sysUserVo.getPageSize();//总页数
            }

            pageInfo.setPages(pages);
            resultMap = ResultMapUtil.successData(pageInfo);
        } catch (Exception e) {
            logger.error("selectSysUsers系统错误", e);
            resultMap = ResultMapUtil.failData(EmBusinessCode.SYS_USER_ADD_QUERY);
        }

        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addSysUsers(SysUserVo sysUserVo) throws Exception {
        Date now = new Date();
        //1.判断登录名是否重复
        if (!CheckUtil.isEmpty(sysUserMapper.selectByLoginName(sysUserVo.getLoginName()))) {//账户名重复
            logger.info("addSysUsers账户名重复{}", sysUserVo.getLoginName());
            return ResultMapUtil.build(EmBusinessCode.SYS_USER_EXIST_ERROR);
        }
        //2.用户新增
        sysUserVo.setCreateTime(now);
        sysUserVo.setCreateUser(sysUserVo.getSysId());//创建者
        sysUserVo.setUpdateTime(now);
        sysUserVo.setUpdateUser(sysUserVo.getSysId());//更新者
        sysUserVo.setUserStatus("1");//新增用户状态默认为1
        //3.验证数据格式合法性
        ValidationResult validationResult = validator.validate(sysUserVo);
        if (validationResult.isHasErrors()) {
            EmBusinessCode.PARAMETER_VALIDATION_ERROR.setErrMsg(validationResult.getErrMsg());
            return ResultMapUtil.build( EmBusinessCode.PARAMETER_VALIDATION_ERROR);
        }
        sysUserVo.setPassword(
                new SimpleHash(
                        "md5",
                        sysUserVo.getPassword(),
                        ByteSource.Util.bytes(Constant.SYS_SALT), 2).toHex());//密码加密存储
        SysUser sysUser=BeanConversionUtil.toSysUser(sysUserVo);
        sysUserMapper.insertSelective(sysUser);

        //4.用户名下有角色资源，新增用户-角色表新增
        if (!CheckUtil.isEmpty(sysUserVo.getRoles())) {
            UserRole p = new UserRole();
            p.setUserId(sysUserVo.getId());
            p.setCreateTime(now);
            for (int id : sysUserVo.getRoles()) {
                p.setRoleId(id);
                userRoleMapper.insertSelective(p);
            }
        }
        return ResultMapUtil.successData("用户信息新增成功");
    }

    @Override
    public Map<String, Object> selectAllRole(SysUserVo sysUserVo) {
        Map<String, Object> resultMap;
        RoleVo roleVo = new RoleVo();
        try {
            roleVo.setRoleStatus("1");
            List<RoleVo> roleList = roleMapper.selectRole(roleVo);
            //用户ID不为空，需要在角色集合中，将当前用户拥有的角色标记
            if (!CheckUtil.isEmpty(sysUserVo.getId())) {
                //当前用户拥有的角色集合
                List<UserRole> userRoles = userRoleMapper.selectByUserId(sysUserVo.getId());
                for (RoleVo ro : roleList) {
                    for (UserRole userRole : userRoles) {
                        if (ro.getId() == userRole.getRoleId()) {
                            ro.setCheck(1);
                        }
                    }
                }
            }
            resultMap = ResultMapUtil.successData(roleList);
        } catch (Exception e) {
            logger.error("selectAllRole系统错误" + e);
            resultMap = ResultMapUtil.failData("查询失败");
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateUser(SysUserVo sysUserVo) throws Exception {
        Date now = new Date();
        //1.更新用户表
        sysUserVo.setUpdateTime(now);
        sysUserVo.setUpdateUser(sysUserVo.getSysId());
        SysUser sysUser=BeanConversionUtil.toSysUser(sysUserVo);
        sysUserMapper.updateByPrimaryKeySelective(sysUser);
        //2.用户角色资源不为空，更新用户-角色表
        if (!CheckUtil.isEmpty(sysUserVo.getRoles())) {
            //2.1删除用户资源表
            userRoleMapper.deleteByUserId(sysUserVo.getId());
            //2.2新增用户资源表
            UserRole p = new UserRole();
            p.setUserId(sysUserVo.getId());
            p.setCreateTime(now);
            for (int id : sysUserVo.getRoles()) {
                p.setRoleId(id);
                userRoleMapper.insertSelective(p);
            }
        }
        return ResultMapUtil.successData("更新用户成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> delUser(SysUserVo sysUserVo) throws Exception {
        Date now = new Date();
        sysUserVo.setUpdateUser(sysUserVo.getSysId());//操作着
        sysUserVo.setUpdateTime(now);//更新时间
        sysUserVo.setUserStatus("0");//删除用户，状态设置0-失效
        SysUser sysUser=BeanConversionUtil.toSysUser(sysUserVo);
        //1. 删除用户信息表
        sysUserMapper.updateByPrimaryKeySelective(sysUser);
        //2.删除用户-角色表
        userRoleMapper.deleteByUserId(sysUserVo.getId());
        return ResultMapUtil.successData("删除用户成功");
    }
}
