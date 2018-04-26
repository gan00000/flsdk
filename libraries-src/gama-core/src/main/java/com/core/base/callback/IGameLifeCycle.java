package com.core.base.callback;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by gan on 2017/2/13.
 */

public interface IGameLifeCycle {

    void onCreate(Activity activity);

    void onResume(Activity activity);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    void onPause(Activity activity);
    
    void onStop(Activity activity);

    void onDestroy(Activity activity);

}
