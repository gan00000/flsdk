package com.mw.sdk.constant;

public enum SdkInnerVersion {

    V1("v1"),
    V2("v2"),
    V3("v3"),
    V4("v4"),
    V5("v5"),
    QOOAPP("qooapp"),
    HUAWEI("huawei");

    private String sdkVeriosnName;
    SdkInnerVersion(String sdkVeriosnName) {
        this.sdkVeriosnName = sdkVeriosnName;
    }

    public String getSdkVeriosnName() {
        return sdkVeriosnName;
    }
}
