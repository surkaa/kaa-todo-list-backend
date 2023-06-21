package cn.surkaa.controller;

import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.module.User;
import cn.surkaa.module.request.ResponseResult;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author SurKaa
 */
@Slf4j
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
     * @return {@link ResponseResult}
     */
    @PostMapping("/register")
    public ResponseResult<?> register(
            @RequestBody UserRegisterRequest registerRequest
    ) {
        log.debug("收到注册请求");
        if (registerRequest == null) {
            log.debug("请求体为空");
            return ResponseResult.error(ErrorEnum.REQUEST_ERROR, "账户密码为空");
        }
        log.debug("注册账号: {}", registerRequest.getAccount());
        return ResponseResult.ofRun(() -> {
            long userId = userService.userRegister(registerRequest);
            log.debug("注册所得用户id={}", userId);
            return userId;
        });
    }

    /**
     * 登录逻辑
     *
     * @param loginRequest 登录请求体
     * @param request      请求
     * @return {@link ResponseResult}
     */
    @PostMapping("/login")
    public ResponseResult<?> login(
            @RequestBody UserLoginRequest loginRequest,
            HttpServletRequest request
    ) {
        log.debug("收到登录请求");
        if (loginRequest == null) {
            log.debug("请求体为空");
            return ResponseResult.error(ErrorEnum.REQUEST_ERROR, "账户密码为空");
        }
        log.debug("登录账号: {}", loginRequest.getAccount());
        return ResponseResult.ofRun(() -> {
            User safeUser = userService.doLogin(
                    loginRequest,
                    request
            );
            log.debug("登陆成功: safeUser={}", safeUser);
            return safeUser;
        });
    }

    @GetMapping("/search")
    public ResponseResult<?> checkPassword(
            String username,
            long currentPage,
            long pageSize
    ) {
        log.debug("收到根据昵称({})搜索请求 currentPage={}, pageSize={}",
                username, currentPage, pageSize);
        // TODO 普通用户不能频繁搜索
        return ResponseResult.ofRun(
                () -> userService.searchWithUserName(username, currentPage, pageSize)
        );
    }
}
