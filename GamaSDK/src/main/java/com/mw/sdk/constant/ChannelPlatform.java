package com.mw.sdk.constant;

public enum ChannelPlatform {

    QOOAPP("qooapp"),
    LUNQI("lunqi"),
    HUAWEI("huawei"),
    GOOGLE("google"),
    ONESTORE("onestore"),
    MEOW("meow");

    private String channel_platform;
    ChannelPlatform(String channelPlatform) {
        this.channel_platform = channelPlatform;
    }

    public String getChannel_platform() {
        return channel_platform;
    }

    public void setChannel_platform(String channel_platform) {
        this.channel_platform = channel_platform;
    }
}
