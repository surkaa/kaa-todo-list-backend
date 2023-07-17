package cn.surkaa.configurtaion;

import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.module.TokenInfo;
import cn.surkaa.utils.StringsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SurKaa
 */
@Slf4j
@Configuration
@EnableScheduling // 开启计划任务的支持
public class TokenConfig {

    private static final Map<String, TokenInfo> data = new ConcurrentHashMap<>();

    // 登陆过期的时间: 一小时
    private static final long OVERDUE_TIME = 360_0000L;

    /**
     * 存放用户登录了的信息
     *
     * @param id 用户id
     * @return token
     */
    public static String putToken(Long id) {
        TokenInfo user = new TokenInfo(id);
        String token = StringsUtils.md5DigestAsHex(id + System.currentTimeMillis());
        log.debug("存放登录用户: token: {}, user: {}, 当前登录用户池数量: {}",
                token, user, data.size() + 1);
        data.put(token, user);
        return token;
    }

    /**
     * 检查是否存在token
     *
     * @param token token
     * @return 是否存在
     */
    public static boolean check(String token) {
        return data.containsKey(token);
    }

    /**
     * 通过token获取id
     *
     * @param token token
     * @return id
     */
    public static Long getLoginId(String token) {
        if (check(token)) {
            TokenInfo tokenInfo = data.get(token);
            log.debug("获取token登录用户: {}", tokenInfo);
            return tokenInfo.getId();
        }
        throw new UserCenterException(ErrorEnum.ILLEGAL_TOKEN_ERROR);
    }

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0/60 * *  * * ? ")
    public void removeOverdueToken() {
        long currented = System.currentTimeMillis();
        data.forEach((s, tokenInfo) -> {
            if (currented - tokenInfo.getTime() > OVERDUE_TIME) {
                log.debug("token:{} 将被移除", s);
                data.remove(s);
            }
        });
    }

}
