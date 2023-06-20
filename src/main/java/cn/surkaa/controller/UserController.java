package cn.surkaa.controller;

import cn.surkaa.module.User;
import cn.surkaa.module.request.RequestResult;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
     * @return {@link RequestResult}
     */
    @PostMapping("/register")
    public RequestResult register(@RequestBody UserRegisterRequest registerRequest) {
        if (registerRequest == null) {
            // 请求体为空
            return RequestResult.failed();
        }
        long userId = userService.userRegister(
                registerRequest.getAccount(),
                registerRequest.getPassword(),
                registerRequest.getCheckPassword()
        );
        return RequestResult.succeed(userId);
    }

    /**
     * 登录逻辑
     *
     * @param loginRequest 登录请求体
     * @param request      请求
     * @return {@link RequestResult}
     */
    @PostMapping("/login")
    public RequestResult login(
            @RequestBody UserLoginRequest loginRequest,
            HttpServletRequest request
    ) {
        if (loginRequest == null) {
            // 请求体为空
            return RequestResult.failed();
        }
        User safeUser = userService.doLogin(
                loginRequest.getAccount(),
                loginRequest.getPassword(),
                request
        );
        return RequestResult.succeed(safeUser);
    }


    @GetMapping("/search/{currentPage}/{pageSize}/{username}")
    public RequestResult search(
            @PathVariable String username,
            @PathVariable long currentPage,
            @PathVariable long pageSize
    ) {
        IPage<User> page = userService.search(username, currentPage, pageSize);
        return RequestResult.succeed(page);
    }
}
