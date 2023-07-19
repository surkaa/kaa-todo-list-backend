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

import static cn.surkaa.configurtaion.TokenConfig.MY_TOKEN;

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
     * @return {@link ResponseResult}
     */
    @PostMapping("/login")
    public ResponseResult<String> login(
            @RequestBody UserLoginRequest loginRequest
    ) {
        log.debug("收到登录请求");
        String token = userService.doLogin(
                loginRequest
        );
        log.debug("登陆成功: token={}", token);
        return ResponseResult.succeed(token);
    }

    @PostMapping
    public ResponseResult<Object> logout(
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        log.debug("logout: {}", token);
        return ResponseResult.succeed(userService.logout(token));
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
     * @return ResponseResult User 用户信息
     */
    @GetMapping
    public ResponseResult<User> getUserByToken(
            HttpServletRequest request
    ) {
        String token = request.getHeader(MY_TOKEN);
        log.debug("getUserByToken: {}", token);
        return ResponseResult.succeed(userService.getUserByToken(token));
    }

    // TODO 更新密码时也要添加原密码进行验证 可以写一个用于更新的Body
    @PutMapping
    public ResponseResult<User> updateUser(
            @RequestBody User user,
            String token
    ) {
        log.debug("开始更新用户: {}", user);
        return ResponseResult.succeed(
                userService.updateUserInfo(user, token)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Object> deleteById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        // TODO 谨慎操作 现在删除仅是将is_delete设置成1但是数据库中仍存在
        // TODO 不过这并未处理Todo表中的对应数据删除
        log.debug("收到删除用户: id={}", id);
        String token = request.getHeader(MY_TOKEN);
        return ResponseResult.condition(
                userService.removeByIdWithUserRole(id, token)
        );
    }
}
