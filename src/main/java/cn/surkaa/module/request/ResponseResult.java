package cn.surkaa.module.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
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
    public static ResponseResult<Object> failed() {
        return new ResponseResult<>(-1, null, "", "");
    }

    /**
     * 直接失败并包含提示信息
     *
     * @return code=-1的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult<Object> failed(String message) {
        return new ResponseResult<>(-1, null, message, "");
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
            return ResponseResult.failed();
        }
    }

    /**
     * 根据某段代码(函数式接口的实现)运行是否报错生成Result
     *
     * @param execute 代码(函数式接口) {@link ResultExecute ResultExecute}
     * @return {@link ResponseResult ResponseResult}
     */
    public static ResponseResult<?> ofRun(ResultExecute execute) {
        try {
            Object data = execute.execute();
            return ResponseResult.succeed(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed("服务器故障... 请稍后再试");
        }
    }

    /**
     * 用于ofRun方法传入的函数段参数
     *
     * @author SurKaa
     */
    public interface ResultExecute {
        /**
         * 运行的代码
         *
         * @return 返回的数据
         */
        Object execute();
    }

}
