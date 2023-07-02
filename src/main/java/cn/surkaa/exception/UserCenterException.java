package cn.surkaa.exception;

import cn.surkaa.exception.error.ErrorEnum;

/**
 * 自定义异常类
 * <p>
 * 全局顶级异常类
 *
 * @author SurKaa
 */
public class UserCenterException extends RuntimeException {
    private final int code;

    private final String description;

    public UserCenterException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public UserCenterException(ErrorEnum errorEnum, String description) {
        this(errorEnum.getCode(), errorEnum.getMessage(), description);
    }

    public UserCenterException(ErrorEnum errorEnum) {
        this(errorEnum.getCode(), errorEnum.getMessage(), "");
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
