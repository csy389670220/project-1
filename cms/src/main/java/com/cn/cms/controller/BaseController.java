package com.cn.cms.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.Serializable;

/**
 * @author: Farben
 * @description: 系统基础控制类
 * @create: 2019/8/21-10:13
 **/
@Controller
public class BaseController implements Serializable {
    public static  final int PAGESIZE=5;//分页查询，页面条目数量
    public static Integer sysId;//登录用户的系统ID




}
