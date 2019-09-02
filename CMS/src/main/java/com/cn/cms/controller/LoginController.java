package com.cn.cms.controller;

import com.cn.cms.mapper.SysUserMapper;
import com.cn.cms.model.SysUser;
import com.cn.cms.shiro.RedisSessionDAO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Farben
 * @description: LoginController 系统登录页面
 * @create: 2019/8/30-13:36
 **/
@Controller
public class LoginController extends  BaseController {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);


    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    private RedisSessionDAO redisSessionDAO;

    /**
     * 访问项目根路径
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public ModelAndView root() {
        ModelAndView view = new ModelAndView("login");
        return view;
    }

    /**
     * 跳转到login页面
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView view = new ModelAndView("login");
        return view;
    }

    /**
     * 用户登录
     * @param request
     * @param loginName
     * @param passWord
     * @param captcha
     * @param model
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String loginUser(HttpServletRequest request, String loginName, String passWord, boolean rememberMe, String captcha, Model model) {

        //校验验证码
        //session中的验证码
        String sessionCaptcha = (String) SecurityUtils.getSubject().getSession().getAttribute(CaptchaController.KEY_CAPTCHA);
        if (null == captcha || !captcha.equalsIgnoreCase(sessionCaptcha)) {
            model.addAttribute("msg","验证码错误！");
            return "login";
        }

        //如果有点击  记住我
        UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(loginName,passWord,rememberMe);
        //UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username,password);
        Subject subject = SecurityUtils.getSubject();
        try {
            //登录操作
            subject.login(usernamePasswordToken);
            return "redirect:index";
        } catch(Exception e) {
            //登录失败从request中获取shiro处理的异常信息 shiroLoginFailure:就是shiro异常类的全类名
            String exception = (String) request.getAttribute("shiroLoginFailure");

            if(e instanceof UnknownAccountException){
                model.addAttribute("msg","用户名或密码错误！");
            }

            if(e instanceof IncorrectCredentialsException){
                model.addAttribute("msg","用户名或密码错误！");
            }

            if(e instanceof LockedAccountException){
                model.addAttribute("msg","账号已被锁定,请联系管理员！");
            }
            logger.info("登录系统错误,登录名:{},错误:{}",loginName,e);
            //返回登录页面
            return "login";
        }
    }

    /**
     * 跳转到index页面
     * @return
     */
    @RequestMapping(value = "/index")
    public ModelAndView index() {
        Subject subject = SecurityUtils.getSubject();
        String loginName=(String) subject.getPrincipal();
        if (loginName == null){
            ModelAndView view = new ModelAndView("login");
            return view;
        }else{
            ModelAndView view = new ModelAndView("index");
            SysUser user=sysUserMapper.selectByLoginName(loginName);
            view.addObject("user",user);
            view.addObject("count",redisSessionDAO.getKickoutSessionSize());
            return view;
        }

    }




}
