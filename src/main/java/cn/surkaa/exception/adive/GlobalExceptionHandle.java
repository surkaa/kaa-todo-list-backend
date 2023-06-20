package cn.surkaa.exception.adive;

import cn.surkaa.exception.AuthenticationException;
import cn.surkaa.exception.error.ErrorEnum;
import cn.surkaa.module.request.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author SurKaa
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseResult<?> loginException(AuthenticationException e) {
        log.error("登录错误: ", e);
        return ResponseResult.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<?> exception(Exception e) {
        log.error("Exception: ", e);
        return ResponseResult.error(ErrorEnum.SYSTEM_ERROR);
    }

}
