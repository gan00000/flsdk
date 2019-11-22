package com.gama.sdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.crashlytics.android.Crashlytics;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.Localization;
import com.gama.base.utils.SLog;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
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
import com.gama.thirdlib.google.SGoogleProxy;
import com.gama.thirdlib.twitter.GamaTwitterLogin;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseMainActivity extends Activity {

    protected Button loginButton, othersPayButton, googlePayBtn, shareButton, showPlatform, crashlytics,
            PurchasesHistory, getFriend, invite, checkShare, getInfo, getFriendNext, getFriendPrevious,
            service, announcement, age, demo_language, track, chaxun, xiaofei, open_url, open_page, demo_pay_one;
    protected ImageView image_test;
    protected IGama iGama;
    private String nextUrl, previousUrl;
    private GamaTwitterLogin gamaTwitterLogin;
    private int iabCount = 0;
    private int iabIndex = 0;
    private int maxCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        othersPayButton = findViewById(R.id.demo_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        shareButton = findViewById(R.id.demo_share);
        showPlatform = findViewById(R.id.showPlatform);
        crashlytics = findViewById(R.id.Crashlytics);
        PurchasesHistory = findViewById(R.id.PurchasesHistory);
        chaxun = findViewById(R.id.chaxun);

        getInfo = findViewById(R.id.getInfo);
        getFriend = findViewById(R.id.getFriend);
        getFriendNext = findViewById(R.id.getFriendNext);
        getFriendPrevious = findViewById(R.id.getFriendPrevious);
        invite = findViewById(R.id.invite);
        checkShare = findViewById(R.id.checkShare);
        service = findViewById(R.id.service);
        announcement = findViewById(R.id.announcement);
        age = findViewById(R.id.age);
        track = findViewById(R.id.track);
        xiaofei = findViewById(R.id.xiaofei);
        open_url = findViewById(R.id.open_url);
        open_page = findViewById(R.id.open_page);
        demo_pay_one = findViewById(R.id.demo_pay_one);

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

                            String msg = "gamaUid : " + uid + "\n"
                                    + "thirdId : " + sLoginResponse.getThirdId() + "\n"
                                    + "loginType : " + sLoginResponse.getLoginType() + "\n"
                                    + "accessToken : " + sLoginResponse.getAccessToken() + "\n"
                                    + "iconUri : " + sLoginResponse.getIconUri() + "\n"
                                    + "ip : " + sLoginResponse.getGmbPlayerIp() + "\n"
                                    + "timeStamp : " + sLoginResponse.getTimestamp() + "\n";
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
                            String roleId = "123"; //角色id
                            String roleName = "角色名"; //角色名
                            String roleLevel = "10"; //角色等级
                            String vipLevel = "5"; //角色vip等级
                            String serverCode = "1"; //角色伺服器id
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

        // String shareUrl = "http://ads.starb168.com/ads_scanner?gameCode=mthxtw&adsPlatForm=star_event&advertiser=share";
        // https://dodi.gamamobi.com/share/index.html?gameCode=dodi&userId=1&roleId=123&roleName=erge&serverCode=1000&serverName=testServer&package=com.xxx.xxx&
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BaseMainActivity.this)
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
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(BaseMainActivity.this)
                                        .setItems(new String[]{"分享图片", "分享文字/链接", "同时分享图片、文字/链接"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int index) {
                                                if (index == 0) {
                                                    iGama.gamaShare(BaseMainActivity.this, finalType, message, "", picPath, new ISdkCallBack() {
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
                                                        ToastUtils.toast(BaseMainActivity.this, "facebook-messenger只支持图片分享");
                                                        return;
                                                    }
                                                    iGama.gamaShare(BaseMainActivity.this, finalType, message, shareUrl, null, new ISdkCallBack() {
                                                        @Override
                                                        public void success() {
                                                            Log.i("facebook", "success");
                                                        }

                                                        @Override
                                                        public void failure() {
                                                            Log.i("facebook", "failure");
                                                        }
                                                    });
                                                } else if (index == 2) {
                                                    if (finalType != GamaThirdPartyType.WHATSAPP &&
                                                            finalType != GamaThirdPartyType.TWITTER) {
                                                        ToastUtils.toast(BaseMainActivity.this, "当前类型不支持同时分享图片和文字");
                                                        return;
                                                    }
                                                    iGama.gamaShare(BaseMainActivity.this, finalType, message, shareUrl, picPath, new ISdkCallBack() {
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
//                iGama.share(BaseMainActivity.this,iSdkCallBack,shareUrl);

//                iGama.share(BaseMainActivity.this,iSdkCallBack,"", "", shareUrl, "");

            }
        });

        open_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 打开一个活动页面接口
                 */
                iGama.openWebview(BaseMainActivity.this);
            }
        });

        open_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 打开一个指定的url
                 * String url: 打开的url
                 */
                iGama.openWebPage(BaseMainActivity.this, GamaOpenWebType.CUSTOM_URL, "https://dodi.gamamobi.com/news/index.html", new ISdkCallBack() {
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
                if(SGameLanguage.ko_KR == Localization.getSGameLanguage(BaseMainActivity.this)) {
                    iGama.gamaOpenCafeHome(BaseMainActivity.this);
                } else if(SGameLanguage.zh_TW == Localization.getSGameLanguage(BaseMainActivity.this)) {
                    iGama.openPlatform(BaseMainActivity.this);
                }

                GamaTimeUtil.getBeiJingTime(BaseMainActivity.this);
                GamaTimeUtil.getDisplayTime(BaseMainActivity.this);
            }
        });

        crashlytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });

        final int limit = 1;
        final Bundle bundle = new Bundle();
        bundle.putString("fields", "friends.limit(" + limit + ")");

        getFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.gamaFetchFriends(BaseMainActivity.this, GamaThirdPartyType.FACEBOOK, bundle, "", limit, new FetchFriendsCallback() {
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
                    Toast.makeText(BaseMainActivity.this, "没有下一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                iGama.gamaFetchFriends(BaseMainActivity.this, GamaThirdPartyType.FACEBOOK, null, nextUrl, limit, new FetchFriendsCallback() {
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
                    Toast.makeText(BaseMainActivity.this, "没有上一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                iGama.gamaFetchFriends(BaseMainActivity.this, GamaThirdPartyType.FACEBOOK, null, previousUrl, limit, new FetchFriendsCallback() {
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

                iGama.gamaInviteFriends(BaseMainActivity.this, GamaThirdPartyType.FACEBOOK, invitingList, "消息內容", "標題", new InviteFriendsCallback() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseMainActivity.this)
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
                                boolean canShare = iGama.gamaShouldShareWithType(BaseMainActivity.this, type);
                                if (canShare) {
                                    ToastUtils.toast(BaseMainActivity.this, "支持分享");
                                } else {
                                    ToastUtils.toast(BaseMainActivity.this, "不支持分享");
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
                iGama.gamaGetUserProfile(BaseMainActivity.this, new UserProfileCallback() {
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
                iGama.openWebPage(BaseMainActivity.this, GamaOpenWebType.SERVICE, "", new ISdkCallBack() {
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
                iGama.openWebPage(BaseMainActivity.this, GamaOpenWebType.ANNOUNCEMENT, "", new ISdkCallBack() {
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
//        gamaTwitterLogin = new GamaTwitterLogin(BaseMainActivity.this);
//
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLogin()) {
                    ToastUtils.toast(BaseMainActivity.this, "请先登入");
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseMainActivity.this)
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
                                    presenter.goAgeStyleThree(BaseMainActivity.this);
                                } else if (i == 1) {
                                    presenter.goAgeStyleOne(BaseMainActivity.this);
                                } else if (i == 2) {
                                    presenter.sendAgeRequest(BaseMainActivity.this, null);
                                } else if (i == 3) {
                                    presenter.requestAgeLimit(BaseMainActivity.this);
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
                iGama.gamaTrack(BaseMainActivity.this, eventName, map, set);
            }
        });

        image_test = findViewById(R.id.image_test);
        image_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.toast(BaseMainActivity.this, "click to refresh");
                loadImage();
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

    private void loadImage() {
        String url = "https://login.gamesword.com/captcha/captcha.app?timestamp=" + System.currentTimeMillis()
                + "&operatingSystem=android&uniqueId=" + SGoogleProxy.getAdvertisingId(BaseMainActivity.this);
        PL.i(url);
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //构建ImageRequest 实例
        final ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                PL.i("response: " + response.toString());
                //给imageView设置图片
                image_test.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //设置一张错误的图片，临时用ic_launcher代替
                image_test.setImageResource(R.drawable.gama_title_sdk_bg);
            }
        });
        requestQueue.add(request);
    }
}
