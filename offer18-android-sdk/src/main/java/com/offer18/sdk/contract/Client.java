package com.offer18.sdk.contract;

import java.util.Map;

public interface Client {
    void trackConversion(Map<String, String> args, Configuration configuration) throws Exception;
    void trackConversion(Map<String, String> args, Configuration configuration, Callback callback) throws Exception;
    Configuration getConfiguration();
}
