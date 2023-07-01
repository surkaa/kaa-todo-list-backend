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

import java.util.Objects;

import static cn.surkaa.contant.UserContant.LOGIN_STATE;

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
    public ResponseResult<?> searchUserWithUsername(
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

    @GetMapping
    public ResponseResult<?> getSelf(
            HttpServletRequest request
    ) {
        try {
            Object o = request.getSession().getAttribute(LOGIN_STATE);
            Objects.requireNonNull(o);
            User user = (User) o;
            return ResponseResult.succeed(user);
        } catch (Exception e) {
            return ResponseResult.error(ErrorEnum.NOT_FOUND_USER_INFO);
        }
    }
}
