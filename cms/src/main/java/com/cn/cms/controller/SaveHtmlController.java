package com.cn.cms.controller;

import com.cn.cms.modelVo.SysUserVo;
import com.cn.cms.service.SysUserService;
import com.cn.cms.util.CheckUtil;
import com.cn.cms.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author: Farben
 * @description: SaveHtmlController
 * @create: 2019/9/23-16:15
 **/
@Controller
@RequestMapping("saveHtml")
public class SaveHtmlController {
    private static final Logger logger = LoggerFactory.getLogger(SaveHtmlController.class);
    private static String IDEALVIEW = null;


    @Autowired
    SysUserService sysUserService;


    //跳转到saveHtml业务index页面
    @RequestMapping(value = "/query")
    public ModelAndView query(SysUserVo sysUserVo) {
        ModelAndView modelAndView = new ModelAndView("saveHtml/saveHtml");
        return modelAndView;
    }


    /**
     * 生成index1页面
     *
     * @return
     */
    @RequestMapping(value = "/save/index1")
    public ModelAndView index1() {
        SysUserVo sysUserVo = new SysUserVo();
        if (CheckUtil.isEmpty(sysUserVo.getUserStatus())) {
            sysUserVo.setUserStatus(null);//用户状态为空时，设为null。sql查询不作为条件
        }
        if (CheckUtil.isEmpty(sysUserVo.getPageNum()) || sysUserVo.getPageNum() == 0) {
            sysUserVo.setPageNum(1);//首次查询，默认为1
        }
        ModelAndView modelAndView = new ModelAndView("saveHtml/index1");
        sysUserVo.setPageSize(10);
        Map<String, Object> result = sysUserService.selectSysUsers(sysUserVo);
        modelAndView.addObject("result", result);
        modelAndView.addObject("sysUser", sysUserVo);
        return modelAndView;
    }

    /**
     * 生成index1页面
     *
     * @return
     */
    @RequestMapping(value = "/save/getView")
    @ResponseBody
    public String getView() {
        String view = FileUtil.ReadFileToString("C:\\Users\\Farben\\Desktop\\001.html");
        IDEALVIEW = view;
        return view;

    }

}
