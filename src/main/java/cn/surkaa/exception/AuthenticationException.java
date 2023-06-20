package cn.surkaa.exception;

import cn.surkaa.exception.error.ErrorEnum;

/**
 * 用于登录时的错误
 *
 * @author SurKaa
 */
public class AuthenticationException extends RuntimeException {

    private final int code;

    private final String description;

    public AuthenticationException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public AuthenticationException(ErrorEnum errorEnum, String description) {
        this(errorEnum.getCode(), errorEnum.getMessage(), description);
    }

    public AuthenticationException(ErrorEnum errorEnum) {
        this(errorEnum.getCode(), errorEnum.getMessage(), "");
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
