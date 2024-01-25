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
	8. [fb分享接口](#11)
	10. [客服接口](#181)
	10. [显示sdk内部绑定手机页面](#13)
	11. [显示sdk内部升级账号页面](#14)
	9. [上架Google Play相关注意的问题](#10)


----------------

* <h2 id="100">SDK接入配置</h2> 
说明：请使用AndroidStudio接入。

*  设置项目顶层的build.gradle(可请参照demo中配置)

	```
	 buildscript {
    repositories {
        google()
        mavenCentral()
		 jcenter()
    }
    dependencies {
        
        classpath 'com.android.tools.build:gradle:8.2.1'
        classpath 'com.google.gms:google-services:4.4.0'// google-services plugin
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
	
	     compileSdk 34
	
		//使用java8编译,支持静态接口
	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_1_8
	        targetCompatibility JavaVersion.VERSION_1_8
	    }
	
	    defaultConfig {
	        minSdkVersion 21
	        targetSdkVersion 34
	        multiDexEnabled true
	    }
	

	    productFlavors {
	
	
	        sdkdemo {
	            
	            applicationId "com.game.xxxx"
	            minSdkVersion 21 //设置支持的最低版本系统
	            targetSdkVersion 33  //与Google最新版本同步
	            versionCode 2
	            versionName "2.0"
	            
	            //设置参数，参数值由对接人员提供
	            resValue "string", "sdk_game_code", "xxxxx"
	            resValue "string", "sdk_app_key", "xxxxx"
	            resValue "string", "sdk_more_language", "false"
	            resValue "string", "sdk_af_dev_key", "xxxxx"
	            resValue "string", "sdk_default_server_language", "xxxxx"
            
	            resValue "string", "facebook_app_id", "xxxxx"
	            resValue "string", "facebook_client_token", "xxxxx"
	            resValue "string", "facebook_authorities", "xxxxx"
	            resValue "string", "fb_login_protocol_scheme", "xxxxx"
	            resValue "string", "line_channelId", "xxxxx"
	            resValue "string", "channel_platform", "google"  //渠道区分,google包设置google


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
		
		//DYSdk-release-1.0为sdk内提供的aar库
	    implementation(name:'DYSdk-release-1.0', ext:'aar')
	    
	    //基础库
	    api 'androidx.legacy:legacy-support-v4:1.0.0'
	    api 'androidx.appcompat:appcompat:1.6.1'
	    api 'androidx.recyclerview:recyclerview:1.3.2'
	    api 'androidx.constraintlayout:constraintlayout:2.1.4'
	    api 'androidx.browser:browser:1.7.0'
	    //mutildex
	    implementation 'androidx.multidex:multidex:2.0.1'
	    //google pay
	    implementation "com.android.billingclient:billing:6.1.0"
	    //google评分
	    implementation 'com.google.android.play:review:2.0.1'
	
	    implementation 'com.google.code.gson:gson:2.8.6'
	    implementation("com.google.guava:guava:31.1-android")
	
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
	    implementation 'com.facebook.android:facebook-core:16.0.0'
	    // Facebook Login only
	    implementation 'com.facebook.android:facebook-login:16.0.0'
	    // Facebook Share only
	    implementation 'com.facebook.android:facebook-share:16.0.0'
	    // Facebook Messenger only
	    implementation 'com.facebook.android:facebook-messenger:16.0.0'
	    //af
	    implementation 'com.appsflyer:af-android-sdk:6.9.0'
	    implementation 'com.android.installreferrer:installreferrer:2.2'
	
	    implementation 'io.reactivex.rxjava3:rxandroid:3.0.2'
	    implementation 'io.reactivex.rxjava3:rxjava:3.1.5'
	    implementation 'com.adjust.sdk:adjust-android:4.33.5'
	
	}


	```


*   添加游戏配置文件，在您的项目中，打开 your_app根目录下添加**google-services.json配置文件（该文件SDK对接人员提供）**
*   添加**adjustEvent**文件到 **assets/dysdk/**目录下面


 ------------------------------

<h2 id="101">SDK api说明</h2>
以下为sdk api使用示例,具体请查看SDK demo 

* <h3 id="1">实例SDK接口对象</h3>  
`mIDYSDK = DYSdk.getInstance(); ` 
 
* <h3 id="2">Activity生命周期和初始化SDK</h3> 

	```java
	游戏Activity以下相应的声明周期方法（必须调用）:  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIDYSDK = DYSdk.getInstance();//返回单例对象
       
        //在游戏Activity的onCreate生命周期中调用
        mIDYSDK.onCreate(this);
	    
	}
   @Override
    protected void onResume() {
        super.onResume();
        在游戏Activity中调用该方法
        mIDYSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		//在游戏Activity中调用该方法
        mIDYSDK.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在游戏Activity中调用该方法
        mIDYSDK.onPause(this);
        
    }

    @Override
    protected void onStop() {
        super.onStop();
        //在游戏Activity中调用该方法
        mIDYSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在游戏Activity中调用该方法
        mIDYSDK.onDestroy(this);
    } 
    
     @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //在游戏Activity中调用该方法
        mIDYSDK.onWindowFocusChanged(this,hasFocus);
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
      	mIDYSDK.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
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
    void setRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

     
  	//sample:
	mIDYSDK.registerRoleInfo(this, "roleid_1", "roleName", "rolelevel", "vip", "s1001", "serverName");
	

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
	mIDYSDK.login(MainActivity.this, new ILoginCallBack() {
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
    mIDYSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, "xxxx","role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {
                 

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
    
    public void trackEvent(Activity activity, String eventName)

	sample:
	
	//埋点检查更新事件
    mIDYSDK.trackEvent(MainActivity.this, EventConstant.EventName.CHECK_UPDATE);
	
	
	
	
	
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
	
	mIDYSDK.share(MainActivity.this, "#萬靈召喚師","2022首款卡牌大作【萬靈召喚師】，爆笑來襲！從東方文明到西方文明的羈絆，從神族到魔族的對抗，一段奇妙的神仙冒險之旅就此展開！","https://static-resource.meowplayer.com/share/index.html", new ISdkCallBack() {
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
		
* <h3 id="181">客服接口</h3>   
	
	```
	//打开sdk内置的客服页面
	void openCs(Activity activity);
    
	
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
	mIDYSDK.showBindPhoneView(MainActivity.this, new SFCallBack<BaseResponseModel>() {
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
    
    mIDYSDK.showUpgradeAccountView(MainActivity.this, new SFCallBack() {
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
	


	
	
	


