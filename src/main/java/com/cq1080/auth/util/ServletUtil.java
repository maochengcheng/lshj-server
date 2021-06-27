package com.cq1080.auth.util;

import com.cq1080.rest.API;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void needLogin(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Content-Type","application/json;charset=utf-8");
        servletResponse.getWriter().write(objectMapper.writeValueAsString(API.e(401,"需要登录")));
        servletResponse.flushBuffer();
    }

    public static void missHeader(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Content-Type","application/json;charset=utf-8");
        servletResponse.getWriter().write(objectMapper.writeValueAsString(API.e(401,"缺少HEADER参数")));
        servletResponse.flushBuffer();
    }

    public static void accessDeny(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.addHeader("Content-Type","application/json;charset=utf-8");
        servletResponse.getWriter().write(objectMapper.writeValueAsString(API.e(403,"没有权限")));
        servletResponse.flushBuffer();
    }
}
