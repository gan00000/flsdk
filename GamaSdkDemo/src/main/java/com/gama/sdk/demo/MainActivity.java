package com.gama.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.crashlytics.android.Crashlytics;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.SLog;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
import com.gama.pay.gp.util.IabHelper;
import com.gama.pay.gp.util.IabResult;
import com.gama.pay.gp.util.Inventory;
import com.gama.pay.gp.util.Purchase;
import com.gama.sdk.ads.GamaAdsConstant;
import com.gama.sdk.callback.IPayListener;
import com.gama.sdk.login.widget.v2.age.IGamaAgePresenter;
import com.gama.sdk.login.widget.v2.age.callback.GamaAgeCallback;
import com.gama.sdk.login.widget.v2.age.impl.GamaAgeImpl;
import com.gama.sdk.out.GamaFactory;
import com.gama.sdk.out.GamaOpenWebType;
import com.gama.sdk.out.GamaThirdPartyType;
import com.gama.sdk.out.IGama;
import com.gama.sdk.out.ISdkCallBack;
import com.gama.sdk.social.bean.UserInfo;
import com.gama.sdk.social.callback.FetchFriendsCallback;
import com.gama.sdk.social.callback.InviteFriendsCallback;
import com.gama.sdk.social.callback.UserProfileCallback;
import com.gama.thirdlib.facebook.FriendProfile;
import com.gama.thirdlib.twitter.GamaTwitterLogin;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, othersPayButton, googlePayBtn, shareButton, showPlatform, crashlytics,
            PurchasesHistory, getFriend, invite, checkShare, getInfo, getFriendNext, getFriendPrevious,
            service, announcement, age, demo_language, track;
    IabHelper mHelper;
    private IGama iGama;
    private String nextUrl, previousUrl;
    private GamaTwitterLogin gamaTwitterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = (Button) findViewById(R.id.demo_login);
        demo_language = (Button) findViewById(R.id.demo_language);
        othersPayButton = (Button) findViewById(R.id.demo_pay);
        googlePayBtn = (Button) findViewById(R.id.demo_pay_google);
        shareButton = (Button) findViewById(R.id.demo_share);
        showPlatform = (Button) findViewById(R.id.showPlatform);
        crashlytics = (Button) findViewById(R.id.Crashlytics);
        PurchasesHistory = (Button) findViewById(R.id.PurchasesHistory);

        getInfo = (Button) findViewById(R.id.getInfo);
        getFriend = (Button) findViewById(R.id.getFriend);
        getFriendNext = (Button) findViewById(R.id.getFriendNext);
        getFriendPrevious = (Button) findViewById(R.id.getFriendPrevious);
        invite = (Button) findViewById(R.id.invite);
        checkShare = (Button) findViewById(R.id.checkShare);
        service = (Button) findViewById(R.id.service);
        announcement = (Button) findViewById(R.id.announcement);
        age = (Button) findViewById(R.id.age);
        track = (Button) findViewById(R.id.track);

        iGama = GamaFactory.create();

        //设置语言
