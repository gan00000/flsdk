package com.thirdlib.irCafebazaar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.farsitel.bazaar.BazaarClientProxy;
import com.farsitel.bazaar.BazaarResponse;
import com.farsitel.bazaar.core.BazaarSignIn;
import com.farsitel.bazaar.core.BazaarSignInClient;
import com.farsitel.bazaar.core.callback.BazaarSignInCallback;
import com.farsitel.bazaar.core.model.BazaarSignInAccount;
import com.farsitel.bazaar.core.model.BazaarSignInOptions;
import com.farsitel.bazaar.core.model.SignInOption;
import com.thirdlib.ThirdModuleUtil;

public class CafebazaarHelper {

    public static final int REQ_CODE_BazaarSignIn = 230;

    private static SFCallBack<String> mCallBack;
    public static void login(Activity activity, SFCallBack<String> sfCallBack){

        mCallBack = sfCallBack;

        if (!ThirdModuleUtil.existBazaarModule()){
            return;
        }

        if (!BazaarClientProxy.INSTANCE.isBazaarInstalledOnDevice(activity)){
            ToastUtils.toast(activity,"Please install the Bazaar app first.");
            if (mCallBack != null){
                mCallBack.fail("","");
            }
            return;
        }

        BazaarSignIn.getLastSignedInAccount(activity, null, new BazaarSignInCallback() {
            @Override
            public void onAccountReceived(BazaarResponse<BazaarSignInAccount> response) {
                PL.d("Bazaar onAccountReceived");
                if (response != null && response.getData() != null){
                    PL.d("Bazaar getLastSignedInAccount exist");
                    BazaarSignInAccount bazaarSignInAccount = response.getData();
                    String accountId = bazaarSignInAccount.getAccountId();
                    if (SStringUtil.isNotEmpty(accountId)) {
                        if (mCallBack != null){
                            mCallBack.success(accountId,accountId);
                        }
                        return;
                    }
                }
                PL.d("Bazaar getLastSignedInAccount null");
                startSignIn(activity);
            }
        });

//        startSignIn(activity);

    }

    private static void startSignIn(Activity activity) {
        BazaarSignInOptions signInOption = new BazaarSignInOptions.Builder(SignInOption.DEFAULT_SIGN_IN).build();
        BazaarSignInClient client = BazaarSignIn.getClient(activity, signInOption);

        Intent intent = client.getSignInIntent();
        activity.startActivityForResult(intent, REQ_CODE_BazaarSignIn);
    }

    public static void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data){

        if (!ThirdModuleUtil.existBazaarModule()){
            return;
        }

        if (requestCode == REQ_CODE_BazaarSignIn){
            BazaarSignInAccount bazaarSignInAccount = BazaarSignIn.getSignedInAccountFromIntent(data);
            if (bazaarSignInAccount != null){
                String accountId = bazaarSignInAccount.getAccountId();
                if (mCallBack != null){
                    mCallBack.success(accountId,accountId);
                }
                return;
            }
            if (mCallBack != null){
                mCallBack.fail("","");
            }
        }

    }

    public static void goToReview(Activity activity){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(Uri.parse("bazaar://details?id=" + activity.getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        activity.startActivity(intent);

    }
}
