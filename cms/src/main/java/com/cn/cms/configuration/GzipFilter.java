package com.cn.cms.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * @author: Farben
 * @description: GzipFilter-页面请求压缩过滤器
 * @create: 2019/9/24-9:58
 **/
public class GzipFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(GzipFilter.class);

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String headEncoding = ((HttpServletRequest) request).getHeader("accept-encoding");
        if (headEncoding == null || (headEncoding.indexOf("gzip") == -1)) {
            //客户端不支持gzip
            chain.doFilter(request, response);
            logger.warn("----------------该浏览器不支持gzip格式编码-----------------");
        } else {
            // 支持 gzip 压缩，对数据进行gzip压缩
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            GzipResponseWrapper mResp = new GzipResponseWrapper(resp); // 包装响应对象 resp 并缓存响应数据

            chain.doFilter(req, mResp);

            byte[] bytes = mResp.getBytes(); // 获取缓存的响应数据
            //logger.debug("压缩前大小：" + bytes.length);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(bout); // 创建 GZIPOutputStream 对象

            gzipOut.write(bytes); // 将响应的数据写到 Gzip 压缩流中
            gzipOut.close(); // 将数据刷新到  bout 字节流数组

            byte[] bts = bout.toByteArray();
            //logger.debug("压缩后大小：" + bts.length);

            resp.setHeader("Content-Encoding", "gzip"); // 设置响应头信息
            resp.setContentLength(-1);//解决页面响应慢或者不响应
            resp.getOutputStream().write(bts); // 将压缩数据响应给客户端
        }


    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        //logger.debug("+++启动压缩。");
    }

}
