package com.flyfun.sdk.social.callback;

import com.flyfun.thirdlib.facebook.FriendProfile;

import org.json.JSONObject;

import java.util.List;

public interface FetchFriendsCallback {
    void onError();
    void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count);
}
