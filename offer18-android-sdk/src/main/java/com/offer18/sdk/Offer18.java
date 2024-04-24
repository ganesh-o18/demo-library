package com.offer18.sdk;

import android.content.Context;

import com.offer18.sdk.exception.Offer18ClientNotInitialiseException;
import com.offer18.sdk.exception.Offer18InvalidCredentialException;
import com.offer18.sdk.constant.Constant;
import com.offer18.sdk.constant.Env;
import com.offer18.sdk.contract.Callback;
import com.offer18.sdk.contract.Client;
import com.offer18.sdk.contract.Configuration;
import com.offer18.sdk.contract.CredentialManager;
import com.offer18.sdk.contract.Storage;
import com.offer18.sdk.logger.Offer18Logger;

import java.util.Map;
import java.util.Objects;

public class Offer18 {
    protected static Configuration configuration;
    /**
     * Offer18 client
     */
    protected static Client client;

    protected static Env env = Env.PRODUCTION;

    /**
     * Init SDK
     */
    public static void init(Context context, String apiKey, String apiSecret) throws Offer18InvalidCredentialException {
        CredentialManager credentialManager = new Offer18CredentialManager(apiKey, apiSecret);
        Configuration configuration = new Offer18Configuration(credentialManager);
        Storage storage = new Offer18Storage(context);
        configuration.setStorage(storage);
        configuration.setLogger(new Offer18Logger(storage.get(Constant.BETTER_STACK_API_KEY)));
        client = new Offer18Client(configuration);
    }

    /**
     * Track conversion
     */
    public static void trackConversion(Map<String, String> args) throws Exception {
        if (Objects.isNull(client)) {
            throw new Offer18ClientNotInitialiseException();
        }
        client.trackConversion(args, configuration);
    }

    /**
     * Track conversion
     */
    public static void trackConversion(Map<String, String> args, Callback callback) throws Exception {
        if (Objects.isNull(client)) {
            throw new Offer18ClientNotInitialiseException();
        }
        client.trackConversion(args, configuration, callback);
    }

    public static String getEnv() {
        if (Objects.isNull(client)) {
            return env.toString();
        }
        Configuration configuration = client.getConfiguration();
        if (Objects.isNull(configuration)) {
            return env.toString();
        }
        return configuration.getEnv().toString();
    }

    public static void enableDebugMode() {
        if (!Objects.isNull(client)) {
            Configuration configuration = client.getConfiguration();
            if (!Objects.isNull(configuration)) {
                configuration.enableDebugMode();
            }
        }
        env = Env.DEBUG;
    }

    public static void enableProductionMode() {
        if (!Objects.isNull(client)) {
            Configuration configuration = client.getConfiguration();
            if (!Objects.isNull(configuration)) {
                configuration.enableProductionMode();
            }
        }
        env = Env.PRODUCTION;
    }

    public static boolean isDebugModeEnabled() {
        if (!Objects.isNull(client)) {
            Configuration configuration = client.getConfiguration();
            if (!Objects.isNull(configuration)) {
                return configuration.getEnv() == Env.DEBUG;
            }
        }
        return Env.DEBUG == env;
    }

    public static boolean isProductionModeEnabled() {
        if (!Objects.isNull(client)) {
            Configuration configuration = client.getConfiguration();
            if (!Objects.isNull(configuration)) {
                return configuration.getEnv() == Env.PRODUCTION;
            }
        }
        return Env.PRODUCTION == env;
    }
}
