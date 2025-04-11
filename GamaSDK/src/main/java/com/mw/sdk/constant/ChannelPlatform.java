package com.mw.sdk.constant;

public enum ChannelPlatform {

    QOOAPP("qooapp"),//tw
    LUNQI("lunqi"),//tw
    HUAWEI("huawei"),//tw
    GOOGLE("google"),
    ONESTORE("onestore"),//kr
    SAMSUNG("samsung"),//kr
    VK("vk"),//俄罗斯vk
    NOWGG("nowgg"),
    Xiaomi("xiaomi"),
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
