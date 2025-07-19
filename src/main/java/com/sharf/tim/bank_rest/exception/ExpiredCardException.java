package com.sharf.tim.bank_rest.exception;

public class ExpiredCardException extends RuntimeException{
    public ExpiredCardException(String message) {
        super(message);
    }
}
