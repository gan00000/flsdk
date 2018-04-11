package com.gama.sdk.plat.data.bean.response;

import com.core.base.bean.BaseResponseModel;

/**
 * Created by gan on 2017/8/23.
 */

public class UserHasGetGiftModel extends BaseResponseModel {

    private String rewardName;
    private String serial;
    private boolean isReceive;

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }
}
