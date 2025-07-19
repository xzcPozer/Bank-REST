package com.sharf.tim.bank_rest.exception;

public class InvalidTransferException extends RuntimeException{
    public InvalidTransferException(String message) {
        super(message);
    }
}
