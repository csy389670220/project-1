package com.cn.cms.service.impl;

import com.cn.cms.mapper.PermissionMapper;
import com.cn.cms.mapper.RoleMapper;
import com.cn.cms.mapper.RolePerMapper;
import com.cn.cms.model.Permission;
import com.cn.cms.model.Role;
import com.cn.cms.model.RolePer;
import com.cn.cms.modelVo.PermissionVo;
import com.cn.cms.modelVo.RoleVo;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.UserPermissionService;
import com.cn.cms.util.BeanConversionUtil;
import com.cn.cms.util.CheckUtil;
import com.cn.cms.validator.ValidationResult;
import com.cn.cms.validator.ValidatorImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {
    private static final Logger logger = LoggerFactory.getLogger(UserPermissionService.class);

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    RolePerMapper rolePerMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public Map<String, Object> selectRole(RoleVo roleVo) {
        Map<String, Object> resultMap;
        try {
            PageHelper.startPage(roleVo.getPageNum(), roleVo.getPageSize());
            List<RoleVo> listRole=roleMapper.selectRole(roleVo);
            PageInfo<RoleVo> pageInfo = new PageInfo<RoleVo>(listRole);
            pageInfo.setPageSize(roleVo.getPageSize());
            pageInfo.setPageNum(roleVo.getPageNum());//当前页码
            int pages;
            int total= roleMapper.getTotal(roleVo);//总数

            if(total==0){
                pages=1;//查询结果为0时，默认总页数为1。返回0，前端分页插件出错
            }else {
                pages=(total+roleVo.getPageSize()-1)/roleVo.getPageSize();//总页数
            }

            pageInfo.setPages(pages);
            resultMap = ResultMapUtil.successData(pageInfo);
        }catch (Exception e){
            logger.error("selectRole系统错误"+e);
            resultMap = ResultMapUtil.build(EmBusinessCode.ROLE_PERMISSION_TREEINIT_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> selectAllPer(RoleVo roleVo) {
        Map<String, Object> resultMap;
        try {
            PermissionVo permissionVo=new PermissionVo();
            List<RolePer> rolePermissions = null;
            //角色ID不为空，需要查询当前角色名下所有资源，进行勾选操作
            if(!CheckUtil.isEmpty(roleVo.getId())){
                //当前角色拥有的资源集合
                 rolePermissions= rolePerMapper.selectByRoleId(roleVo.getId());
            }
            //1.查询1级资源集合
            permissionVo.setPerLev(1);//1级资源
            List<Permission> listParent=permissionMapper.selectAllPer(BeanConversionUtil.toPermission(permissionVo));
            List<PermissionVo> listParentVo=BeanConversionUtil.toPermissionVos(listParent);
            //1级资源勾选判断
            if(!CheckUtil.isEmpty(rolePermissions)){
                checkRolePermission(listParentVo,rolePermissions);
            }
            //2.添加2级资源
            for (PermissionVo psv : listParentVo) {
                permissionVo.setPerLev(2);//2级资源.
                permissionVo.setParentId(psv.getId());
                List<Permission> listChild=permissionMapper.selectAllPer(BeanConversionUtil.toPermission(permissionVo));
                List<PermissionVo> listChildVo=BeanConversionUtil.toPermissionVos(listChild);
                //2级资源勾选判断
                if(!CheckUtil.isEmpty(rolePermissions)){
                    checkRolePermission(listChildVo,rolePermissions);
                }
                psv.setChildPer(listChildVo);
            }
            resultMap = ResultMapUtil.successData(listParentVo);
        }catch (Exception e){
            logger.error("selectAllPer系统错误"+e);
            resultMap = ResultMapUtil.build(EmBusinessCode.ROLE_PERMISSION_TREEINIT_ERROR);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addRole(RoleVo roleVo) throws Exception {
        Date now=new Date();
        //1.新增角色
        roleVo.setRoleStatus("1");
        roleVo.setCreateTime(now);
        roleVo.setUpdateTime(now);
        roleVo.setCreateUser(roleVo.getSysId());
        roleVo.setUpdateUser(roleVo.getSysId());

        //验证数据格式合法性
        ValidationResult validationResult = validator.validate(roleVo);
        if (validationResult.isHasErrors()) {
            EmBusinessCode.PARAMETER_VALIDATION_ERROR.setErrMsg(validationResult.getErrMsg());
            return ResultMapUtil.build(EmBusinessCode.PARAMETER_VALIDATION_ERROR);
        }
        Role role=BeanConversionUtil.toRole(roleVo);
        roleMapper.insertSelective(role);
        //2.添加角色-资源表
        if(!CheckUtil.isEmpty(roleVo.getPermissions())){//角色有资源
            RolePer p=new RolePer();
            p.setRoleId(role.getId());
            p.setCreateTime(now);
            for(int id:roleVo.getPermissions()){
                p.setPerId(id);
                rolePerMapper.insertSelective(p);
            }
        }

        return ResultMapUtil.successData("新增角色成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> delRole(RoleVo roleVo) throws Exception {
        Date now=new Date();
        roleVo.setRoleStatus("0");
        roleVo.setUpdateUser(roleVo.getSysId());
        roleVo.setUpdateTime(now);
        Role role=BeanConversionUtil.toRole(roleVo);
        //1.删除角色表
        roleMapper.updateByPrimaryKeySelective(role);
        //2.删除角色-权限表
        rolePerMapper.deleteByRoleID(roleVo.getId());
        return ResultMapUtil.successData("删除角色成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateRole(RoleVo roleVo) throws Exception {
        Date now=new Date();
        //1.更新角色
        roleVo.setUpdateTime(now);
        roleVo.setUpdateUser(roleVo.getSysId());
        Role role=BeanConversionUtil.toRole(roleVo);
        roleMapper.updateByPrimaryKeySelective(role);
        //2.删除角色-资源表
        rolePerMapper.deleteByRoleID(roleVo.getId());
        //3.增加角色-资源表
        if(!CheckUtil.isEmpty(roleVo.getPermissions())){
            RolePer p=new RolePer();
            p.setRoleId(roleVo.getId());
            p.setCreateTime(now);
            for(int id:roleVo.getPermissions()){
                p.setPerId(id);
                rolePerMapper.insertSelective(p);
            }
        }
        return ResultMapUtil.successData("更新角色成功");
    }


    /**
     * 根据角色的资源集合，在系统总资源集合中标记出当前角色的资源，用于角色资源树的回显。
     * @param permissionVos 当前系统全部资源集合
     * @param rolePers  指定角色的资源集合
     */
    public  static void checkRolePermission(List<PermissionVo> permissionVos, List<RolePer> rolePers){
        for(PermissionVo per:permissionVos){
            for(RolePer rolePer:rolePers){
                if(per.getId()==rolePer.getPerId()){
                    per.setCheck(1);//设置选中
                }
            }
        }
    }
}
