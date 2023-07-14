package com.thirdlib.twitter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

public class TwitterLogin {

    private static final String TAG = TwitterLogin.class.getSimpleName();
    static final String ERROR_MSG_NO_ACTIVITY = "TwitterLoginButton requires an activity."
            + " Override getActivity to provide the activity for this button.";

    private Activity mActivity;
//    private volatile TwitterAuthClient authClient;
    private FirebaseAuth mAuth;

    public TwitterLogin() {

    }

    /**
     * 开始登陆
     */
    public void startLogin(Activity activity, final TwitterLoginCallBack callBack) {
        if(mActivity == null || mActivity.isFinishing()) {
            Log.e(TAG, "activity in illegal state");
            return;
        }
        this.mActivity = activity;
        // Initialize Firebase Auth
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        mAuth.startActivityForSignInWithProvider(/* activity= */ mActivity, provider.build())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                // The OAuth secret can be retrieved by calling:
                                // ((OAuthCredential)authResult.getCredential()).getSecret().
                            }
                        })
                .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                        });

        //twitter登入回调
       /* Callback<TwitterSession> callback = new Callback<TwitterSession>() {
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
        getTwitterAuthClient().authorize(mActivity, callback);*/
    }

  /*  TwitterAuthClient getTwitterAuthClient() {
        if (authClient == null) {
            synchronized (TwitterLoginButton.class) {
                if (authClient == null) {
                    authClient = new TwitterAuthClient();
                }
            }
        }
        return authClient;
    }
*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == getTwitterAuthClient().getRequestCode()) {
            getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }*/
    }

    public interface TwitterLoginCallBack{
        void success(String id, String mFullName, String mEmail, String idTokenString);
        void failure(String msg);
    }
}
