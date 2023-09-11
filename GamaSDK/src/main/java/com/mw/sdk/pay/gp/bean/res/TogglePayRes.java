package com.mw.sdk.pay.gp.bean.res;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class TogglePayRes extends BaseResponseModel {

    @Override
    public boolean isRequestSuccess() {
        return SUCCESS_CODE.equals(this.getCode());
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{

        private boolean isTogglePay;//是否切支付
        private boolean hideSelectChannel;//1000不显示选择页面

        public boolean isTogglePay() {
            return isTogglePay;
        }

        public void setTogglePay(boolean togglePay) {
            isTogglePay = togglePay;
        }

        public boolean isHideSelectChannel() {
            return hideSelectChannel;
        }

        public void setHideSelectChannel(boolean hideSelectChannel) {
            this.hideSelectChannel = hideSelectChannel;
        }
    }
}
