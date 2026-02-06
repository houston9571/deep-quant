package com.optimus.exception;

/**
 * 参数验证错误异常
 **/
public class ParamValidatorException extends RuntimeException{

    public ParamValidatorException(String message) {
        super(message);
    }

}
