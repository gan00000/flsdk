#SDK接入说明

* [一. SDK接入配置](#100)
* [二. SDK api说明](#101)
	1. [实例SDK接口对象](#1)
	2. [Activity生命周期和初始化SDK](#2)
	3. [设置角色信息](#5)
	4. [登录接口](#6)
	5. [切换账号登录接口](#18)
	5. [充值接口](#7)
	6. [事件埋点接口](#8)
	7. [应用内评分接口](#9)
	8. [fb分享接口](#11)
	9. [line分享接口](#12)
	10. [客服接口](#181)
	10. [显示sdk内部绑定手机页面](#13)
	11. [显示sdk内部升级账号页面](#14)
	12. [请求获取手机验证码](#15)
	13. [请求绑定手机](#16)
	14. [请求账号升级](#17)
	15. [打开活动弹窗页面](#19)
	16. [打开社群(banner)展示页面](#20)
	9. [上架Google Play相关注意的问题](#10)


----------------

* <h2 id="100">SDK接入配置</h2> 
说明：由于Google 2015年6月不再支持Eclipse，https://developer.android.com/studio/tools/sdk/eclipse-adt.html
，Android 24之后的版本SDK和Google play servcie库，google support v4 v7库，还有很多第三方库都已经不支持Eclipse，固本SDK不支持Eclipse，请使用AndroidStudio接入。

*  设置项目顶层的build.gradle(可请参照demo中配置)

	```
	 buildscript {
    repositories {
        google()
        mavenCentral()
		 jcenter()
    }
    dependencies {
        
        classpath 'com.android.tools.build:gradle:8.1.2'
        // google-services plugin
        classpath 'com.google.gms:google-services:4.4.0'
        // Add the Crashlytics Gradle plugin.
        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.9.9'
    	}
	}

	allprojects {
	    repositories {
	        google()
	        mavenCentral()
		 	 jcenter()
	    }
	}

	```

* 设置您的模块（应用级）Gradle 文件

	```
	apply plugin: 'com.android.application'
	//添加 Google 服务 Gradle 插件
	apply plugin: 'com.google.gms.google-services'
	
	// Add the Crashlytics Gradle plugin
	apply plugin: 'com.google.firebase.crashlytics'

	android {
	
	    compileSdkVersion 33
	
		//java 11
	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_11
	        targetCompatibility JavaVersion.VERSION_11
	    }
	
	    defaultConfig {
	        minSdkVersion 21
	        targetSdkVersion 33
	        multiDexEnabled true
	    }
	

	    productFlavors {
	
	
	        tgsw_google {
	        
            signingConfig signingConfigs.release_kr  //签名sdk提供
            applicationId "com.xxx.xxx"  //包名sdk提供	
            minSdkVersion 21
            targetSdkVersion 33
            versionCode 1
            versionName "1.0."
            flavorDimensions "1"

            //=======================================sdk config==========================
            //以下参数对接SDK的时候提供
            //base
            resValue "string", "sdk_game_code", "xxxxx"
            resValue "string", "sdk_app_key", "xxxxx"
            resValue "string", "sdk_more_language", "xxxxx"
            resValue "string", "sdk_default_server_language", "zh_TW"   //default zh_TW, en_US/zh_TW/zh_CN/vi_VN/ko_KR
            resValue "string", "scheme", "xxxxx"
            resValue "string", "channel_platform", "google"  //渠道区分,google包设置google,apk包设置"meow",onestore渠道使用"onestore"
            //facebook
            resValue "string", "facebook_app_id", "xxxxx"
            resValue "string", "facebook_client_token", "xxxxx"
            resValue "string", "facebook_authorities", "com.facebook.app.FacebookContentProvider xxxxx" //启用分析
            resValue "string", "fb_login_protocol_scheme", "fb xxxxx"
            resValue "string", "facebook_app_name", "xxxxx" //游戏名字
            //ad
            resValue "string", "adjust_app_token", "xxxxx"
            resValue "string", "sdk_appflyer_dev_key", "xxxxx"
            //line
            resValue "string", "line_channelId", ""
            //naver
            resValue "string", "naver_client_id", "xxxxx"
            resValue "string", "naver_client_secret", "xxxxx"
            //=======================================sdk config end==========================



            //每个游戏的demo设置 test 不需要设置
            resValue "string", "sdk_name", "SdkDemo"

        	}
	
	    }
	}
	
	repositories {
	    flatDir {
	        dirs 'libs'
	    }
	}
	
	//添加下列的依赖
	dependencies {
		
		//MWSDK-release为sdk内提供的aar库，版本可能会有更改
	    implementation(name:'MWSDK-release', ext:'aar')
	    
		 
		 //基础库
		api 'androidx.legacy:legacy-support-v4:1.0.0'
		api 'androidx.appcompat:appcompat:1.6.1'
		api 'androidx.recyclerview:recyclerview:1.3.2'
		api 'androidx.constraintlayout:constraintlayout:2.1.4'
		api 'androidx.browser:browser:1.7.0'
		implementation 'androidx.viewpager2:viewpager2:1.0.0'
		
		//mutildex
		implementation 'androidx.multidex:multidex:2.0.1'
		//google pay
		implementation "com.android.billingclient:billing:6.1.0"
		//google评分
		implementation 'com.google.android.play:review:2.0.1'
		
		implementation("com.google.guava:guava:31.1-android")
		implementation 'com.zhy:base-rvadapter:3.0.3'
		
		//Google库
		implementation 'com.google.android.gms:play-services-auth:20.7.0'
		implementation 'com.google.android.gms:play-services-base:18.2.0'
		implementation 'com.google.android.gms:play-services-games:23.1.0'
		
		//firebase
		implementation platform('com.google.firebase:firebase-bom:32.3.1')
		implementation 'com.google.firebase:firebase-messaging'
		implementation 'com.google.firebase:firebase-auth'
		// Recommended: Add the Firebase SDK for Google Analytics.
		implementation 'com.google.firebase:firebase-analytics'
		// Add the Firebase Crashlytics SDK.
		implementation 'com.google.firebase:firebase-crashlytics'
		
		//Facebook库
		// Facebook Core only (Analytics)
		implementation 'com.facebook.android:facebook-core:16.0.0'  //latest.release
		// Facebook Login only
		implementation 'com.facebook.android:facebook-login:16.0.0'
		// Facebook Share only
		implementation 'com.facebook.android:facebook-share:16.0.0'
		// Facebook Messenger only
		implementation 'com.facebook.android:facebook-messenger:16.0.0'
		//line
		implementation 'com.linecorp:linesdk:5.0.1'
		
		//af
		implementation 'com.appsflyer:af-android-sdk:6.9.0'
		implementation 'com.android.installreferrer:installreferrer:2.2'
		
		
		implementation 'com.google.code.gson:gson:2.8.6'
		implementation 'io.reactivex.rxjava3:rxandroid:3.0.2'
		implementation 'io.reactivex.rxjava3:rxjava:3.1.5'
		implementation 'com.squareup.retrofit2:retrofit:2.9.0'
		implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
		implementation 'com.squareup.retrofit2:adapter-rxjava3:2.9.0'
		implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
		
		implementation 'com.github.bumptech.glide:glide:4.16.0'
		annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'
		
		//naver
		implementation("com.navercorp.nid:oauth:5.6.0") // jdk 11
		//onestore
		implementation "com.onestorecorp.sdk:sdk-iap:21.00.00"
		implementation "com.onestorecorp.sdk:sdk-configuration-kr:1.0.0"
	
	}


	```


*   添加游戏配置文件，在您的项目中，打开 your_app根目录下添加**google-services.json配置文件（该文件SDK对接人员提供）**


 ------------------------------

<h2 id="101">SDK api说明</h2>
以下为sdk api使用示例,具体请查看SDK demo 

* <h3 id="1">实例SDK接口IMWSDK对象</h3>  
`mIMWSDK = MWSdkFactory.create(); ` 
 
* <h3 id="2">Activity生命周期和初始化SDK</h3> 

	```java
	
	1.应用Application onCreate中调用 （必须调用）
	public class XXXApplication extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	
	        MWSdkFactory.create().applicationOnCreate(this);
	    }

  }
	
	2.游戏Activity以下相应的声明周期方法（必须调用）:  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIMWSDK = MWSdkFactory.create();
       
        //在游戏Activity的onCreate生命周期中调用
        mIMWSDK.onCreate(this);
	    
	}
   @Override
    protected void onResume() {
        super.onResume();
        在游戏Activity中调用该方法
        mIMWSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		//在游戏Activity中调用该方法
        mIMWSDK.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在游戏Activity中调用该方法
        mIMWSDK.onPause(this);
        
    }

    @Override
    protected void onStop() {
        super.onStop();
        //在游戏Activity中调用该方法
        mIMWSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在游戏Activity中调用该方法
        mIMWSDK.onDestroy(this);
    } 
    
     @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //在游戏Activity中调用该方法
        mIMWSDK.onWindowFocusChanged(this,hasFocus);
    }
    
     /**
     * 6.0以上系统权限授权回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { 
    	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      	//在游戏Activity中调用该方法
      	mIMWSDK.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    } 
    
     
    ```
 	
* <h3 id="5">设置角色信息</h3> 
	
	在游戏获得角色信息的时候调用，每次登陆，切换账号等角色变化时调用

 	```
 
 接口定义:
	/**
     * 设置角色信息  在游戏获得角色信息的时候调用，每次登陆，切换账号等角色变化时调用
     * @param roleId            角色id  				必传
     * @param roleName          角色名   			必传
     * @param roleLevel         角色等级			没有传空值 ""
     * @param vipLevel          vip等级   			没有传空值 ""
     * @param severCode         角色伺服器id 		必传
     * @param serverName        角色伺服器名称	 	必传
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

     
  	//sample:
	mIMWSDK.registerRoleInfo(this, "roleid_1", "roleName", "rolelevel", "vip", "s1001", "serverName");
	

	```

* <h3 id="6">登录接口</h3>  

 	```
 	
 	接口定义:
 	/**
     * 登录
     * @param activity
     * @param iLoginCallBack        登录回调
     */
    void login(Activity activity, ILoginCallBack iLoginCallBack);
 	
 	
	//sample:
	mIFLSDK.login(MainActivity.this, new ILoginCallBack() {
	    @Override
	    public void onLogin(SLoginResponse sLoginResponse) {
	        if (sLoginResponse != null){
	        		
	        	 //获得登录成功后的用户uid
	            String uid = sLoginResponse.getUserId();
	            String sign = sLoginResponse.getSign();
	            String timestamp = sLoginResponse.getTimestamp();
				
	        }
	    }
	});

 	```
 	
* <h3 id="18">切换账号登录接口</h3>  

	游戏切换账号请使用该接口，返回数据也上面的登录接口相同
	
 	```
 	void switchLogin(Activity activity, ILoginCallBack iLoginCallBack);
 	
 	```

* <h3 id="7">充值接口</h3>    

 	```
 
 接口定义:
    /**
     * @param activity
     * @param payType           SPayType.WEB为平台网页第三方储值，SPayType.GOOGLE为Google储值
     * @param cpOrderId         厂商订单号    必传
     * @param productId         购买的商品id   必传
     * @param extra             预留的穿透值   可选
     * @param roleId            角色id  				必传
     * @param roleName          角色名   			必传
     * @param roleLevel         角色等级			没有传空值 ""
     * @param vipLevel          vip等级   			没有传空值 ""
     * @param severCode         角色伺服器id 		必传
     * @param serverName        角色伺服器名称	 	必传
     * @param listener          充值回调              辅助回调，充值是否成功以服务端回调为准
     */
    void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName, IPayListener listener);

 
    //sample:
    mIMWSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, "xxxx","role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {
                 

            @Override
            public void onPaySuccess(String productId, String cpOrderId) {
					//支付成功，客户端是辅助，请以服务端为准
            }

            @Override
            public void onPayFail() {
					//支付失败，客户端是辅助，请以服务端为准
            }

        });
                
       
	```


* <h3 id="8">事件埋点接口</h3>   
	
	```
	
	/**
     * 事件埋点接口
     * @param activity
     * @param eventName 事件名称
     */
    void trackEvent(Activity activity, EventConstant.EventName eventName);
    
    游戏接入需要对下列几个事件进行埋点：
    CHECK_PERMISSIONS,  检查权限(如果有该操作，没有不用接)
    CHECK_UPDATE, 检查更新
    CHECK_RESOURCES, 检查游戏资源
    SELECT_SERVER,	选择伺服器
    CREATE_ROLE,		创建角色
    START_GUIDE, 开始新手引导
    COMPLETE_GUIDE,  完成新手引导
    
    此接口可接入常用的SDK埋点事件，如有特殊的另外事件需要进行埋点，使用 ：
    public void trackEvent(Activity activity, String eventName)

	sample:
	
	//埋点检查更新事件
    mIMWSDK.trackEvent(MainActivity.this, EventConstant.EventName.CHECK_UPDATE);
	
	
	
	
	
	```
	
* <h3 id="9">应用内评分接口</h3>  
	
	```
	接口声明：
	public void requestStoreReview(Activity activity, SFCallBack sfCallBack);
	
	示例：
	
	//重要提示：如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
    mIMWSDK.requestStoreReview(MainActivity.this, new SFCallBack() {
        @Override
        public void success(Object result, String msg) {
            //评价成功
        }

        @Override
        public void fail(Object result, String msg) {
            //评价失败
        }
    });
	```
	
* <h3 id="11">fb分享接口</h3>   
	
	```
	
	/**
     * fb分享
     * @param activity
     * @param hashTag  话题
     * @param msg   引文内容
     * @param shareLinkUrl  分享的链接
     * @param iSdkCallBack  分享回调
     */
    void share(Activity activity, String hashTag, String msg, String shareLinkUrl, ISdkCallBack iSdkCallBack);
    

	sample:
	
	mIMWSDK.share(MainActivity.this, "#萬靈召喚師","2022首款卡牌大作【萬靈召喚師】，爆笑來襲！從東方文明到西方文明的羈絆，從神族到魔族的對抗，一段奇妙的神仙冒險之旅就此展開！","https://static-resource.meowplayer.com/share/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        Log.i("tag","share success");
                    }

                    @Override
                    public void failure() {
                        Log.i("tag","share failure");
                    }
                });
                
	
	```
	
* <h3 id="12">line分享接口</h3>   
	
	```
	/**
     * line分享
     * @param activity
     * @param content 分享的内容
     * @param iSdkCallBack  分享回调
     */    
     void shareLine(final Activity activity, final String content, final ISdkCallBack iSdkCallBack);
    
	sample:
	
	mIMWSDK.shareLine(MainActivity.this, "★胡宇威盛裝代言★邀你前往異時空三國世界！重新定義三國名將，在鬼武時空大顯神威！https://member.dustyx.com/sdk/share/gwsg/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });
	```
	
* <h3 id="181">客服接口</h3>   
	
	```
	//打开sdk内置的客服页面
	 void openCs(Activity activity, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);


    
	
	```
	
* <h3 id="13">显示sdk内部绑定手机页面</h3>   
	
	```
	/**
     * 显示sdk内部绑定手机页面
     * @param activity
     * @param sfCallBack
     */
    public void showBindPhoneView(Activity activity, SFCallBack sfCallBack);
    
	//示例
	mIMWSDK.showBindPhoneView(MainActivity.this, new SFCallBack<BaseResponseModel>() {
                    @Override
                    public void success(BaseResponseModel result, String msg) {
                        //todo绑定手机成功

                    }

                    @Override
                    public void fail(BaseResponseModel result, String msg) {
                        //todo绑定手机失败
                    }
                });
	```
	
* <h3 id="14">显示sdk内部升级账号页面</h3>   
	
	```
	
	public void showUpgradeAccountView(Activity activity, SFCallBack sfCallBack);
	
	/**
     * 显示sdk内部升级账号页面
     * @param activity
     * @param sfCallBack
     */
    
    mIMWSDK.showUpgradeAccountView(MainActivity.this, new SFCallBack() {
            @Override
            public void success(Object result, String msg) {
                //账号升级成功
                SLoginResponse sLoginResponse = (SLoginResponse)result;
            }

            @Override
            public void fail(Object result, String msg) {

            }
        });	
	```
	
* <h3 id="15">请求获取手机验证码</h3>   
	
	```
	/**
     * 请求获取手机验证码
     * @param activity
     * @param areaCode  手机区号（不带"+"，如中国：86）
     * @param telephone 手机号码
     * @param sfCallBack    回调
     */
    public void requestVfCode(Activity activity, String areaCode, String telephone,SFCallBack sfCallBack);
    
    示例：
    mIMWSDK.requestVfCode(this, "86", "13622843403", new SFCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String result) {
                //获取手机验证码成功
                //todo
            }

            @Override
            public void fail(BaseResponseModel responseModel, String result) {
                if (responseModel != null){
                    String errMsg = responseModel.getMessage();//获取验证码 错误信息
                }
            }
        });
	
	```
* <h3 id="16">请求绑定手机</h3>   
	
	```
	/**
     * 请求绑定手机
     * @param activity
     * @param areaCode 区号
     * @param telephone 手机号码
     * @param vfCode    验证码
     * @param sfCallBack
     */
    public void requestBindPhone(Activity activity, String areaCode, String telephone,String vfCode, SFCallBack sfCallBack);
    
    
    示例:
    
     mIMWSDK.requestBindPhone(this, "86", "13622843403", "111111", new SFCallBack<SLoginResponse>() {
            @Override
            public void success(SLoginResponse sLoginResponse, String result) {
                if (sLoginResponse != null) {
                    String tel = sLoginResponse.getData().getTelephone();//绑定的手机号码，格式：国际区号-号码，比如86-13622843403
                }
            }

            @Override
            public void fail(SLoginResponse sLoginResponse, String result) {
                if (sLoginResponse != null) {
                    String errMsg = sLoginResponse.getMessage();
                }

            }
        });
	
	```
* <h3 id="17">请求账号升级</h3>   
	
	```
	 /**
     * 请求账号升级
     * @param activity
     * @param account 需要绑定的新账号
     * @param pwd 新账号的密码
     * @param sfCallBack 回调
     */
    public void requestUpgradeAccount(Activity activity, String account, String pwd, SFCallBack sfCallBack);
	
	示例：
	mIMWSDK.requestUpgradeAccount(this, "xxx", "pwd", new SFCallBack<SLoginResponse>() {

            @Override
            public void success(SLoginResponse result, String msg) {
                //账号升级成功
            }

            @Override
            public void fail(SLoginResponse result, String msg) {
                //账号升级绑定失败
            }
        });
        
	```
	
* <h3 id="19">打开活动弹窗页面</h3>   
	
	```
	 /**
	 * 游戏内设置一个按钮，触发打开活动弹窗页面
	 * @param activity
	 */
	public void showActView(Activity activity);
		
	    
	```
	
* <h3 id="20">打开社群(banner)展示页面</h3>   
	
	```
	 /**
	 * 游戏内设置一个按钮，打开社群展示页面
	 * @param activity
	 */
	public void showSocialView(Activity activity);
		
	    
	```
	
* <h3 id="10">上架Google Play相关注意的问题</h3>
	1. apk包不要包含有其他渠道的代码和资源，不要包含有talkdata sdk相关代码
	2. openssl漏洞问题，应用迁移至 OpenSSL 1.02f/1.01r 或更高版本，查看命令 ($ unzip -p YourApp.apk | strings | grep "OpenSSL")，相关说明：https://support.google.com/faqs/answer/6376725
	3. 检查权限，不要添加多余的、没有使用的权限，使用到的权限越少越好；不能使用Google不建议的权限,如SYSTEM_ALERT_WINDOW and WRITE_SETTINGS 
	相关说明:https://developer.android.com/guide/topics/permissions/requesting.html#perm-groups"
	4. 从 2021 年 8 月起，新应用需要使用 Android App Bundle 才能在 Google Play 中发布。现在，Play Feature Delivery 或 Play Asset Delivery 支持大小超过 150 MB 的新应用,相关链接：https://developer.android.com/guide/app-bundle?hl=zh-cn
	5. 最低版本支持：minSdkVersion 21
	6. Libpng 漏洞问题，需要使用合适的版本 文档： https://support.google.com/faqs/answer/7011127
	7. WebView SSL 错误处理 https://support.google.com/faqs/answer/7071387?hl=zh-Hans
	8. 确保您的应用支持64位设备  https://developer.android.com/distribute/best-practices/develop/64-bit
	9. sdk使用AndroidX，不使用Android support。支持库迁移AndroidX https://developer.android.google.cn/jetpack/androidx 参考链接 https://developer.android.google.cn/jetpack/androidx/migrate
	


	
	
	


