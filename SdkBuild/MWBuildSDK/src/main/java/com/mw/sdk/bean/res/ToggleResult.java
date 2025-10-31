package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;

import java.util.List;

/**
 * Created by ganyuanrong on 2017/2/23.
 */

public class ToggleResult extends BaseResponseModel {

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

        private boolean isShowMarketButton;//是否开启活动商城

        private String productName;
        private List<PayChannelData> channelList;

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

        public boolean isShowMarketButton() {
            return isShowMarketButton;
        }

        public void setShowMarketButton(boolean showMarketButton) {
            isShowMarketButton = showMarketButton;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public List<PayChannelData> getChannelList() {
            return channelList;
        }

        public void setChannelList(List<PayChannelData> channelList) {
            this.channelList = channelList;
        }
    }
}
