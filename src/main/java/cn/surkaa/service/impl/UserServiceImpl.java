package cn.surkaa.service.impl;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.surkaa.exception.AuthenticationException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.mapper.UserMapper;
import cn.surkaa.module.User;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.service.UserService;
import cn.surkaa.utils.StringsUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static cn.surkaa.contant.UserContant.LOGIN_STATE;

/**
 * @author SurKaa
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-06-19 19:46:45
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    // 混淆密码
    private static final String SALT = "surkaa";

    /**
     * 用户注册
     * <h2>注册逻辑 注册条件</h2>
     * <ul>
     *     <li>账户密码以及确认密码都不为空(不是null 不是空字符)</li>
     *     <li>账户长度不小于<strong>6</strong>位</li>
     *     <li>密码不小于<strong>8</strong>位</li>
     *     <li>账户不能以数字开头</li>
     *     <li>密码和校验密码相同</li>
     *     <li>账户和密码只能包含如下字符<pre>{@code a-z A-Z 0-9}</pre></li>
     *     <li>账户不重复</li>
     *     <li>对密码进行加密保存</li>
     * </ul>
     *
     * @param registerRequest 注册请求体
     * @return 注册成功后的用户id
     */
    @Override
    public Long userRegister(UserRegisterRequest registerRequest) {
        log.debug("开始注册");
        String account = registerRequest.getAccount();
        String password = registerRequest.getPassword();
        String checkPassword = registerRequest.getCheckPassword();

        // 是否为空
        if (StrUtil.hasBlank(account, password, checkPassword)) {
            log.debug("注册信息存在空值");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "注册信息存在空值");
        }

        // 账号长度是否不小于6位
        if (account.length() < 6) {
            log.debug("注册账号长度小于6位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "注册账号长度小于6位");
        }

        // 密码是否不小于8位
        if (password.length() < 8) {
            log.debug("注册密码长度小于8位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "注册密码长度小于8位");
        }

        // 账户是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            log.debug("账户不能以数字开头");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账户不能以数字开头");
        }

        // 密码和校验密码是否相同
        if (!password.equals(checkPassword)) {
            log.debug("密码和校验密码不匹配");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "密码和校验密码不匹配");
        }

        // 账户密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account, password)) {
            log.debug("账户或者密码中含有其他字符(只能包含大小写字母以及数字)");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账户或者密码中含有其他字符(只能包含大小写字母以及数字)");
        }

        // 账户是否重复
        log.debug("开始检测账户是否已存在");
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        Long count = this.baseMapper.selectCount(lqw);
        if (count > 0) {
            log.debug("注册账号已经被使用");
            throw new AuthenticationException(ErrorEnum.REGISTER_ACCOUNT_REPEAT_ERROR);
        }
        log.debug("账户未使用可以注册");

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

    /**
     * 用户登录
     * <h2>登录逻辑 登录条件</h2>
     *
     * <ul>
     *     <li>账户密码都不为空(不是null 不是空字符)</li>
     *     <li>账户长度不小于<strong>6</strong>位</li>
     *     <li>密码不小于<strong>8</strong>位</li>
     *     <li>账户不能以数字开头</li>
     *     <li>账户和密码只能包含如下字符<pre>{@code a-z A-Z 0-9}</pre></li>
     * </ul>
     *
     * @param loginRequest 登录请求体
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User doLogin(UserLoginRequest loginRequest, HttpServletRequest request) {
        log.debug("开始登录");

        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();

        // 是否为空
        if (StrUtil.hasBlank(account, password)) {
            log.debug("账号或者密码为空");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账号或者密码为空");
        }

        // 账号长度是否不小于6位
        if (account.length() < 6) {
            log.debug("登录账户长度小于6位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "登录账户长度小于6位");
        }

        // 密码是否不小于8位
        if (password.length() < 8) {
            log.debug("登录密码小于8位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "登录密码小于8位");
        }

        // 账户是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            log.debug("账户不能以数字开头");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账户不能以数字开头");
        }

        // 账户密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account, password)) {
            log.debug("账户或者密码中含有其他字符(只能包含大小写字母以及数字)");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账户或者密码中含有其他字符(只能包含大小写字母以及数字)");
        }

        // 获取加密后的密码
        log.debug("开始获取加密后的密码");
        String encryptPassword = getEncryptPassword(password);
        log.debug(encryptPassword);
        // 条件查询匹配账号的用户
        log.debug("开始查询并匹配账户的用户");
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        User user = this.baseMapper.selectOne(lqw);
        if (user == null) {
            log.debug("没有找到账户匹配的信息");
            throw new AuthenticationException(ErrorEnum.LOGIN_NOTFOUND_USER_ERROR);
        }
        log.debug("查找成功");
        if (!user.getUserPassword().equals(encryptPassword)) {
            log.debug("密码不正确");
            throw new AuthenticationException(ErrorEnum.LOGIN_PASSWORD_ERROR);
        }
        log.debug("匹配成功");
        User safeUser = createSafeUser(user);

        log.debug("开始保存登录用户到session");
        // 将登录状态记录到请求体的session中
        if (request == null) {
            // 无法将登录状态保存
            log.debug("保存失败");
            throw new AuthenticationException(ErrorEnum.SYSTEM_ERROR);
        }
        request.getSession().setAttribute(LOGIN_STATE, safeUser);

        log.debug("保存成功");
        log.debug("登录成功");
        return safeUser;
    }

    /**
     * 根据用户昵称搜索用户并分页
     *
     * @param username    用户昵称
     * @param currentPage 当前页号
     * @param pageSize    页大小
     * @return 分页结果
     */
    @Override
    public IPage<User> searchWithUserName(String username, long currentPage, long pageSize) {
        log.debug("开始通过昵称匹配用户");
        // 分页对象
        PageDTO<User> page = new PageDTO<>(currentPage, pageSize);
        // 条件查询对象
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        // 配置昵称相似条件
        lqw.like(
                StrUtil.isNotBlank(username),
                User::getUsername,
                username
        );
        return this.page(page, lqw);
    }

    /**
     * 用户注册
     *
     * @param account       注册账户
     * @param password      注册密码
     * @param checkPassword 确认密码
     * @return 成功注册的用户id
     */
    @Override
    public Long userRegister(String account, String password, String checkPassword) {
        log.debug("开始注册");
        return userRegister(new UserRegisterRequest(account, password, checkPassword));
    }

    /**
     * 登录
     *
     * @param account  账号
     * @param password 密码
     * @param request  请求体
     * @return 请求所得用户
     */
    @Override
    public User doLogin(String account, String password, HttpServletRequest request) {
        log.debug("开始登录");
        return doLogin(new UserLoginRequest(account, password), request);
    }

    private String getEncryptPassword(String originPassword) {
        log.debug("开始获取加密密码");
        return DigestUtils.md5DigestAsHex(
                (SALT + originPassword).getBytes(StandardCharsets.UTF_8)
        );
    }

    public static User createSafeUser(User user) {
        log.debug("开始对用户信息脱敏");
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUsername(user.getUsername());
        safeUser.setAvatarId(user.getAvatarId());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setUserRole(user.getUserRole());
        // 逻辑删除
        // safeUser.setIsDelete(user.getIsDelete());
        return safeUser;
    }

}