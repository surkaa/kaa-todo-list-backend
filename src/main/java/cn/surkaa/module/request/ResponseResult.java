package cn.surkaa.module.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author SurKaa
 */
@Data
public class ResponseResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 2930639290615220583L;

    // 是否成功
    private final boolean flag;

    // 数据
    private final Object data;

    // 信息
    private final String message;

    /**
     * 构造
     *
     * @param flag    是否成功
     * @param data    数据
     * @param message 信息
     */
    public ResponseResult(boolean flag, Object data, String message) {
        this.flag = flag;
        this.data = data;
        this.message = message;
    }

    /**
     * 直接失败 无提示信息
     *
     * @return flag=false & message=null 的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult failed() {
        return new ResponseResult(false, null, null);
    }

    /**
     * 直接失败并包含提示信息
     *
     * @return flag=false的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult failed(String message) {
        return new ResponseResult(false, null, message);
    }

    /**
     * 没有返回值的成功结果
     *
     * @return flag=true的{@link ResponseResult ResponseResult}
     */
    public static ResponseResult succeed() {
        return new ResponseResult(true, null, null);
    }

    /**
     * 成功获取 结果在data中
     *
     * @param data 获取得到的数据
     * @return flag=true包含数据的 {@link ResponseResult ResponseResult}
     */
    public static ResponseResult succeed(Object data) {
        return new ResponseResult(true, data, null);
    }

    /**
     * 成功获取并包含提示信息
     *
     * @param data 获取得到的数据
     * @return flag=true包含数据的 {@link ResponseResult ResponseResult}
     */
    public static ResponseResult succeed(Object data, String message) {
        return new ResponseResult(true, data, message);
    }

    /**
     * 无数据的根据条件生成的Result
     *
     * @param condition 条件
     * @return flag=condition包含数据的 {@link ResponseResult ResponseResult}
     */
    public static ResponseResult condition(boolean condition) {
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
    public static ResponseResult ofRun(ResultExecute execute) {
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
