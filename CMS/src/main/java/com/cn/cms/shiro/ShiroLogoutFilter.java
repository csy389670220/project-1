package com.cn.cms.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author: Farben
 * @description: ShiroLogoutFilter: 自定义 LogoutFilter
 * @create: 2019/9/5-9:52
 **/
public class ShiroLogoutFilter extends LogoutFilter {
    private  KickoutSessionControlFilter kickoutSessionControlFilter;



    /**
     * 自定义登出,登出之后,清理当前用户redis部分缓存信息
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        //登出操作 清除缓存  subject.logout() 可以自动清理缓存信息, 这些代码是可以省略的  这里只是做个笔记 表示这种方式也可以清除
        Subject subject = getSubject(request,response);
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        ShiroRealm shiroRealm = (ShiroRealm) securityManager.getRealms().iterator().next();
        PrincipalCollection principals = subject.getPrincipals();
        shiroRealm.clearCache(principals);
        String userName= (String) subject.getPrincipal();
        //登出
        subject.logout();

        //清楚控制并发控制缓存缓存-shiro:cache:kickout:用户名
        kickoutSessionControlFilter.delCache(userName);

        //获取登出后重定向到的地址
        String redirectUrl = getRedirectUrl(request,response,subject);
        //重定向
        issueRedirect(request,response,redirectUrl);
        return false;
    }

    private String getRedisKickoutKey(String username) {
        return "shiro:cache:kickout:" + username;
    }


    public KickoutSessionControlFilter getKickoutSessionControlFilter() {
        return kickoutSessionControlFilter;
    }

    public void setKickoutSessionControlFilter(KickoutSessionControlFilter kickoutSessionControlFilter) {
        this.kickoutSessionControlFilter = kickoutSessionControlFilter;
    }
}
