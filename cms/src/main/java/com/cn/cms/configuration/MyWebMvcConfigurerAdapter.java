package com.cn.cms.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;

/**
 * @author: Farben
 * @description: MyWebMvcConfigurerAdapter-SpringMVC的配置文件
 * @create: 2019/8/29-13:52
 **/
@Configuration
public class MyWebMvcConfigurerAdapter implements WebMvcConfigurer {

    @Bean
    public ResourceUrlProvider resourceUrlProvider(){
        ResourceUrlProvider resourceUrlProvider = new ResourceUrlProvider();
        return resourceUrlProvider;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new ResourceUrlProviderExposingInterceptor(resourceUrlProvider())).addPathPatterns("/**");
    }

    /**
     * 设置自己的path匹配规则
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 常用的两种
        // 匹配结尾 / :会识别 url 的最后一个字符是否为 /
        // 设置为true: localhost:8080/test 与 localhost:8080/test/ 等价
        configurer.setUseTrailingSlashMatch(true);
        // 匹配后缀名：会识别 xx.* 后缀的内容
        // 设置为true: localhost:8080/test 与 localhost:8080/test.jsp 等价
        configurer.setUseSuffixPatternMatch(true);
    }


    /**
     * 静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }


    /**
     * 将返回的页面响应，写入指定路径下的文件
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegist1() {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(new IdealSaveHtmlFilter());
        frBean.addUrlPatterns("/saveHtml/save/*");
        frBean.setOrder(1);
        System.out.println("IdealSaveHtmlFilter");
        return frBean;
    }

    /**
     * 指定请求响应进行压缩
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegist2() {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(new GzipFilter());
        frBean.addUrlPatterns("/seckill/*");
        frBean.addUrlPatterns("*.js");
        frBean.setOrder(1);
        System.out.println("GzipFilter");
        return frBean;
    }
}
