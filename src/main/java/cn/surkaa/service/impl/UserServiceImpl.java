package cn.surkaa.service.impl;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.surkaa.entity.User;
import cn.surkaa.mapper.UserMapper;
import cn.surkaa.service.UserService;
import cn.surkaa.utils.StringsUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

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
    public long userRegister(String account, String password, String checkPassword) {

        // 是否为空
        if (StrUtil.hasBlank(account, password, checkPassword)) {
            return -1;
        }

        // 账号长度是否不小于6位
        if (account.length() < 6) {
            return -1;
        }

        // 密码是否不小于8位
        if (password.length() < 8) {
            return -1;
        }

        // 账户是否以数字开头
        if (CharUtil.isNumber(account.charAt(0))) {
            return -1;
        }

        // 密码和校验密码是否相同
        if (!password.equals(checkPassword)) {
            return -1;
        }

        // 账户密码是否包含其他字符
        if (StringsUtils.isNotBelongLatterAndNumber(account, password)) {
            return -1;
        }

        // 账户是否重复
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        Long count = this.baseMapper.selectCount(lqw);
        if (count > 0) {
            return -1;
        }

        // 将密码加密保存
        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + password).getBytes(StandardCharsets.UTF_8)
        );
        User user = new User();
        user.setUserAccount(account);
        user.setUserPassword(encryptPassword);
        boolean flag = this.save(user);
        if (!flag) {
            // 保存失败
            return -1;
        }
        // 成功返回
        return user.getId();
    }

}