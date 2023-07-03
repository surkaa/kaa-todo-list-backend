package cn.surkaa.exception;

import cn.surkaa.exception.error.ErrorEnum;

/**
 * @author SurKaa
 */
public class PermissionDeniedException extends UserCenterException {

    public PermissionDeniedException(int code, String message, String description) {
        super(code, message, description);
    }

    public PermissionDeniedException(ErrorEnum errorEnum, String description) {
        super(errorEnum, description);
    }

    public PermissionDeniedException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
