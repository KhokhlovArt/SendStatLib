package com.mks.sendstatlib.StatParams;

public class ExternalStatParams {
    private String publisherId;
    private String externalGaid;
    private String appid;
    private String sdkversion;
    private String packageNames;
    private String appFCMToken;

    public ExternalStatParams setPublisherId(String publisherId) {
        this.publisherId = publisherId;
        return this;
    }

    public ExternalStatParams setExternalGaid(String externalGaid) {
        this.externalGaid = externalGaid;
        return this;
    }

    public ExternalStatParams setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public ExternalStatParams setSdkversion(String sdkversion) {
        this.sdkversion = sdkversion;
        return this;
    }

    public ExternalStatParams setPackageNames(String packageNames) {
        this.packageNames = packageNames;
        return this;
    }

    public ExternalStatParams setAppFCMToken(String appFCMToken) {
        this.appFCMToken = appFCMToken;
        return this;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getExternalGaid() {
        return externalGaid;
    }

    public String getAppid() {
        return appid;
    }

    public String getSdkversion() {
        return sdkversion;
    }

    public String getPackageNames() {
        return packageNames;
    }

    public String getAppFCMToken() {
        return appFCMToken;
    }
}
