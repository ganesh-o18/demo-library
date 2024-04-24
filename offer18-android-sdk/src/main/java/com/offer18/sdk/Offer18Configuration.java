package com.offer18.sdk;

import com.offer18.sdk.constant.Constant;
import com.offer18.sdk.constant.Env;
import com.offer18.sdk.contract.Configuration;
import com.offer18.sdk.contract.CredentialManager;
import com.offer18.sdk.contract.Logger;
import com.offer18.sdk.contract.Storage;

import java.util.Calendar;
import java.util.Objects;

class Offer18Configuration implements Configuration {
    protected CredentialManager credentialManager;
    protected Env env = Env.PRODUCTION;
    protected Storage storage;
    protected Logger logger;

    public Offer18Configuration(CredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

    @Override
    public String getLoggingMode() {
        return null;
    }

    @Override
    public Env getEnv() {
        return this.env;
    }

    @Override
    public String getDomain() {
        return this.credentialManager.getDomain();
    }

    @Override
    public String getAccountID() {
        return this.credentialManager.getAccountID();
    }

    @Override
    public Storage getStorage() {
        return this.storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void enableDebugMode() {
        this.env = Env.DEBUG;
    }

    @Override
    public void enableProductionMode() {
        this.env = Env.PRODUCTION;
    }

    @Override
    public String get(String key) {
        return this.getStorage().get(key);
    }

    @Override
    public boolean set(String key, String value) {
        return this.storage.set(key, value);
    }

    @Override
    public boolean remove(String key) {
        return this.storage.remove(key);
    }

    @Override
    public long getHttpDefaultTimeout() {
        return 2000;
    }

    /**
     *
     */
    @Override
    public boolean isRemoteConfigOutdated() {
        boolean isOutdated;
        if (Objects.isNull(this.storage)) {
            return true;
        }
        try {
            String lastUpdatedAt = this.storage.get(Constant.EXPIRES_AT);
            long currentUnixStamp = Calendar.getInstance().getTimeInMillis() / 1000;
            long lastUpdatedStamp = Long.parseLong(lastUpdatedAt);
            isOutdated = currentUnixStamp >= lastUpdatedStamp;
        } catch (Exception exception) {
            isOutdated = true;
        }
        return isOutdated;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public boolean isLoggingEnabled() {
        if (Objects.isNull(this.storage)) {
            return true;
        }
        return this.storage.get(Constant.SERVICE_DISCOVERY_ENABLE_LOG).equals("true");
    }
}
