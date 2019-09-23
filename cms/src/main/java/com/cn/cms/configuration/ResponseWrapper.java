package com.cn.cms.configuration;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
 
/**
 * @author: Farben
 * @description: ResponseWrapper: 使用HttpServletResponseWrapper包装HttpServletResponse
 * @create: 2019/9/23-15:58
 **/
public class ResponseWrapper extends HttpServletResponseWrapper {
    private PrintWriter cachedWriter;
    private CharArrayWriter bufferedWriter;
 
    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        bufferedWriter = new CharArrayWriter();
        cachedWriter = new PrintWriter(bufferedWriter);
    }
 
    public PrintWriter getWriter() throws IOException {
        return cachedWriter;
    }
 
    public String getResult() {
        byte[] bytes = bufferedWriter.toString().getBytes();
        try {
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            System.out.println("error"+e);
            return "";
        }
    }
 
}