package com.offer18.sdk.exception;

public class Offer18InvalidCredentialException extends Exception {
    public Offer18InvalidCredentialException() {
        super("There is no domain or accountID in provided details");
    }
}
