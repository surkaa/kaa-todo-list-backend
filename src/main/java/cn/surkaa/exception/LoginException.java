package cn.surkaa.exception;

import cn.surkaa.exception.error.ErrorEnum;

/**
 * 用于登录时的错误
 *
 * @author SurKaa
 */
public class LoginException extends RuntimeException {

    private final int code;

    private final String description;

    public LoginException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public LoginException(ErrorEnum errorEnum, String description) {
        this(errorEnum.getCode(), errorEnum.getMessage(), description);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
