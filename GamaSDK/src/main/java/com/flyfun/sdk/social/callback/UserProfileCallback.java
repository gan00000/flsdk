package com.flyfun.sdk.social.callback;

import com.flyfun.sdk.social.bean.UserInfo;

public interface UserProfileCallback {
    public void onCancel();
    public void onError(String message);
    public void onSuccess(UserInfo user);
}
