package cn.surkaa.module.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3080961583213659928L;

    // 账号
    private String account;

    // 密码
    private String password;

}
