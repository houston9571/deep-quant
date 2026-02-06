package com.optimus.exception;

import com.optimus.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetWorkException extends ServiceException {


    public NetWorkException(ErrorCode error, Object... args) {
        super(error, args);
        log.error("code:{} msg:{}", super.getCode(), super.getMsg());
    }

    public NetWorkException(int code, String msg) {
        super(code, msg);
        log.error("code:{} msg:{}", super.getCode(), super.getMsg());
    }
}
