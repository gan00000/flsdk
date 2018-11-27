package com.gama.sdk.social.callback;

import java.util.List;

public interface InviteFriendsCallback {
    void failure();
    void success(String requestId, List<String> requestRecipients);
}
