package com.thirdlib.vk;

import android.app.Activity;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;

import ru.rustore.sdk.review.RuStoreReviewManager;
import ru.rustore.sdk.review.RuStoreReviewManagerFactory;

public class RustoreManager {

    public static void launchReviewFlow(Activity activity, SFCallBack sfCallBack){

        try {
            Class<?> clazz = Class.forName("ru.rustore.sdk.review.RuStoreReviewManager");
            if (clazz == null){
                return;
            }
        } catch (ClassNotFoundException e) {
            PL.w("rustore pay module not exist.");
            return;
        }

        RuStoreReviewManager manager = RuStoreReviewManagerFactory.INSTANCE.create(activity);
        manager.requestReviewFlow()
                .addOnSuccessListener(reviewInfo -> {
                    // Save reviewInfo
                    manager.launchReviewFlow(reviewInfo)
                            .addOnSuccessListener(unit -> {
                                // Review flow has finished, continue your app flow.
                                if (sfCallBack != null){
                                    sfCallBack.success("","");
                                }

                            })
                            .addOnFailureListener(throwable -> {
                                // Review flow has finished, continue your app flow.
                                PL.w("RuStoreReviewManager launchReviewFlow error=>" + throwable.getMessage());
                                throwable.printStackTrace();
                                if (sfCallBack != null){
                                    sfCallBack.fail("","");
                                }
                            });

                })
                .addOnFailureListener(throwable -> {
                    // Handle
                    PL.w("RuStoreReviewManager requestReviewFlow error=>" + throwable.getMessage());
                    throwable.printStackTrace();
                    if (sfCallBack != null){
                        sfCallBack.fail("","");
                    }
                });

    }
}
