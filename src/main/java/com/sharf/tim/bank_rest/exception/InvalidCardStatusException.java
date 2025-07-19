package com.sharf.tim.bank_rest.exception;

public class InvalidCardStatusException extends RuntimeException{
    public InvalidCardStatusException(String message) {
        super(message);
    }
}