//        iGama.setGameLanguage(this, SGameLanguage.zh_TW);

        //初始化sdk
        iGama.initSDK(this, SGameLanguage.zh_TW);

        //在游戏Activity的onCreate生命周期中调用
        iGama.onCreate(this);

        demo_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
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
                                    iGama.setGameLanguage(MainActivity.this, language);
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
                iGama.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        if (sLoginResponse != null) {
                            String uid = sLoginResponse.getUserId();
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
                            /**
                             * 同步角色信息(以下均为测试信息)
                             */
                            String roleId = "123"; //角色id
                            String roleName = "角色名"; //角色名
                            String roleLevel = "10"; //角色等级
                            String vipLevel = "5"; //角色vip等级
                            String serverCode = "666"; //角色伺服器id
                            String serverName = "S1"; //角色伺服器名称
                            iGama.registerRoleInfo(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
                        } else {
                            PL.i("从登录界面返回");
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= 21) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog);
                            } else if (Build.VERSION.SDK_INT >= 14) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(MainActivity.this);
                            }
                            AlertDialog dialog = builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loginButton.performClick();
                                }
                            }).setCancelable(false)
                                    .setMessage("是否退出遊戲")
                                    .create();
                            dialog.show();
                        }
                    }
                });

            }
        });

        othersPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /*
                充值接口
                SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
                cpOrderId cp订单号，请保持每次的值都是不会重复的
                productId 充值的商品id
                customize 自定义透传字段（从服务端回调到cp）
                */
                iGama.pay(MainActivity.this, SPayType.OTHERS, "" + System.currentTimeMillis(), "payone", "customize", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("OtherPay支付结束");
                    }
                });


            }
        });

        googlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                充值接口
                SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
                cpOrderId cp订单号，请保持每次的值都是不会重复的
                productId 充值的商品id
                customize 自定义透传字段（从服务端回调到cp）
                */
                iGama.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(), getResources().getString(R.string.test_sku), "customize", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("GooglePay结束");
                        int status = 0;
                        if (bundle != null) {
                            status = bundle.getInt("status");

                            for (String next : bundle.keySet()) {
                                PL.i(next + " : " + bundle.get(next));
                            }
                        }
                    }
                });

            }
        });


        // String shareUrl = "http://ads.starb168.com/ads_scanner?gameCode=mthxtw&adsPlatForm=star_event&advertiser=share";
        // https://dodi.gamamobi.com/share/index.html?gameCode=dodi&userId=1&roleId=123&roleName=erge&serverCode=1000&serverName=testServer&package=com.xxx.xxx&
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setItems(new String[]{"facebook", "line", "whatsapp", "facebook-messenger", "Twitter"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GamaThirdPartyType type = null;
                                if (i == 0) {
                                    type = GamaThirdPartyType.FACEBOOK;
                                } else if (i == 1) {
                                    type = GamaThirdPartyType.LINE;
                                } else if (i == 2) {
                                    type = GamaThirdPartyType.WHATSAPP;
                                } else if (i == 3) {
                                    type = GamaThirdPartyType.FACEBOOK_MESSENGER;
                                } else if (i == 4) {
                                    type = GamaThirdPartyType.TWITTER;
                                }
                                //下面的参数请按照实际传值
                                final String message = "分享内容啊";
                                final String shareUrl = "http://www.gamamobi.com/";
                                final String picPath = Environment.getExternalStorageDirectory() + File.separator + "1.jpg";
//                                final Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator + "1.jpg"));

                                final GamaThirdPartyType finalType = type;
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this)
                                        .setItems(new String[]{"分享图片", "分享文字/链接", "同时分享图片、文字/链接"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int index) {
                                                if (index == 0) {
                                                    iGama.gamaShare(MainActivity.this, finalType, message, "", picPath, new ISdkCallBack() {
                                                        @Override
                                                        public void success() {
                                                            Log.i("facebook", "success");
                                                        }

                                                        @Override
                                                        public void failure() {
                                                            Log.i("facebook", "failure");
                                                        }
                                                    });
                                                } else if (index == 1) {
                                                    if (finalType == GamaThirdPartyType.FACEBOOK_MESSENGER) {
                                                        ToastUtils.toast(MainActivity.this, "facebook-messenger只支持图片分享");
                                                        return;
                                                    }
                                                    iGama.gamaShare(MainActivity.this, finalType, message, shareUrl, null, new ISdkCallBack() {
                                                        @Override
                                                        public void success() {
                                                            Log.i("facebook", "success");
                                                        }

                                                        @Override
                                                        public void failure() {
                                                            Log.i("facebook", "failure");
                                                        }
                                                    });
                                                } else if(index == 2) {
                                                    if (finalType != GamaThirdPartyType.WHATSAPP &&
                                                            finalType != GamaThirdPartyType.TWITTER) {
                                                        ToastUtils.toast(MainActivity.this, "当前类型不支持同时分享图片和文字");
                                                        return;
                                                    }
                                                    iGama.gamaShare(MainActivity.this, finalType, message, shareUrl, picPath, new ISdkCallBack() {
                                                        @Override
                                                        public void success() {
                                                            Log.i("twitter", "success");
                                                        }

                                                        @Override
                                                        public void failure() {
                                                            Log.i("twitter", "failure");
                                                        }
                                                    });
                                                }
                                            }
                                        })
                                        .setTitle("选择分享内容");
                                builder2.create().show();

                            }
                        })
                        .setTitle("选择分享方式");

                builder.create().show();

                //分享回调
