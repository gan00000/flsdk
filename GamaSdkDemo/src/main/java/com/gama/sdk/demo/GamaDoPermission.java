package com.gama.sdk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.core.base.utils.SPUtil;
import com.gama.base.utils.GamaUtil;

/**
 * 用于处理Android 6.0以上授权的工具类
 */
public class GamaDoPermission {

    private static final int GAMA_REQUEST_PERMISSION = 0x015;
    private boolean doubleCheck = false; //二次确认的状态
    private static GamaDoPermission doPermission;

    private GamaDoPermission() {}

    public static GamaDoPermission getInstance() {
        if(doPermission == null) {
            return new GamaDoPermission();
        }
        return doPermission;
    }

    public void onRequestPermissionsResult(Activity activity, final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Permissions", "Permissions result :" + grantResults.length);
        if (requestCode == GAMA_REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: 2018/6/7 回调
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) { //没有选择不再提示
                //二次确认提示
                if(!doubleCheck) { //没有进行过二次提示
                    showAlert2(activity);
                } else { //已经进行二次提示
                    // TODO: 2018/6/7 回调
                }
            } else { //选择了不再提示
                // TODO: 2018/6/7 回调
            }
        } else {
            //正常情況下不可能走到这里
            // TODO: 2018/6/7 回调
        }
    }

    public void doRequestPermission(Activity activity, String[] permissions, RequestPermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // TODO: 2018/6/7 回调
            return;
        }
        //是否首次启动
        boolean launched = SPUtil.getSimpleBoolean(activity, GamaUtil.GAMA_SP_FILE, "launched");
        //未授权
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(!launched || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showAlert(activity);
            } else {
                requestPermission(activity);
            }
        } else { //已授权
            // TODO: 2018/6/7 回调
        }
        if(!launched) {
            SPUtil.saveSimpleInfo(activity, GamaUtil.GAMA_SP_FILE, "launched", true);
        }
    }

    /**
     * 首次提示并进行授权
     */
    private void showAlert(final Activity activity) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 21) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog);
        } else if (Build.VERSION.SDK_INT >= 14) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Holo_Dialog);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        AlertDialog dialog = builder
                .setTitle("注意")
                .setMessage("為確保您的遊戲檔案存取正常，請授予以下權限，系統不會執行遊戲檔案存取以外動作")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission(activity);
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();
    }

    /**
     * 进行二次提示。
     */
    private void showAlert2(final Activity activity) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= 21) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog);
        } else if (Build.VERSION.SDK_INT >= 14) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Holo_Dialog);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        AlertDialog dialog = builder
                .setTitle("注意")
                .setMessage("您已拒絕授予權限，可能會影響遊戲體驗")
                .setPositiveButton("重新授權", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doubleCheck = true;
                        requestPermission(activity);
                    }
                })
                .setNegativeButton("繼續進入遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2018/6/7 回调
                    }
                })
                .setCancelable(false)
                .create();
        dialog.show();

    }

    private void requestPermission(Activity activity) {
        String[] pers = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(activity, pers, GAMA_REQUEST_PERMISSION);
    }

    public interface RequestPermissionCallback {
        /**
         * @param grantPermission 已获得的权限
         * @param deniedPermission 未获得的权限
         */
        void onResult(String[] grantPermission, String[] deniedPermission);
    }
}

