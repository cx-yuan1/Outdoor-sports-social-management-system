package com.outdoor.interceptor;

import com.outdoor.common.Constants;
import com.outdoor.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        // 判断是否是管理后台请求
        if (uri.startsWith("/admin") || uri.startsWith("/api/admin")) {
            if (user == null) {
                // 未登录，重定向到统一登录页
                if (uri.startsWith("/api/")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
                } else {
                    response.sendRedirect("/login");
                }
                return false;
            }
            // 检查是否是管理员
            if (user.getRole() != Constants.Role.ADMIN) {
                if (uri.startsWith("/api/")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                } else {
                    response.sendRedirect("/login");
                }
                return false;
            }
        } else if (uri.startsWith("/organizer") || uri.startsWith("/api/organizer")) {
            // 活动发起者端
            if (user == null) {
                if (uri.startsWith("/api/")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
                } else {
                    response.sendRedirect("/login");
                }
                return false;
            }
            // 检查是否是活动发起者或管理员
            if (user.getRole() != Constants.Role.ORGANIZER && user.getRole() != Constants.Role.ADMIN) {
                if (uri.startsWith("/api/")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"无权限访问\"}");
                } else {
                    response.sendRedirect("/");
                }
                return false;
            }
        } else if (uri.startsWith("/user") || uri.startsWith("/api/user")) {
            // 用户端需要登录的接口
            if (user == null) {
                if (uri.startsWith("/api/")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
                } else {
                    response.sendRedirect("/login");
                }
                return false;
            }
        }

        return true;
    }
}
