package com.game.sdk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.core.base.utils.SPUtil;
import com.mw.base.utils.SdkUtil;

public class DoPermissionReq extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = "Unity";
    private static final int WRITE_READ_PERMISSION = 0x015; //请求授权的requestCode
    private boolean doubleCheck = false; //二次确认的状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startMainActivity();
            return;
        }
        //是否首次启动
        boolean launched = SPUtil.getSimpleBoolean(this, SdkUtil.SDK_SP_FILE, "launched");
        //未授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(!launched || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showAlert();
            } else {
                requestPermission();
            }
        } else { //已授权
            startMainActivity();
        }
        if(!launched) {
            SPUtil.saveSimpleInfo(this, SdkUtil.SDK_SP_FILE, "launched", true);
        }
    }

    /**
     * 首次提示并进行授权
     */
    private void showAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 21) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog);
        } else if (Build.VERSION.SDK_INT >= 14) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        AlertDialog dialog = builder
                .setTitle("注意")
                .setMessage("為確保您的遊戲檔案存取正常，請授予以下權限，系統不會執行遊戲檔案存取以外動作")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    /**
     * 进行二次提示。
     */
    private void showAlert2() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 21) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog);
        } else if (Build.VERSION.SDK_INT >= 14) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Dialog);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        AlertDialog dialog = builder
                .setTitle("注意")
                .setMessage("您已拒絕授予權限，可能會影響遊戲體驗")
                .setPositiveButton("重新授權", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doubleCheck = true;
                            requestPermission();
                    }
                })
                .setNegativeButton("繼續進入遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();

    }

    private void requestPermission() {
        String[] pers = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
        ActivityCompat.requestPermissions(this, pers, WRITE_READ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permissions", "Permissions result :" + grantResults.length);
        if (requestCode == WRITE_READ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMainActivity();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) { //没有选择不再提示
                //二次确认提示
                if(!doubleCheck) { //没有进行过二次提示
                    showAlert2();
                } else { //已经进行二次提示
                    startMainActivity();
                }
            } else { //选择了不再提示
                startMainActivity();
            }
        } else {
            //正常情況下不可能走到这里
            startMainActivity();
        }

    }

    /**
     * 继续游戏
     */
    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        this.startActivity(i);
        this.finish();
    }
}
