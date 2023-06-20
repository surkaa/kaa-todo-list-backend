package cn.surkaa.module.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2822086678441354537L;

    // 登录账号
    private String account;

    // 登录密码
    private String password;

    // 确认密码
    private String checkPassword;

}
