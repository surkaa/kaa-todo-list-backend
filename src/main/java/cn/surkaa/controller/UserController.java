package cn.surkaa.controller;

import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.module.User;
import cn.surkaa.module.request.ResponseResult;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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
     * @return {@link ResponseResult}
     */
    @PostMapping("/register")
    public ResponseResult<?> register(
            @RequestBody UserRegisterRequest registerRequest
    ) {
        if (registerRequest == null) {
            // 请求体为空
            return ResponseResult.error(ErrorEnum.REQUEST_ERROR);
        }
        return ResponseResult.ofRun(() -> {
            long userId = userService.userRegister(registerRequest);
            return ResponseResult.succeed(userId);
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
        if (loginRequest == null) {
            // 请求体为空
            return ResponseResult.error(ErrorEnum.REQUEST_ERROR);
        }
        return ResponseResult.ofRun(() -> {
            User safeUser = userService.doLogin(
                    loginRequest,
                    request
            );
            return ResponseResult.succeed(safeUser);
        });
    }

    @GetMapping("/search/{currentPage}/{pageSize}/{username}")
    public ResponseResult<?> search(
            @PathVariable String username,
            @PathVariable long currentPage,
            @PathVariable long pageSize
    ) {
        return ResponseResult.ofRun(
                () -> userService.search(username, currentPage, pageSize)
        );
    }
}
