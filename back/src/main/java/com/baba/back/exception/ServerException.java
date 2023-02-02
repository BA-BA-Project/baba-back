package com.baba.back.exception;

public class ServerException extends RuntimeException{
    public ServerException(String message) {
        super(message);
    }
}