//                ISdkCallBack iSdkCallBack = new ISdkCallBack() {
//                    @Override
//                    public void success() {
//                        PL.i("share  success");
//                    }
//
//                    @Override
//                    public void failure() {
//                        PL.i("share  failure");
//                    }
//                };
//
//                iGama.share(MainActivity.this,iSdkCallBack,shareUrl);

//                iGama.share(MainActivity.this,iSdkCallBack,"", "", shareUrl, "");

            }
        });

        findViewById(R.id.open_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 打开一个活动页面接口
                 */
                iGama.openWebview(MainActivity.this);
            }
        });

        findViewById(R.id.open_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 打开一个指定的url
                 * String url: 打开的url
                 */
                iGama.openWebPage(MainActivity.this, GamaOpenWebType.CUSTOM_URL, "https://dodi.gamamobi.com/news/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        Log.i("gama", "关闭web页面");
                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });

        showPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.openPlatform(MainActivity.this);

                GamaTimeUtil.getBeiJingTime(MainActivity.this);
                GamaTimeUtil.getDisplayTime(MainActivity.this);
            }
        });

        mHelper = new IabHelper(MainActivity.this.getApplicationContext());
        findViewById(R.id.xiaofei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHelper.isSetupDone()) {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            if (result.isSuccess()) {
                                SLog.logD("初始化iabHelper成功，开始消费");
                                consume();
                            } else {
                                SLog.logD("初始化iabHelper失败，消费结束");
                            }
                        }
                    });
                } else {
                    SLog.logD("已经初始化iabHelper，开始消费");
                    consume();
                }
            }
        });

        crashlytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });

        PurchasesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHelper.isSetupDone()) {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            if (result.isSuccess()) {
                                SLog.logD("初始化iabHelper成功，开始查历史记录");
                                mHelper.queryPurchasesHistory();
                            } else {
                                SLog.logD("初始化iabHelper失败，查历史记录结束");
                            }
                        }
                    });
                } else {
                    SLog.logD("已经初始化iabHelper，开始查历史记录");
                    mHelper.queryPurchasesHistory();
                }
            }
        });

        final int limit = 1;
        final Bundle bundle = new Bundle();
        bundle.putString("fields", "friends.limit(" + limit + ")");

        getFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.gamaFetchFriends(MainActivity.this, GamaThirdPartyType.FACEBOOK, bundle, "", limit, new FetchFriendsCallback() {
                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count) {
                        Log.i("facebook", "graphObject: " + graphObject.toString());
                        if (friendProfiles != null) {
                            for (FriendProfile profile : friendProfiles) {
                                Log.i("facebook", "profile: " + profile.getThirdId());
                                Log.i("facebook", "profile: " + profile.getGender());
                                Log.i("facebook", "profile: " + profile.getName());
                                Log.i("facebook", "profile: " + profile.getIconUrl());
                            }
                            Log.i("facebook", "next: " + next);
                            Log.i("facebook", "previous: " + previous);
                            Log.i("facebook", "count: " + count);
                            nextUrl = next;
                            previousUrl = previous;
                        }

                    }
                });
            }
        });

        getFriendNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(nextUrl)) {
                    Toast.makeText(MainActivity.this, "没有下一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                iGama.gamaFetchFriends(MainActivity.this, GamaThirdPartyType.FACEBOOK, null, nextUrl, limit, new FetchFriendsCallback() {
                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count) {
                        Log.i("facebook", "graphObject: " + graphObject.toString());
                        if (friendProfiles != null) {
                            for (FriendProfile profile : friendProfiles) {
                                Log.i("facebook", "profile: " + profile.getThirdId());
                                Log.i("facebook", "profile: " + profile.getGender());
                                Log.i("facebook", "profile: " + profile.getName());
                                Log.i("facebook", "profile: " + profile.getIconUrl());
                            }
                            Log.i("facebook", "next: " + next);
                            Log.i("facebook", "previous: " + previous);
                            Log.i("facebook", "count: " + count);
                            nextUrl = next;
                            previousUrl = previous;
                        }

                    }
                });
            }
        });

        getFriendPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(previousUrl)) {
                    Toast.makeText(MainActivity.this, "没有上一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                iGama.gamaFetchFriends(MainActivity.this, GamaThirdPartyType.FACEBOOK, null, previousUrl, limit, new FetchFriendsCallback() {
                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count) {
                        Log.i("facebook", "graphObject: " + graphObject.toString());
                        if (friendProfiles != null) {
                            for (FriendProfile profile : friendProfiles) {
                                Log.i("facebook", "profile: " + profile.getThirdId());
                                Log.i("facebook", "profile: " + profile.getGender());
                                Log.i("facebook", "profile: " + profile.getName());
                                Log.i("facebook", "profile: " + profile.getIconUrl());
                            }
                            Log.i("facebook", "next: " + next);
                            Log.i("facebook", "previous: " + previous);
                            Log.i("facebook", "count: " + count);
                            nextUrl = next;
                            previousUrl = previous;
                        }

                    }
                });
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<FriendProfile> invitingList = new ArrayList<>();
                FriendProfile profile = new FriendProfile();
                profile.setThirdId("529074447599533");
