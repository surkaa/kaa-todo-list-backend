package cn.surkaa.module.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3080961583213659928L;

    // 账号
    private String account;

    // 密码
    private String password;

}
