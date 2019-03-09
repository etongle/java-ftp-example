package com.rxliuli.example.ftpdemo.common.util;

/**
 * 自定义的用于携带消息的异常
 * 项目中手动抛出的异常都尽量使用 GlobalException
 *
 * @author rxliuli
 */
public class GlobalException extends RuntimeException {
    /**
     * 错误码，用于全局异常拦截器使用
     */
    private final Integer code;

    public GlobalException(String message) {
        super(message);
        this.code = 500;
    }

    public GlobalException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public GlobalException(Throwable cause) {
        super(cause);
        this.code = 500;
    }


    public Integer getCode() {
        return code;
    }
}
