package cn.surkaa.module.utils;

import cn.surkaa.exception.UserCenterException;
import cn.surkaa.exception.error.ErrorEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
@Slf4j
@SuppressWarnings("unused")
public class ResponseResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 2930639290615220583L;

    /**
     * 错误代码
     * 成功为0 否则失败
     */
    private final int code;

    // 数据
    private final T data;

    // 信息
    private final String message;

    // 详细信息
    private final String description;

    /**
     * 构造
     *
     * @param code    返回代码
     * @param data    数据
     * @param message 信息
     */
    public ResponseResult(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    /**
     * 直接失败 无提示信息
     *
     * @return code=-1 & message=null 的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> error() {
        return new ResponseResult<>(-1, null, "failed", "");
    }

    /**
     * 直接失败并包含提示信息
     *
     * @return code=-1的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> error(String message) {
        return new ResponseResult<>(-1, null, message, "");
    }

    /**
     * 直接失败并包含提示信息
     *
     * @return code=-1的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> error(int code, String message, String description) {
        return new ResponseResult<>(code, null, message, description);
    }

    public static ResponseResult<Object> error(ErrorEnum error) {
        return new ResponseResult<>(
                error.getCode(), null, error.getMessage(), ""
        );
    }

    public static ResponseResult<Object> error(UserCenterException e) {
        return new ResponseResult<>(
                e.getCode(), null, e.getMessage(), e.getDescription()
        );
    }

    public static ResponseResult<Object> error(ErrorEnum error, String description) {
        return new ResponseResult<>(
                error.getCode(), null, error.getMessage(), description
        );
    }

    /**
     * 没有返回值的成功结果
     *
     * @return code=true的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> succeed() {
        return new ResponseResult<>(0, null, null, "");
    }

    /**
     * 成功获取 结果在data中
     *
     * @param data 获取得到的数据
     * @return code=true包含数据的 {@link ResponseResult ResponseResult}
     */
    public static <T> ResponseResult<T> succeed(T data) {
        return new ResponseResult<>(0, data, null, "");
    }

    /**
     * 成功获取并包含提示信息
     *
     * @param data 获取得到的数据
     * @return code=true包含数据的 {@link ResponseResult ResponseResult}
     */
    public static <T> ResponseResult<T> succeed(T data, String message) {
        return new ResponseResult<>(0, data, message, "");
    }

    public static <T> ResponseResult<T> succeed(T data, String message, String description) {
        return new ResponseResult<>(0, data, message, description);
    }

    /**
     * 无数据的根据条件生成的Result
     *
     * @param condition 条件
     * @return code=condition包含数据的 {@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> condition(boolean condition) {
        if (condition) {
            return ResponseResult.succeed();
        } else {
            return ResponseResult.error();
        }
    }

}
