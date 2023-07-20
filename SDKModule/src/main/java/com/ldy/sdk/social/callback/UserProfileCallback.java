package com.ldy.sdk.social.callback;

import com.ldy.sdk.social.bean.UserInfo;

public interface UserProfileCallback {
    public void onCancel();
    public void onError(String message);
    public void onSuccess(UserInfo user);
}
