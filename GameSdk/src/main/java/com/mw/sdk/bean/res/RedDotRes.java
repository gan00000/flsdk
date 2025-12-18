package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;

public class RedDotRes extends BaseResponseModel {

    private  RedDotData data;

    public RedDotData getData() {
        return data;
    }

    public void setData(RedDotData data) {
        this.data = data;
    }

    public static class RedDotData{

        private boolean cs;

        public boolean isCs() {
            return cs;
        }

        public void setCs(boolean cs) {
            this.cs = cs;
        }
    }
}
