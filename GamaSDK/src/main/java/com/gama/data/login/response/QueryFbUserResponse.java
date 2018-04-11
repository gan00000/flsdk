package com.gama.data.login.response;

import com.core.base.bean.BaseResponseModel;

import java.util.List;

/**
 * Created by GanYuanrong on 2017/2/11.
 * "code":1000,"accessToken":"2eccffd3771eeec9303616386f6a2d27","message":"登入成功","userId":"41","timestamp":"1487844049868"}
 */

public class QueryFbUserResponse extends BaseResponseModel {

    private List<FbUser> fbUsers;

    public List<FbUser> getFbUsers() {
        return fbUsers;
    }

    public void setFbUsers(List<FbUser> fbUsers) {
        this.fbUsers = fbUsers;
    }
}
