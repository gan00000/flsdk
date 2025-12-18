package com.mw.sdk.bean.res;

import com.core.base.bean.BaseResponseModel;
import com.core.base.utils.SStringUtil;

import java.util.List;

/**
 * 请求创单接口返回的数据
 */
public class EventRes extends BaseResponseModel {

    private List<EventData> data;

    public List<EventData> getData() {
        return data;
    }

    public void setData(List<EventData> data) {
        this.data = data;
    }

    public static class EventData{
        String eventName;
        List<EventPlatData> data;

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public List<EventPlatData> getData() {
            return data;
        }

        public void setData(List<EventPlatData> data) {
            this.data = data;
        }
    }

    public static class EventPlatData{
        String platform;
        String currency = "USD";
        double value;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getCurrency() {
            if (SStringUtil.isEmpty(currency)){
                return "USD";
            }
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
}
