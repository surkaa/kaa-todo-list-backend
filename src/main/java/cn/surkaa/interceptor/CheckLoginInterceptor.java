package cn.surkaa.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

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
    ) {
        log.debug("开始检测是否登录");
        String token = request.getHeader("token");
        if (!StrUtil.isBlank(token)) {
            log.debug("已经登录!");
            return true;
        }
        log.debug("未登录请求");
        String uri = request.getRequestURI();
        // 未登录 但是正打算登录的直接放行
        if ("/users/login".equals(uri)) {
            log.debug("因此请求正是登录请求 放行");
            return true;
        }
        // 未登录 但是正打算注册的直接放行
        if ("/users/register".equals(uri)) {
            log.debug("因此请求正是注册请求 放行");
            return true;
        }
        throw new UserCenterException(ErrorEnum.NOT_LOGIN_ERROR);
    }
}
