package com.optimus.exception;

import com.optimus.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code = 1000;

    private String msg;


    public ServiceException(ErrorCode error, Object... args) {
        super(error.getMsg(args));
        msg = error.getMsg(args);
        code = error.getCode();
        log.error("code:{} msg:{}", code, msg);
    }


    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg);
    }

    public ServiceException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg);
    }

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg);
    }

    public ServiceException(int code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg);
    }


    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
