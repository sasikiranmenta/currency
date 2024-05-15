package com.sjsu.currency;

public class TransactionError extends RuntimeException {
    public TransactionError(String error) {
        super(error);
    }
}
