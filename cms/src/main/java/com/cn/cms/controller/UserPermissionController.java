package com.cn.cms.controller;

import com.cn.cms.modelVo.RoleVo;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.UserPermissionService;
import com.cn.cms.util.CheckUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 *  系统用户权限管理控制类
 */
@Controller
@ResponseBody
@RequestMapping("userPer")
public class UserPermissionController extends BaseController {
    private static final Logger logger= LoggerFactory.getLogger(UserPermissionController.class);

    @Autowired
    UserPermissionService userPermissionService;

    //跳转到UserPermission业务页面
    @RequestMapping(value = "/query")
    @RequiresPermissions("role_per_query")
    public ModelAndView query(RoleVo roleVo) {
        ModelAndView modelAndView = new ModelAndView("userPer/userPermission");
        if (CheckUtil.isEmpty(roleVo.getPageNum())||roleVo.getPageNum()==0) {
            roleVo.setPageNum(1);//首次查询，默认为1
        }
        roleVo.setPageSize(PAGESIZE);
        Map<String, Object> result=userPermissionService.selectRole(roleVo);
        modelAndView.addObject("result",result);
        modelAndView.addObject("role",roleVo);
        return modelAndView;
    }



    @RequestMapping(value = "/treeInit")
    public ModelAndView treeInit(RoleVo roleVo) {
        ModelAndView modelAndView = new ModelAndView("userPer/tree");
        Map<String, Object> perList=userPermissionService.selectAllPer(roleVo);
        modelAndView.addObject("result",perList);
        return modelAndView;
    }

    @RequestMapping(value = "/add")
    @RequiresPermissions("role_per_add")
    public Map<String, Object> addRole(RoleVo roleVo) {
        Map<String, Object> result;
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        roleVo.setSysId(sysId);
        try {
             result=userPermissionService.addRole(roleVo);
        } catch (Exception e) {
            logger.error("addRole系统错误:{}",e);
            result= ResultMapUtil.build(EmBusinessCode.ROLE_PERMISSION_ADD_ERROR);
    }
        return result;
    }

    @RequestMapping(value = "/del")
    @RequiresPermissions("role_per_del")
    public Map<String, Object> delRole(RoleVo roleVo) {
        Map<String, Object> result;
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        roleVo.setSysId(sysId);
        try {
            result=userPermissionService.delRole(roleVo);
        } catch (Exception e) {
            logger.error("delRole系统错误");
            result= ResultMapUtil.build(EmBusinessCode.ROLE_PERMISSION_DEL_ERROR);
        }
        return result;
    }

    @RequestMapping(value = "/update")
    @RequiresPermissions("role_per_update")
    public Map<String, Object> updateRole(RoleVo roleVo) {
        Map<String, Object> result;
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        roleVo.setSysId(sysId);
        try {
            result=userPermissionService.updateRole(roleVo);
        } catch (Exception e) {
            logger.error("updateRole系统错误");
            result= ResultMapUtil.build(EmBusinessCode.ROLE_PERMISSION_UPDATE_ERROR);
        }
        return result;
    }




}
