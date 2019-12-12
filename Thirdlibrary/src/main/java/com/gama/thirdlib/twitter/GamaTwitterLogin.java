package com.gama.thirdlib.twitter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class GamaTwitterLogin {

    private static final String TAG = GamaTwitterLogin.class.getSimpleName();
    static final String ERROR_MSG_NO_ACTIVITY = "TwitterLoginButton requires an activity."
            + " Override getActivity to provide the activity for this button.";

    private Activity mActivity;
    private volatile TwitterAuthClient authClient;

    public GamaTwitterLogin(Activity activity) {
        this.mActivity = activity;
        try {
            Twitter.initialize(activity);
            TwitterCore.getInstance();
        } catch (IllegalStateException ex) {
            Log.e(TAG, "twitter init failed");
        }
    }

    /**
     * 开始登陆
     */
    public void startLogin(final TwitterLoginCallBack callBack) {
        if(mActivity == null || mActivity.isFinishing()) {
            Log.e(TAG, "activity in illegal state");
            return;
        }
        //twitter登入回调
        Callback<TwitterSession> callback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.i(TAG, "success : " + result.data.toString());
                if(callBack != null) {
                    callBack.success(result.data.getUserId() + "", result.data.getUserName(), "", result.data.getAuthToken().token);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Log.i(TAG, "failure : " + exception.getMessage());
                if(callBack != null) {
                    callBack.failure(exception.getMessage());
                }
            }
        };
        getTwitterAuthClient().authorize(mActivity, callback);
    }

    TwitterAuthClient getTwitterAuthClient() {
        if (authClient == null) {
            synchronized (TwitterLoginButton.class) {
                if (authClient == null) {
                    authClient = new TwitterAuthClient();
                }
            }
        }
        return authClient;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface TwitterLoginCallBack{
        void success(String id, String mFullName, String mEmail, String idTokenString);
        void failure(String msg);
    }
}
