package cn.surkaa.controller;

import cn.surkaa.module.User;
import cn.surkaa.module.request.ResponseResult;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public ResponseResult<Long> register(
            @RequestBody UserRegisterRequest registerRequest
    ) {
        log.debug("收到注册请求");
        Long userId = userService.userRegister(registerRequest);
        log.debug("注册所得用户id={}", userId);
        return ResponseResult.succeed(userId);
    }

    /**
     * 登录逻辑
     *
     * @param loginRequest 登录请求体
     * @param request      请求
     * @return {@link ResponseResult}
     */
    @PostMapping("/login")
    public ResponseResult<User> login(
            @RequestBody UserLoginRequest loginRequest,
            HttpServletRequest request
    ) {
        log.debug("收到登录请求");
        User safeUser = userService.doLogin(
                loginRequest,
                request
        );
        log.debug("登陆成功: safeUser={}", safeUser);
        return ResponseResult.succeed(safeUser);
    }

    @GetMapping("/search")
    public ResponseResult<IPage<User>> searchUserWithUsername(
            String username,
            long currentPage,
            long pageSize
    ) {
        log.debug("收到根据昵称({})搜索请求 currentPage={}, pageSize={}",
                username, currentPage, pageSize);
        // TODO 普通用户不能频繁搜索
        return ResponseResult.succeed(
                userService.searchWithUserName(username, currentPage, pageSize)
        );
    }

    /**
     * 获取当前登录账号的账号信息
     *
     * @param request 请求
     * @return ResponseResult User 用户信息
     */
    @GetMapping
    public ResponseResult<User> getSelf(
            HttpServletRequest request
    ) {
        return ResponseResult.succeed(userService.getUser(request));
    }

    @PutMapping
    public ResponseResult<User> updateUser(
            @RequestBody User user,
            HttpServletRequest request
    ) {
        log.debug("开始更新用户: {}", user);
        return ResponseResult.succeed(
                userService.updateUserInfo(user, request)
        );
    }
}
