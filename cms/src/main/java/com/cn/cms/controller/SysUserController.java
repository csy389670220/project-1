package com.cn.cms.controller;

import com.cn.cms.modelVo.SysUserVo;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.SysUserService;
import com.cn.cms.util.CheckUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 *  系统用户信息维护控制类
 */
@Controller
@ResponseBody
@RequestMapping("sysUser")
public class SysUserController extends BaseController {
    private static final Logger logger= LoggerFactory.getLogger(SysUserController.class);

    @Autowired
    SysUserService sysUserService;


    //跳转到sysUser业务index页面
    @RequestMapping(value = "/query")
    @RequiresPermissions("sys_user_query")
    public ModelAndView query(SysUserVo sysUserVo) {
        if (CheckUtil.isEmpty(sysUserVo.getUserStatus())) {
            sysUserVo.setUserStatus(null);//用户状态为空时，设为null。sql查询不作为条件
        }
        if (CheckUtil.isEmpty(sysUserVo.getPageNum())||sysUserVo.getPageNum()==0) {
            sysUserVo.setPageNum(1);//首次查询，默认为1
        }
        ModelAndView modelAndView = new ModelAndView("sysUser/sysUser");
        sysUserVo.setPageSize(PAGESIZE);
        Map<String, Object> result=sysUserService.selectSysUsers(sysUserVo);
        modelAndView.addObject("result",result);
        modelAndView.addObject("sysUser",sysUserVo);
        return modelAndView;
    }


    //新增资源
    @RequestMapping(value = "/add")
    @RequiresPermissions("sys_user_add")
    public Map<String, Object> add(SysUserVo sysUserVo) {
        /**
         * 获取系统登录ID
         */
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        Map<String, Object> result= null;
        try {
            sysUserVo.setSysId(sysId);
            result = sysUserService.addSysUsers(sysUserVo);
        } catch (Exception e) {
            result= ResultMapUtil.build(EmBusinessCode.SYS_USER_ADD_ERROR);
        }
        return result;
    }

    //角色树初始化
    @RequestMapping(value = "/treeInit")
    public ModelAndView treeInit(SysUserVo sysUserVo) {
        ModelAndView modelAndView = new ModelAndView("sysUser/tree");
        Map<String, Object> result=sysUserService.selectAllRole(sysUserVo);
        modelAndView.addObject("result",result);
        return modelAndView;
    }


    //更新用户
    @RequestMapping(value = "/update")
    @RequiresPermissions("sys_user_update")
    public Map<String, Object> updateUser(SysUserVo sysUserVo) {
        /**
         * 获取系统登录ID
         */
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        Map<String, Object> result= null;
        try {
            sysUserVo.setSysId(sysId);
            result = sysUserService.updateUser(sysUserVo);
        } catch (Exception e) {
            result=ResultMapUtil.build(EmBusinessCode.SYS_USER_UPDATE_ERROR);
        }
        return result;
    }

    //删除用户
    @RequestMapping(value = "/del")
    @RequiresPermissions("sys_user_del")
    public Map<String, Object> del(SysUserVo sysUserVo) {
        /**
         * 获取系统登录ID
         */
        Integer sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        Map<String, Object> result;
        try {
            sysUserVo.setSysId(sysId);
            result = sysUserService.delUser(sysUserVo);
        } catch (Exception e) {
            result=ResultMapUtil.build(EmBusinessCode.SYS_USER_DEL_ERROR);
        }
        return result;
    }


}
