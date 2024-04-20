package com.offer18.sdk.worker;

import com.offer18.sdk.constant.Constant;
import com.offer18.sdk.contract.Configuration;
import com.offer18.sdk.contract.Storage;
import com.offer18.sdk.util.Offer18Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceDiscoveryWorker implements Runnable {
    CountDownLatch remoteConfigDownloadSignal;
    OkHttpClient httpClient;
    Configuration configuration;
    Map<String, String> args;

    public ServiceDiscoveryWorker(CountDownLatch remoteConfigDownloadSignal, OkHttpClient okHttpClient, Configuration configuration, Map<String, String> args) {
        this.remoteConfigDownloadSignal = remoteConfigDownloadSignal;
        this.httpClient = okHttpClient;
        this.configuration = configuration;
        this.args = args;
    }

    @Override
    public void run() {
        boolean isRemoteConfigOutdated = this.configuration.isRemoteConfigOutdated();
        if (!isRemoteConfigOutdated) {
            this.remoteConfigDownloadSignal.countDown();
            return;
        }
        this.configuration.getLogger().log("remote config is outdated");
        String url = Constant.SERVICE_DISCOVERY_ENDPOINT + "?digest=" + this.configuration.get(Constant.DIGEST);
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = this.httpClient.newCall(request).execute();
            if (response.code() == 204) {
                this.remoteConfigDownloadSignal.countDown();
                return;
            }
            if (response.isSuccessful()) {
                this.onResponse(response, isRemoteConfigOutdated);
            } else {
                this.remoteConfigDownloadSignal.countDown();
            }
        } catch (IOException e) {
            this.remoteConfigDownloadSignal.countDown();
        }
    }

    public void onResponse(Response response, boolean isRemoteConfigOutdated) throws IOException {
        JSONObject serviceDocument, services, http, serviceDiscovery, conversion, conversionFields;
        JSONArray conversionFieldsArray = new JSONArray();
        long currentUnixTimeStamp = Calendar.getInstance().getTimeInMillis() / 1000;
        boolean shouldUpdateLocalConfig;
        String digest = null;
        Storage storage = configuration.getStorage();
        String json = response.body() != null ? response.body().string() : null;
        try {
            serviceDocument = new JSONObject(json);
            try {
                String remoteDigest = Offer18Util.getMD5(json);
                String localDigest = this.configuration.get(Constant.DIGEST);
                shouldUpdateLocalConfig = !remoteDigest.equals(localDigest);
                if (shouldUpdateLocalConfig) {
                    digest = remoteDigest;
                }
            } catch (Exception exception) {
                shouldUpdateLocalConfig = true;
            }
            if (shouldUpdateLocalConfig) {
                if (!Objects.isNull(digest)) {
                    this.configuration.set(Constant.DIGEST, digest);
                }
                http = serviceDocument.getJSONObject(Constant.HTTP);
                serviceDiscovery = serviceDocument.getJSONObject(Constant.SERVICE_DISCOVERY);
                storage.set(Constant.HTTP_TIME_OUT, Long.toString(http.getLong(Constant.HTTP_TIME_OUT)));
                storage.set(Constant.HTTP_SSL_VERIFICATION, http.getString(Constant.HTTP_SSL_VERIFICATION));
                storage.set(Constant.SERVICE_DISCOVERY_ENABLE_LOG, serviceDiscovery.getString(Constant.SERVICE_DISCOVERY_ENABLE_LOG));
                storage.set(Constant.BETTER_STACK_API_KEY, serviceDiscovery.getString(Constant.BETTER_STACK_API_KEY));
                try {
                    long expiresIn = serviceDiscovery.getLong(Constant.EXPIRES_AT);
                    long expiresAt = currentUnixTimeStamp + expiresIn;
                    storage.set(Constant.EXPIRES_AT, Long.toString(expiresAt));
                } catch (Exception e) {
                }
                services = serviceDocument.getJSONObject(Constant.SERVICES);
                conversion = services.getJSONObject(Constant.SERVICES_CONVERSION);
                conversionFields = conversion.getJSONObject(Constant.SERVICES_FIELDS);
                Iterator<String> params = conversionFields.keys();
                while (params.hasNext()) {
                    String param = params.next();
                    JSONObject fieldValidation = conversionFields.getJSONObject(param);
                    String formName = fieldValidation.getString(Constant.CONVERSION_FIELDS_PREFIX_FORM_NAME);
                    boolean required = fieldValidation.getBoolean(Constant.CONVERSION_FIELDS_PREFIX_REQUIRED);
                    String dataType = fieldValidation.getString(Constant.CONVERSION_FIELDS_PREFIX_DATA_TYPE);
                    storage.set(Constant.CONVERSION_FIELDS_PREFIX + "." + param + "." + Constant.CONVERSION_FIELDS_PREFIX_FORM_NAME, formName);
                    storage.set(Constant.CONVERSION_FIELDS_PREFIX + "." + param + "." + Constant.CONVERSION_FIELDS_PREFIX_REQUIRED, Boolean.toString(required));
                    storage.set(Constant.CONVERSION_FIELDS_PREFIX + "." + param + "." + Constant.CONVERSION_FIELDS_PREFIX_DATA_TYPE, dataType);
                    conversionFieldsArray.put(param);
                }
                storage.set(Constant.CONVERSION_PARAMS, conversionFieldsArray.toString());
            }
            if (isRemoteConfigOutdated) {
                try {
                    long expiresIn = serviceDocument.getJSONObject(Constant.SERVICE_DISCOVERY).getLong(Constant.EXPIRES_AT);
                    long expiresAt = currentUnixTimeStamp + expiresIn;
                    storage.set(Constant.EXPIRES_AT, Long.toString(expiresAt));
                } catch (Exception e) {
                }
            }
        } catch (JSONException e) {
        }
        remoteConfigDownloadSignal.countDown();
    }
}
