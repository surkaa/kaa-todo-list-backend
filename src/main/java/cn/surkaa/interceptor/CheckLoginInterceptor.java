package cn.surkaa.interceptor;

import cn.surkaa.module.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Objects;

import static cn.surkaa.contant.UserContant.LOGIN_STATE;

/**
 * 用于检查请求是否登录的拦截器
 *
 * @author SurKaa
 */
@Slf4j
public class CheckLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        log.debug("开始检测是否登录");
        try {
            Object o = request.getSession().getAttribute(LOGIN_STATE);
            User user = (User) Objects.requireNonNull(o);
            Objects.requireNonNull(user);
        } catch (Exception e) {
            log.debug("未登录请求");
            // 未登录 但是正打算登录的直接放行
            if ("/users/login".equals(request.getRequestURI())) {
                log.debug("因此请求正是登录请求 放行");
                return true;
            }
            String direct = request.getContextPath() + "/users/login";
            log.info("将即将跳转到登陆页面: {}", direct);
            response.sendRedirect(direct);
            return false;
        }
        return true;
    }
}
