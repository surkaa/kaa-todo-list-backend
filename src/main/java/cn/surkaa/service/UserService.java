package cn.surkaa.service;

import cn.hutool.core.util.CharUtil;
import cn.surkaa.configurtaion.TokenConfig;
import cn.surkaa.exception.AuthenticationException;
import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.module.User;
import cn.surkaa.module.request.UserLoginRequest;
import cn.surkaa.module.request.UserRegisterRequest;
import cn.surkaa.utils.StringsUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SurKaa
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-06-19 19:46:45
 */
public interface UserService extends IService<User> {

    // 混淆密码
    String SALT = "surkaa";

    Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 用户注册
     * <h2>注册逻辑 注册条件</h2>
     * <ul>
     *     <li>账号密码以及确认密码都不为空(不是null 不是空字符)</li>
     *     <li>账号长度不小于<strong>6</strong>位</li>
     *     <li>密码不小于<strong>8</strong>位</li>
     *     <li>账号不能以数字开头</li>
     *     <li>密码和校验密码相同</li>
     *     <li>账号和密码只能包含如下字符<pre>{@code a-z A-Z 0-9}</pre></li>
     *     <li>账号不重复</li>
     *     <li>对密码进行加密保存</li>
     * </ul>
     *
     * @param registerRequest 注册请求体
     * @return 注册成功后的用户id
     */
    Long userRegister(UserRegisterRequest registerRequest);

    /**
     * 用户登录
     * <h2>登录逻辑 登录条件</h2>
     *
     * <ul>
     *     <li>账号密码都不为空(不是null 不是空字符)</li>
     *     <li>账号长度不小于<strong>6</strong>位</li>
     *     <li>密码不小于<strong>8</strong>位</li>
     *     <li>账号不能以数字开头</li>
     *     <li>账号和密码只能包含如下字符<pre>{@code a-z A-Z 0-9}</pre></li>
     * </ul>
     *
     * @param loginRequest 登录请求体
     * @return 脱敏后的用户信息
     */
    String doLogin(UserLoginRequest loginRequest);

    /**
     * 登出
     * @param token token
     * @return 成功与否
     */
    boolean logout(String token);

    /**
     * 根据当前用户的角色(等级)修改信息
     *
     * <h2>修改规则:</h2>
     * <ul>
     *     <li>普通用户只能修改自己的用户信息</li>
     *     <li>管理员用户可以修改普通用户和自己的用户信息</li>
     *     <li>超级管理员可以修改除其他超级管理员之外的用户信息</li>
     * </ul>
     *
     * @param entity 更改后的信息
     * @param token  token
     * @return 更改成功后的信息
     */
    User updateUserInfo(User entity, String token);

    /**
     * 根据用户昵称搜索用户并分页
     *
     * @param username    用户昵称
     * @param currentPage 当前页号
     * @param pageSize    页大小
     * @return 分页结果
     */
    IPage<User> searchWithUserName(String username, long currentPage, long pageSize);

    /**
     * 通过id并结合当前用户角色删除用户
     *
     * @param id    用户id
     * @param token token
     * @return 是否删除成功
     */
    Boolean removeByIdWithUserRole(Long id, String token);

    /**
     * 获取当前登录的用户信息
     *
     * @param token token
     * @return 登录用户
     */
    default User getUserByToken(String token) {
        try {
            Long id = TokenConfig.getLoginId(token);
            User user = getBaseMapper().selectById(id);
            return createSafeUser(user);
        } catch (Exception e) {
            throw new UserCenterException(ErrorEnum.NOT_FOUND_USER_INFO,
                    "您可能尚未未登录, 或者登陆过期吗请尝试重新登陆");
        }
    }

    /**
     * 获取加密后的密码
     *
     * @param originPassword 密码明文
     * @return 加密密码
     */
    default String getEncryptPassword(String originPassword) {
        log.debug("开始获取加密密码");
        return StringsUtils.md5DigestAsHex(SALT + originPassword);
    }

    /**
     * 用于检查账号是否合法
     *
     * @param account 账号
     * @throws AuthenticationException 当账号不合法时
     */
    default void checkAccount(String account) {
        // 账号长度是否不小于6位
        if (account.length() < 6) {
            log.debug("账号长度小于6位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账号长度小于6位");
        }

        // 账号是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            log.debug("账号不能以数字开头");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "账号不能以数字开头");
        }

        // 账号是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account)) {
            log.debug("账号中含有其他字符(只能包含大小写字母以及数字)");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR,
                    "账号中含有其他字符(只能包含大小写字母以及数字)");
        }
    }

    /**
     * 用于检查密码是否合法
     *
     * @param password 密码
     * @throws AuthenticationException 当密码不合法时
     */
    default void checkPassword(String password) {
        // 密码是否不小于8位
        if (password.length() < 8) {
            log.debug("密码长度小于8位");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR, "密码长度小于8位");
        }

        // 密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(password)) {
            log.debug("密码中含有其他字符(只能包含大小写字母以及数字)");
            throw new AuthenticationException(ErrorEnum.PARAM_ERROR,
                    "密码中含有其他字符(只能包含大小写字母以及数字)");
        }
    }

    /**
     * 用于对用户信息进行脱敏
     *
     * @param user 包含隐私的用户数据
     * @return 去除隐私的用户数据
     */
    default User createSafeUser(User user) {
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
        log.debug("脱敏成功");
        return safeUser;
    }

}
