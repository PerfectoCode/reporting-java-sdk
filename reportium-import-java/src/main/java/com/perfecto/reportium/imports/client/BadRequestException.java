package com.perfecto.reportium.imports.client;

/**
 * Created by michaeld on 3/10/2017.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
