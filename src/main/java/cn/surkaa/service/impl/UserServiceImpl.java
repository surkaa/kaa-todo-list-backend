package cn.surkaa.service.impl;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.surkaa.mapper.UserMapper;
import cn.surkaa.module.User;
import cn.surkaa.service.UserService;
import cn.surkaa.utils.StringsUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static cn.surkaa.contant.UserContant.LOGIN_STATE;

/**
 * @author SurKaa
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-06-19 19:46:45
 */
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
     * @param account       注册账号
     * @param password      注册密码
     * @param checkPassword 确认密码
     * @return 注册成功后的用户id
     */
    @Override
    public Long userRegister(String account, String password, String checkPassword) {

        // 是否为空
        if (StrUtil.hasBlank(account, password, checkPassword)) {
            return -1L;
        }

        // 账号长度是否不小于6位
        if (account.length() < 6) {
            return -1L;
        }

        // 密码是否不小于8位
        if (password.length() < 8) {
            return -1L;
        }

        // 账户是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            return -1L;
        }

        // 密码和校验密码是否相同
        if (!password.equals(checkPassword)) {
            return -1L;
        }

        // 账户密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account, password)) {
            return -1L;
        }

        // 账户是否重复
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        Long count = this.baseMapper.selectCount(lqw);
        if (count > 0) {
            return -1L;
        }

        // 将密码加密保存
        String encryptPassword = getEncryptPassword(password);
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptPassword);
        boolean flag = this.save(user);
        if (!flag) {
            // 保存失败
            return -1L;
        }
        // 成功返回
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
     * @param account  登录账号
     * @param password 登录密码
     * @param request  请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User doLogin(String account, String password, HttpServletRequest request) {
        // 是否为空
        if (StrUtil.hasBlank(account, password)) {
            return null;
        }

        // 账号长度是否不小于6位
        if (account.length() < 6) {
            return null;
        }

        // 密码是否不小于8位
        if (password.length() < 8) {
            return null;
        }

        // 账户是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            return null;
        }

        // 账户密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account, password)) {
            return null;
        }

        String encryptPassword = getEncryptPassword(password);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw
                .eq(User::getUserAccount, account)
                .eq(User::getUserPassword, encryptPassword);
        User user = this.baseMapper.selectOne(lqw);
        if (user == null) {
            // 获取失败
            return null;
        }
        User safeUser = createSafeUser(user);

        // 将登录状态记录到请求体的session中
        if (request == null) {
            // 无法将登录状态保存
            return safeUser;
        }
        request.getSession().setAttribute(LOGIN_STATE, safeUser);

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
    public IPage<User> search(String username, long currentPage, long pageSize) {
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

    private String getEncryptPassword(String originPassword) {
        return DigestUtils.md5DigestAsHex(
                (SALT + originPassword).getBytes(StandardCharsets.UTF_8)
        );
    }

    public static User createSafeUser(User user) {
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