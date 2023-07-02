package cn.surkaa.exception.error;

/**
 * 记录发生错误时的错误信息
 *
 * @author SurKaa
 */
public enum ErrorEnum {

    SUCCESS(0, "success"),
    PARAM_ERROR(5000, "参数问题"),
    REQUEST_ERROR(6000, "空请求"),
    REGISTER_ACCOUNT_REPEAT_ERROR(6100, "注册账号已经被使用"),
    REGISTER_ERROR(6101, "注册失败"),
    NOT_FOUND_USER_INFO(6150, "无法获取登录信息"),
    LOGIN_NOTFOUND_USER_ERROR(6200, "没有找到要登陆的用户"),
    LOGIN_PASSWORD_ERROR(6201, "登录密码错误"),
    SYSTEM_ERROR(10000, "服务器故障...");

    private final int code;

    private final String message;

    /**
     * 构造
     *
     * @param code    错误代码
     * @param message 错误信息
     */
    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
