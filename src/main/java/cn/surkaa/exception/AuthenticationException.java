package cn.surkaa.exception;

import cn.surkaa.exception.error.ErrorEnum;

/**
 * 用于登录时的错误
 *
 * @author SurKaa
 */
public class AuthenticationException extends UserCenterException {

    public AuthenticationException(int code, String message, String description) {
        super(code, message, description);
    }

    public AuthenticationException(ErrorEnum errorEnum, String description) {
        super(errorEnum, description);
    }

    public AuthenticationException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
