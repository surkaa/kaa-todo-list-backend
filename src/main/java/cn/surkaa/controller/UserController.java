package cn.surkaa.controller;

import cn.surkaa.module.User;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SurKaa
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 注册逻辑 表现层
     *
     * @param registerRequest 注册请求体
     * @return 注册成功id 否则为null
     */
    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
            // 请求体为空
            return null;
        }
        return userService.userRegister(
                registerRequest.getAccount(),
                registerRequest.getPassword(),
                registerRequest.getCheckPassword()
        );
    }

    /**
     * 登录逻辑
     *
     * @param loginRequest 登录请求体
     * @param request      请求
     * @return 登录成功获取到的User
     */
    @PostMapping("/login")
    public User login(
            @RequestBody UserLoginRequest loginRequest,
            HttpServletRequest request
    ) {
        if (loginRequest == null) {
            // 请求体为空
            return null;
        }
        return userService.doLogin(
                loginRequest.getAccount(),
                loginRequest.getPassword(),
                request
        );
    }
}
