package com.gama.base.constant;

public enum GsCommonSwitchType {
    ANNOUCE("annouce");

    private String string;
    GsCommonSwitchType(String s) {
        this.string = s;
    }

    public String getString() {
        return this.string;
    }

}