//                invitingList.add(profile);

                iGama.gamaInviteFriends(MainActivity.this, GamaThirdPartyType.FACEBOOK, invitingList, "消息內容", "標題", new InviteFriendsCallback() {
                    @Override
                    public void failure() {
                        Log.i("facebook", "failure");
                    }

                    @Override
                    public void success(String requestId, List<String> requestRecipients) {
                        Log.i("facebook", "requestId: " + requestId);
                        if (requestRecipients != null) {
                            for (String s : requestRecipients) {
                                Log.i("facebook", "requestRecipients: " + s);
                            }
                        }

                    }
                });
            }
        });

        checkShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setItems(new String[]{"facebook", "line", "whatsapp", "facebook-messenger"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GamaThirdPartyType type = null;
                                if (i == 0) {
                                    type = GamaThirdPartyType.FACEBOOK;
                                } else if (i == 1) {
                                    type = GamaThirdPartyType.LINE;
                                } else if (i == 2) {
                                    type = GamaThirdPartyType.WHATSAPP;
                                } else if (i == 3) {
                                    type = GamaThirdPartyType.FACEBOOK_MESSENGER;
                                }
                                boolean canShare = iGama.gamaShouldShareWithType(MainActivity.this, type);
                                if (canShare) {
                                    ToastUtils.toast(MainActivity.this, "支持分享");
                                } else {
                                    ToastUtils.toast(MainActivity.this, "不支持分享");
                                }

                            }
                        })
                        .setTitle("选择分享方式");

                builder.create().show();
            }
        });

        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.gamaGetUserProfile(MainActivity.this, new UserProfileCallback() {
                    @Override
                    public void onCancel() {
                        Log.i("facebook", "onCancel");
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("facebook", "onError " + message);
                    }

                    @Override
                    public void onSuccess(UserInfo user) {
                        Log.i("facebook", "UserInfo: " + user.getAccessTokenString());
                        Log.i("facebook", "UserInfo: " + user.getBirthday());
                        Log.i("facebook", "UserInfo: " + user.getGender());
                        Log.i("facebook", "UserInfo: " + user.getName());
                        Log.i("facebook", "UserInfo: " + user.getPictureUri());
                        Log.i("facebook", "UserInfo: " + user.getUserThirdId());
                    }
                });
            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.openWebPage(MainActivity.this, GamaOpenWebType.SERVICE, "", new ISdkCallBack() {
                    @Override
                    public void success() {
                        Log.i("gama", "关闭客服页面");
                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });

        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.openWebPage(MainActivity.this, GamaOpenWebType.ANNOUNCEMENT, "", new ISdkCallBack() {
                    @Override
                    public void success() {
                        Log.i("gama", "关闭公告页面");
                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });

//        TwitterConfig config = new TwitterConfig.Builder(this)
//                .logger(new DefaultLogger(Log.DEBUG))
//                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
//                        getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
//                .debug(true)
//                .build();
//        Twitter.initialize(config);
//
//        gamaTwitterLogin = new GamaTwitterLogin(MainActivity.this);
//
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLogin()) {
                    ToastUtils.toast(MainActivity.this, "请先登入");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setItems(new String[]{"选择年龄页面", "达到上限页面", "发送年龄请求", "判断购买上限"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                IGamaAgePresenter presenter = new GamaAgeImpl();
                                ((GamaAgeImpl) presenter).setAgeCallback(new GamaAgeCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }

                                    @Override
                                    public void canBuy() {

                                    }
                                });
                                if (i == 0) {
                                    presenter.goAgeStyleThree(MainActivity.this);
                                } else if (i == 1) {
                                    presenter.goAgeStyleOne(MainActivity.this);
                                } else if (i == 2) {
                                    presenter.sendAgeRequest(MainActivity.this, null);
                                } else if (i == 3) {
                                    presenter.requestAgeLimit(MainActivity.this);
                                }
                            }
                        });
                builder.create().show();

            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = "eventName"; //事件名
                Map map = new HashMap(); //事件属性列表,如没有可传空
                Set<GamaAdsConstant.GamaEventReportChannel> set = new HashSet<>();
                set.add(GamaAdsConstant.GamaEventReportChannel.GamaEventReportFacebook);
                set.add(GamaAdsConstant.GamaEventReportChannel.GamaEventReportFirebase);
                set.add(GamaAdsConstant.GamaEventReportChannel.GamaEventReportAllChannel);
                iGama.gamaTrack(MainActivity.this, eventName, map, set);
            }
        });

    }

    private void consume() {
        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // callbackFail();
                    PL.i("query result:" + result.getMessage());
                    SLog.logD("getQueryInventoryState is null");
                } else {

                    SLog.logD("Query inventory was successful.");
                    List<Purchase> purchaseList = inventory.getAllPurchases();

                    if (null == purchaseList || purchaseList.isEmpty()) {
                        //没有未消费的商品
                        //  callbackFail();
                        SLog.logD("purchases is empty");

                    } else {
                        SLog.logD("purchases size: " + purchaseList.size());

                        if (purchaseList.size() == 1) {
                            SLog.logD("mConsumeFinishedListener. 消费一个");
                            mHelper.consumeAsync(purchaseList.get(0), new IabHelper.OnConsumeFinishedListener() {
                                @Override
                                public void onConsumeFinished(Purchase purchase, IabResult result) {
                                    if (null != purchase) {
                                        SLog.logD("Purchase: " + purchase.toString() + ", result: " + result);
                                    } else {
                                        SLog.logD("Purchase is null");
                                    }
                                    if (result.isSuccess()) {
                                        SLog.logD("Consumption successful.");
                                        if (purchase != null) {
                                            SLog.logD("sku: " + purchase.getSku() + " Consume finished success");
                                        }
                                    } else {
                                        SLog.logD("consumption is not success, yet to be consumed.");
                                    }
                                }
                            });
                        } else if (purchaseList.size() > 1) {
                            SLog.logD("mConsumeMultiFinishedListener.消费多个");
                            mHelper.consumeAsync(purchaseList, new IabHelper.OnConsumeMultiFinishedListener() {
                                @Override
                                public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
                                    SLog.logD("Consume Multiple finished.");
                                    for (int i = 0; i < purchases.size(); i++) {
                                        if (results.get(i).isSuccess()) {
                                            SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished success");
                                        } else {
                                            SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished fail");
                                            SLog.logD(purchases.get(i).getSku() + "consumption is not success, yet to be consumed.");
                                        }
                                    }
                                    SLog.logD("End consumption flow.");
                                }
                            });
                        }
                    }
                }
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

        if (gamaTwitterLogin != null) {
            gamaTwitterLogin.onActivityResult(requestCode, resultCode, data);
        }
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
        if (mHelper != null) {
            mHelper.dispose();
        }
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
