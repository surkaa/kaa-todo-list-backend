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
