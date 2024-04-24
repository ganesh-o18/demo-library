package com.offer18.sdk.exception;

public class Offer18ClientNotInitialiseException extends Exception {
    public Offer18ClientNotInitialiseException() {
        super("Client is not initialised");
    }
}
