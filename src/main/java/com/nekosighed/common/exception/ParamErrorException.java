package com.nekosighed.common.exception;

/**
 * 参数错误异常
 */
public class ParamErrorException extends RuntimeException {
    public ParamErrorException() {
        super();
    }

    public ParamErrorException(String message) {
        super(message);
    }
}
