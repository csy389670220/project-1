package com.cn.cms.configuration;

import com.cn.cms.util.FileUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
/**
 * @author: Farben
 * @description: IdealSaveHtmlFilter:IDEAL镶嵌日志监控系统静态页面的生成拦截器
 * @create: 2019/9/23-16:02
 **/
public class IdealSaveHtmlFilter implements Filter {
 
    public void destroy() {
 
    }
 
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        ResponseWrapper responseWrapper=null;
        if(resp instanceof  HttpServletResponse){
            responseWrapper= new ResponseWrapper((HttpServletResponse)resp);

        }

        if(responseWrapper==null){
             
            chain.doFilter(req, resp);
             
        } else {
             

            chain.doFilter(req, responseWrapper);
             
            PrintWriter out = resp.getWriter(); 
            try {
                //取返回的json串
                String result = responseWrapper.getResult();
                // 写入文件
                FileUtil.Write2FileByBuffered("C:\\Users\\Farben\\Desktop\\001.html",result);
                out.write("OK");
            } catch (Exception e) {
                System.out.println("error  doFilter:"+e);
            } finally {
                out.flush(); 
                out.close();
            }
        }
    }
 
    @Override
    public void init(FilterConfig filterconfig) throws ServletException {
         
    }

 
}