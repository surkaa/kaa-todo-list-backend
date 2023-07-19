package cn.surkaa.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.surkaa.configurtaion.TokenConfig;
import cn.surkaa.exception.AuthenticationException;
import cn.surkaa.exception.PermissionDeniedException;
import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.mapper.UserMapper;
import cn.surkaa.module.User;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static cn.surkaa.contant.UserContant.*;

/**
 * @author SurKaa
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-06-19 19:46:45
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public Long userRegister(UserRegisterRequest registerRequest) {
        log.debug("开始注册");

        if (null == registerRequest) {
            log.debug("请求体为空");
            throw new AuthenticationException(ErrorEnum.REQUEST_ERROR, "账号密码为空");
        }

        String account = registerRequest.getAccount();
        String password = registerRequest.getPassword();
        String checkPassword = registerRequest.getCheckPassword();

        log.debug("注册账号: {}", account);

        // 是否为空
        if (StrUtil.hasBlank(account, password, checkPassword)) {
            log.debug("注册信息存在空值");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "注册信息存在空值");
        }

        // 密码和校验密码是否相同
        if (!password.equals(checkPassword)) {
            log.debug("密码和确认密码不匹配");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "密码和确认密码不匹配");
        }

        // 检查账号密码的合法性
        checkAccount(account);
        checkPassword(password);

        // 账号是否重复
        log.debug("开始检测账号是否已存在");
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        Long count = this.baseMapper.selectCount(lqw);
        if (count > 0) {
            log.debug("注册账号已经被使用");
            throw new AuthenticationException(ErrorEnum.REGISTER_ACCOUNT_REPEAT_ERROR);
        }
        log.debug("账号未使用可以注册");

        // 将密码加密保存
        log.debug("开始获取加密后的密码");
        String encryptPassword = getEncryptPassword(password);
        log.debug(encryptPassword);
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptPassword);
        log.debug("开始向数据库插入数据");
        boolean flag = this.save(user);
        if (!flag) {
            // 注册失败
            log.debug("注册失败");
            throw new AuthenticationException(ErrorEnum.REGISTER_ERROR);
        }
        // 成功返回
        log.debug("注册成功");
        return user.getId();
    }

    @Override
    public String doLogin(UserLoginRequest loginRequest) {
        log.debug("开始登录");

        if (null == loginRequest) {
            log.debug("请求体为空");
            throw new AuthenticationException(ErrorEnum.REQUEST_ERROR, "账号密码为空");
        }

        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();

        log.debug("登录账号: {}", account);

        // 是否为空
        if (StrUtil.hasBlank(account, password)) {
            log.debug("账号或者密码为空");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账号或者密码为空");
        }

        // 检查账号密码的合法性
        checkAccount(account);
        checkPassword(password);

        // 获取加密后的密码
        log.debug("开始获取加密后的密码");
        String encryptPassword = getEncryptPassword(password);
        log.debug(encryptPassword);
        // 条件查询匹配账号的用户
        log.debug("开始查询并匹配账号的用户");
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        User user = this.baseMapper.selectOne(lqw);
        if (null == user) {
            log.debug("没有找到账号匹配的信息");
            throw new AuthenticationException(ErrorEnum.LOGIN_NOTFOUND_USER_ERROR);
        }
        log.debug("查找成功");
        if (!user.getUserPassword().equals(encryptPassword)) {
            log.debug("密码不正确");
            throw new AuthenticationException(ErrorEnum.LOGIN_PASSWORD_ERROR);
        }
        log.debug("匹配成功");
        User safeUser = createSafeUser(user);

        log.debug("开始保存登录用户到map");
        String putToken = TokenConfig.putToken(safeUser.getId());

        log.debug("保存成功");
        log.debug("登录成功");
        return putToken;
    }

    @Override
    public boolean logout(String token) {
        return TokenConfig.remove(token);
    }

    @Override
    public User updateUserInfo(User update, String token) {
        if (null == update) {
            throw new PermissionDeniedException(ErrorEnum.PARAM_ERROR, "更新为空");
        }
        // 先根据user的id检测是否有这个用户
        log.debug("检测是否存在要更新的这个用户");
        User select = getBaseMapper().selectById(update.getId());
        if (null == select) {
            log.debug("找不到要更新的数据");
            throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_FOR_OPERATION_ERROR);
        }
        // 检查是否可以并获取可更新后的信息
        log.debug("存在! 开始检查是否可以更新");
        User forUpdate = checkUserForUpdate(update, token);
        log.debug("可以更新 即将开始更新");
        getBaseMapper().updateById(forUpdate);
        log.debug("更新成功");
        // 获取已经修改后的
        User updated = getBaseMapper().selectById(forUpdate.getId());
        // 脱敏
        return createSafeUser(updated);
    }

    @Override
    public IPage<User> searchWithUserName(String username, long currentPage, long pageSize) {
        log.debug("开始通过昵称匹配用户");
        // TODO username为空时可能出现bug
        // 分页对象
        PageDTO<User> page = new PageDTO<>(currentPage, pageSize);
        // 条件查询对象
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // 配置昵称相似条件
        // TODO 下面的去掉注释
//        if (username.isBlank()) {
//            log.debug("不能以空的用户名进行搜索");
//            return page;
//        }
        lqw.like(
                StrUtil.isNotBlank(username),
                User::getUsername,
                username
        );
        PageDTO<User> res = this.page(page, lqw);
        long max = res.getPages();
        if (max < currentPage) {
            log.debug("查询的页号大于最大页号");
            log.debug("正在查询最后一页: currentPage={}", max);
            page.setCurrent(max);
            res = this.page(page, lqw);
        }
        // 脱敏用户数据
        res.setRecords(
                res.getRecords().stream().map(this::createSafeUser).collect(Collectors.toList())
        );
        log.debug("查询成功");
        return res;
    }

    @Override
    public Boolean removeByIdWithUserRole(Long id, String token) {
        User removeUser = getBaseMapper().selectById(id);
        if (removeUser == null) {
            throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_FOR_OPERATION_ERROR);
        }
        User user = getUserByToken(token);
        switch (user.getUserRole()) {
            case DEFAULT_USER -> {
                log.debug("普通用户{}尝试删除普通用户{}", user.getId(), removeUser.getId());
                throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "您无法删除其他用户");
            }
            case ADMIN_USER -> {
                // 管理员用户只能删除普通用户
                if (removeUser.getUserRole() == DEFAULT_USER) {
                    log.debug("管理员用户{}尝试删除普通用户{}", user.getId(), removeUser.getId());
                    int countOfDelete = getBaseMapper().deleteById(removeUser.getId());
                    return countOfDelete == 1;
                }
                log.debug("管理员用户{}尝试删除管理员用户{}", user.getId(), removeUser.getId());
                throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "您无法删除其他用户");
            }
            case ROOT_USER -> {
                // 超级管理员无法删除其他超级管理员
                if (removeUser.getUserRole() == ROOT_USER) {
                    log.debug("超级管理员用户{}尝试删除超级管理员用户{}", user.getId(), removeUser.getId());
                    throw new PermissionDeniedException(ErrorEnum.NOT_FOUND_USER_ROLE_ERROR, "您无法删除其他用户");
                }
                log.debug("超级管理员用户{}尝试删除用户{}", user.getId(), removeUser.getId());
                int countOfDelete = getBaseMapper().deleteById(removeUser.getId());
                return countOfDelete == 1;
            }
            default -> throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_ROLE_ERROR);
        }
    }

    /**
     * 检查更新操作是否合法
     *
     * @param user    更新后的用户数据
     * @param token token
     * @return 允许更新后的可更新内容
     * @throws PermissionDeniedException 权限不足更新失败时
     */
    private User checkUserForUpdate(User user, String token) {
        User loginUser = getUserByToken(token);
        // 根据操作者的不同决定不同的更新
        switch (loginUser.getUserRole()) {
            case DEFAULT_USER -> {
                log.debug("默认用户");
                // 默认用户只能修改自己的
                if (loginUser.getId().equals(user.getId())) {
                    log.debug("尝试修改自身信息");
                    // 移除不允许修改的参数
                    return checkUpdateUserProperty(user, DEFAULT_USER);
                }
                log.debug("尝试修改其他人的信息");
                throw new PermissionDeniedException(ErrorEnum.UPDATE_OPERATION_DENIED_ERROR, "您无法修改其他用户的信息");
            }
            case ADMIN_USER -> {
                log.debug("管理员用户");
                // 管理员只能修改自己或者普通用户的信息
                if (loginUser.getId().equals(user.getId()) || DEFAULT_USER == user.getUserRole()) {
                    log.debug("尝试修改自身或者普通用户的信息");
                    // 移除不允许修改的参数
                    return checkUpdateUserProperty(user, ADMIN_USER);
                }
                log.debug("尝试更改其他管理员的信息");
                throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "您无法修改其他管理员的信息");
            }
            case ROOT_USER -> {
                log.debug("超级管理员用户");
                // 超级管理员无法修改其他超级管理员的信息
                if (ROOT_USER == user.getUserRole()) {
                    log.debug("尝试更改其他超级管理员的信息");
                    throw new PermissionDeniedException(ErrorEnum.UPDATE_OPERATION_DENIED_ERROR, "您无法修改其他超级管理员的信息");
                }
                log.debug("尝试修改普通用户或者管理员的信息");
                // 移除不允许修改的参数
                return checkUpdateUserProperty(user, ROOT_USER);
            }
            default ->
                    throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_ROLE_ERROR, "用户角色异常 请尝试重新登陆");
        }
    }

    /**
     * 根据不同角色移除掉部分重要系统属性或者直接抛出异常
     *
     * @param user        要更新后的用户信息
     * @param defaultUser 操作者角色
     * @return 更新后的信息
     */
    private User checkUpdateUserProperty(User user, int defaultUser) {
        // 检查更新是否包含了密码
        log.debug("开始检查更新是否包含了密码");
        String password = user.getUserPassword();
        if (password != null) {
            log.debug("有更新密码 将替换成加密后的密文");
            String passwordForUpdate = getEncryptPassword(password);
            user.setUserPassword(passwordForUpdate);
            log.debug("替换完成");
        }

        switch (defaultUser) {
            case DEFAULT_USER -> {
                if (user.getUserAccount() != null) {
                    // 账号不可以修改
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "账号不可以修改");
                }
                // 密码可以修改 // TODO 下次可以记录修改了啥
                // 昵称可以修改
                // 头像可以修改
                user.setAvatarId(null); // TODO 涉及外键暂时不可修改
                // 性别可以修改
                // 电话可以修改
                // 邮箱可以修改
                // 创建时间无法修改
                if (user.getCreateTime() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "创建时间无法修改");
                }
                // 更新时间不能修改
                if (user.getUpdateTime() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "更新时间不能修改");
                }
                // 状态不可以修改
                if (user.getUserStatus() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "状态不可以修改");
                }
                if (user.getUserRole() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "不能升降配用户角色");
                }
                // 删除可以修改
                return user;
            }
            case ADMIN_USER -> {
                // 账号可以修改
                // 密码可以修改
                // 昵称可以修改
                // 头像可以修改
                user.setAvatarId(null); // TODO 涉及外键暂时不可修改
                // 性别可以修改
                // 电话可以修改
                // 邮箱可以修改
                // 创建时间无法修改
                if (user.getCreateTime() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "创建时间无法修改");
                }
                // 更新时间不能修改
                if (user.getUpdateTime() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "更新时间不能修改");
                }
                // 状态可以修改
                // 不能升降配用户角色
                if (user.getUserRole() != null) {
                    throw new PermissionDeniedException(ErrorEnum.OPERATION_DENIED_ERROR, "不能升降配用户角色");
                }
                // 删除可以修改
                return user;
            }
            case ROOT_USER -> {
                // 都能改
                return user;
            }
            default -> throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_ROLE_ERROR);
        }
    }
}