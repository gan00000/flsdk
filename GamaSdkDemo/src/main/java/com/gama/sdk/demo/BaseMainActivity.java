package com.gama.sdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.core.base.utils.PL;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.SLog;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.out.GamaFactory;
import com.gama.sdk.out.IGama;

public class BaseMainActivity extends Activity {

    protected Button loginButton, othersPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_cs;
    protected IGama iGama;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        othersPayButton = findViewById(R.id.demo_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        demo_cs = findViewById(R.id.demo_cs);

        iGama = GamaFactory.create();

        //在游戏Activity的onCreate生命周期中调用
        iGama.onCreate(this);

        demo_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseMainActivity.this)
                        .setItems(new String[]{"繁中", "日语", "韩语"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SGameLanguage language = null;
                                switch (which) {
                                    case 0:
                                        language = SGameLanguage.zh_TW;
                                        break;
                                    case 1:
                                        language = SGameLanguage.ja_JP;
                                        break;
                                    case 2:
                                        language = SGameLanguage.ko_KR;
                                        break;
                                }
                                iGama.setGameLanguage(BaseMainActivity.this, language);
                            }
                        })
                        .setTitle("选择语言");
                builder.create().show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //登陆接口 ILoginCallBack为登录成功后的回调
                iGama.login(BaseMainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        if (sLoginResponse != null) {
                            String uid = sLoginResponse.getUserId();
                            userId = uid;
                            String accessToken = sLoginResponse.getAccessToken();
                            String timestamp = sLoginResponse.getTimestamp();
                            Log.i("gamaLogin", "uid:" + uid);
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getNickName());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getBirthday());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getAccessToken());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getGender());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getThirdId());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getLoginType());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getTimestamp());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getIconUri());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getThirdToken());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.getGmbPlayerIp());
                            Log.i("gamaLogin", "sLoginResponse: " + sLoginResponse.isLinked());

                            String msg = "gamaUid : " + uid + "\n"
                                    + "thirdId : " + sLoginResponse.getThirdId() + "\n"
                                    + "loginType : " + sLoginResponse.getLoginType() + "\n"
                                    + "accessToken : " + sLoginResponse.getAccessToken() + "\n"
                                    + "iconUri : " + sLoginResponse.getIconUri() + "\n"
                                    + "ip : " + sLoginResponse.getGmbPlayerIp() + "\n"
                                    + "code : " + sLoginResponse.getCode() + "\n"
                                    + "timeStamp : " + sLoginResponse.getTimestamp() + "\n"
                                    + "isLinked : " + sLoginResponse.isLinked() + "\n";
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= 21) {
                                builder = new AlertDialog.Builder(BaseMainActivity.this, android.R.style.Theme_Material_Dialog);
                            } else if (Build.VERSION.SDK_INT >= 14) {
                                builder = new AlertDialog.Builder(BaseMainActivity.this, android.R.style.Theme_Holo_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(BaseMainActivity.this);
                            }
                            AlertDialog dialog = builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false)
                                    .setMessage(msg)
                                    .create();
                            dialog.show();

                            /**
                             * 同步角色信息(以下均为测试信息)
                             */
                            String roleId = "20001000402"; //角色id
                            String roleName = "貪婪聖殿"; //角色名
                            String roleLevel = "106"; //角色等级
                            String vipLevel = "5"; //角色vip等级
                            String serverCode = "1000"; //角色伺服器id
                            String serverName = "S1"; //角色伺服器名称
                            iGama.registerRoleInfo(BaseMainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
                        } else {
                            PL.i("从登录界面返回");
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= 21) {
                                builder = new AlertDialog.Builder(BaseMainActivity.this, android.R.style.Theme_Material_Dialog);
                            } else if (Build.VERSION.SDK_INT >= 14) {
                                builder = new AlertDialog.Builder(BaseMainActivity.this, android.R.style.Theme_Holo_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(BaseMainActivity.this);
                            }
                            AlertDialog dialog = builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BaseMainActivity.this.finish();
                                }
                            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loginButton.performClick();
                                }
                            }).setCancelable(false)
                                    .setMessage("是否退出遊戲")
                                    .create();
//                            dialog.show();
                        }
                    }
                });

            }
        });

        demo_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 打开客服页面
                 */
                iGama.openCs(BaseMainActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        iGama.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        iGama.onActivityResult(this, requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        iGama.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        iGama.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        iGama.onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PL.i("activity onRequestPermissionsResult");
        iGama.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        iGama.onWindowFocusChanged(this, hasFocus);
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(GamaUtil.getUid(this));
    }

}
