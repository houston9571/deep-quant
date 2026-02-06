package com.optimus.exception;

public class EncryptException extends RuntimeException {

    public EncryptException() {
        super("Encrypted data failed. (加解密数据失败)");
    }

    public EncryptException(String message) {
        super(message);
    }

}
