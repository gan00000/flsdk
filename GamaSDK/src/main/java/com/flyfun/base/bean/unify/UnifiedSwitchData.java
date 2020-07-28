package com.flyfun.base.bean.unify;

import java.io.Serializable;

public class UnifiedSwitchData implements Serializable {

    private UnifiedSwitchDataType fb;
    private UnifiedSwitchDataType notice;

    public UnifiedSwitchDataType getFb() {
        return fb;
    }

    public void setFb(UnifiedSwitchDataType fb) {
        this.fb = fb;
    }

    public UnifiedSwitchDataType getNotice() {
        return notice;
    }

    public void setNotice(UnifiedSwitchDataType notice) {
        this.notice = notice;
    }
}
