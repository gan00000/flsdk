package com.mw.sdk.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.core.base.utils.FileUtil;
import com.google.gson.Gson;
import com.mw.base.bean.PhoneInfo;

public class PhoneAreaCodeDialogHelper {

    //区域json
    private String areaJson;
    //区域bean列表
    private PhoneInfo[] areaBeanList;
    //已选中的区域bean
    private PhoneInfo selectedBean;

    private Activity activity;
    AlertDialog alertDialog;
    private AreaCodeSelectCallback areaCodeSelectCallback;

    public PhoneAreaCodeDialogHelper(Activity activity) {
        this.activity = activity;
    }

    public void showPhoneAreaCodeDialog(AreaCodeSelectCallback areaCodeSelectCallback) {

        this.areaCodeSelectCallback = areaCodeSelectCallback;

        if(areaBeanList == null || areaBeanList.length < 1) {
            /*GamaAreaInfoRequestTask task = new GamaAreaInfoRequestTask(getContext());
            task.setLoadDialog(DialogUtil.createLoadingDialog(getActivity(), "Loading..."));
            task.setReqCallBack(new ISReqCallBack() {
                @Override
                public void success(Object o, String rawResult) {
                    try {
                        if (TextUtils.isEmpty(rawResult)) {
                            areaJson = FileUtil.readAssetsTxtFile(getContext(), "mwsdk/areaInfo");
                        } else {
                            JSONArray areaArray = new JSONArray(rawResult);
                            areaJson = areaArray.toString();
                        }

                        Gson gson = new Gson();
                        areaBeanList = gson.fromJson(areaJson, GamaAreaInfoBean[].class);
                        showAreaDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void timeout(String code) {

                    try {

                        areaJson = FileUtil.readAssetsTxtFile(getContext(), "mwsdk/areaInfo");
                        Gson gson = new Gson();
                        areaBeanList = gson.fromJson(areaJson, GamaAreaInfoBean[].class);
                        showAreaDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void noData() {

                }

                @Override
                public void cancel() {

                }
            });
            task.excute();*/

            areaJson = FileUtil.readAssetsTxtFile(activity.getApplicationContext(), "mwsdk/areaInfo");
            Gson gson = new Gson();
            areaBeanList = gson.fromJson(areaJson, PhoneInfo[].class);
            showAreaDialog();
        } else {
            showAreaDialog();
        }
    }

    private void showAreaDialog() {

        if (alertDialog != null) {
            alertDialog.show();
            return;
        }

        final String[] areaList = new String[areaBeanList.length];
        for(int i = 0; i < areaBeanList.length; i++) {
            areaList[i] = areaBeanList[i].getText();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setItems(areaList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBean = areaBeanList[which];
                        if (areaCodeSelectCallback != null){
                            areaCodeSelectCallback.select(selectedBean);
                        }
                    }
                });
        alertDialog = builder.create();

        alertDialog.show();
    }

    public interface AreaCodeSelectCallback
    {
        void select(PhoneInfo phoneInfo);
    }

}
