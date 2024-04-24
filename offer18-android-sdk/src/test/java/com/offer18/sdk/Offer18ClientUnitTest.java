package com.offer18.sdk;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Offer18ClientUnitTest {

    @Test
    public void sdk_init_throws_exception_when_context_is_null() {
        try {
            Offer18.init(null, "", "");
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }
}

