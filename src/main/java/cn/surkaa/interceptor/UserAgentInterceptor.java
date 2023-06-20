package cn.surkaa.interceptor;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用于拦截所有请求并获取请求者的ip 所用浏览器和使用的操作系统
 *
 * @author SurKaa
 */
@Slf4j
public class UserAgentInterceptor implements HandlerInterceptor {

    // 客户端使用代理时的请求头 用来反代理客户端真实ip
    private static final String[] HEADER_NAMES = new String[]{
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("new request");
        String ip = unProxyIp(request);
        if ("!".equals(ip)) {
            return false;
        }
        UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        log.debug("from os={}, browser={}, ip={}, url={}, method={}",
                userAgent.getOs(),
                userAgent.getBrowser(),
                ip,
                request.getRequestURI(),
                request.getMethod()
        );
        return true;
    }

    /**
     * 反向代理获取客户端的真实ip
     *
     * @param request 请求
     * @return header或者ip
     */
    private static String unProxyIp(HttpServletRequest request) {
        String ip;
        for (String name : HEADER_NAMES) {
            ip = request.getHeader(name);
            // 检查是否使用了代理ip (未使用则获取到的一直是未知的: null "" unknown)
            if (!Strings.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                log.debug("收到代理ip请求 将直接拒绝访问: header={{}: {}}", name, ip);
                return "!";
            }
        }
        return request.getRemoteAddr();
    }
}
