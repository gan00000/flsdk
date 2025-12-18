package com.mw.sdk.constant;

public enum SdkInnerVersion {

    V1("v1"),
    V2("v2"),
    V3("v3"),
    V4("v4"),
    V5("v5"),
    V6("v6"),
    V7("v7"),
    V8("v8"),
    QOOAPP("qooapp"),
    HUAWEI("huawei"),
    VN("vn"),
    KR("kr");

    private String sdkVeriosnName;
    SdkInnerVersion(String sdkVeriosnName) {
        this.sdkVeriosnName = sdkVeriosnName;
    }

    public String getSdkVeriosnName() {
        return sdkVeriosnName;
    }
}
