package com.mw.sdk.login.constant;

public enum BindType {
    BIND_UNIQUE("visitor"),
    BIND_FB("fb"),
    BIND_GOOGLE("google"),
    BIND_TWITTER("twitter"),
    BIND_LINE("line");

    BindType(String type) {
    }
}