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
    NULL_POINTER_ERROR(6005, "空指针"),
    NOT_LOGIN_ERROR(6099, "请您登录后尝试"),
    REGISTER_ACCOUNT_REPEAT_ERROR(6100, "注册账号已经被使用"),
    REGISTER_ERROR(6101, "注册失败"),
    NOT_FOUND_USER_INFO(6150, "无法获取登录信息"),
    LOGIN_NOTFOUND_USER_ERROR(6200, "没有找到要登陆的用户"),
    LOGIN_PASSWORD_ERROR(6201, "登录密码错误"),
    ILLEGAL_TOKEN_ERROR(6300, "token已经过期"),
    NOT_FOUND_USER_ROLE_ERROR(6999, "用户角色非法"),
    OPERATION_DENIED_ERROR(7000, "操作权限不足"),
    NOT_FOUND_USER_FOR_OPERATION_ERROR(7099, "没有找到用户数据"),
    UPDATE_OPERATION_DENIED_ERROR(7100, "您无法修改他人或者管理员的信息"),
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
