package com.blog.demo.exception;

public class GlobalException extends RuntimeException {
    public GlobalException(String message) {
        super(message);
    }
    public GlobalException(String message, Throwable cause) {
        super(message, cause);
    }
    public GlobalException(Throwable cause) {
        super(cause);
    }
}
