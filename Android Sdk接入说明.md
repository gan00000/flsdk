#SDK接入说明

* [一. SDK接入配置](#100)
* [二. SDK api说明](#101)
	* [1. 实例SDK接口IFLSDK对象](#1)
	* [2. Activity生命周期调用](#2)
	* [3. 初始化sdk](#3)
	* [4. 设置SDK语言版本](#4)
	* [5. 设置角色信息](#5)
	* [6. 登录接口](#6)
	* [7. 充值接口](#7)
	* [9. 分享接口](#9)
	*   [10. 客服接口](#11)
	
	


----------------

* <h2 id="100">SDK接入配置</h2> 
说明：由于Google 2015年6月不再支持Eclipse，https://developer.android.com/studio/tools/sdk/eclipse-adt.html
，Android 24之后的版本SDK和Google play servcie库，google support v4 v7库，还有很多第三方库都已经不支持Eclipse，固本SDK不支持Eclipse，请使用AndroidStudio接入。

*  在项目顶层的build.gradle的repositories中添加(请参照demo中配置)

	```
	 google()
	```

*  在根项目的build.gradle的dependencies中添加(请参照demo中配置)

	```
classpath 'com.android.tools.build:gradle:4.0.1'
classpath 'com.google.gms:google-services:4.2.0'// google-services plugin
	```

* 在您的项目中，打开 your_app | Gradle Scripts | build.gradle (Module: app) 并添加以下配置，依赖的aar和其他库，以便编译SDK:

	```
		//公共库
    //Google库
    implementation 'com.google.android.gms:play-services-auth:18.1.0'
    implementation 'com.google.android.gms:play-services-base:17.4.0'
    implementation 'com.google.android.gms:play-services-games:20.0.0'

    //firebase
    implementation 'com.google.firebase:firebase-core:17.5.0'
    implementation 'com.google.firebase:firebase-messaging:20.2.4'
    implementation 'com.google.firebase:firebase-auth:19.3.2'
    // Recommended: Add the Firebase SDK for Google Analytics.
    implementation 'com.google.firebase:firebase-analytics:17.5.0'
    // Add the Firebase Crashlytics SDK.
    //implementation 'com.google.firebase:firebase-crashlytics:17.2.1'

    //Facebook库
    implementation 'com.facebook.android:facebook-login:7.1.0'
    implementation 'com.facebook.android:facebook-share:7.1.0'
    implementation 'com.facebook.android:facebook-messenger:7.1.0'
    //额外库
    api 'androidx.legacy:legacy-support-v4:1.0.0'
    api 'androidx.appcompat:appcompat:1.2.0'
    api 'androidx.recyclerview:recyclerview:1.1.0'
    api 'androidx.constraintlayout:constraintlayout:2.0.0'
    api 'androidx.browser:browser:1.2.0'
    //mutildex
    implementation 'androidx.multidex:multidex:2.0.1'

    implementation 'com.google.code.gson:gson:2.8.2'

    implementation 'com.github.bumptech.glide:glide:4.11.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
    //google volley
    implementation 'com.android.volley:volley:1.1.1'

    //Twitter
    implementation 'com.twitter.sdk.android:twitter-core:3.1.1'
    implementation ('com.twitter.sdk.android:tweet-ui:3.1.1'){
        exclude group: 'com.android.support'
    }
    implementation ('com.twitter.sdk.android:tweet-composer:3.1.1'){
        exclude group: 'com.android.support'
    }
    implementation 'com.twitter.sdk.android:twitter-mopub:3.1.1'

    //ads
    //adjust
    api 'com.adjust.sdk:adjust-android:4.21.1'
    api 'com.android.installreferrer:installreferrer:2.1'

    implementation 'com.zhy:base-rvadapter:3.0.3'
	    
	```
	
* 在运行module的build.gradle最后添加 

	```
apply plugin: 'com.google.gms.google-services'
	```

* 添加游戏配置文件，在您的项目中，打开 your_app | assets下创建mwsdk目录并添加 **gameconfig.propertie和adlist 配置文件（该文件SDK对接人员提供）**

*   添加游戏配置文件，在您的项目中，打开 your_app根目录下添加google-services.json配置文件（该文件SDK对接人员提供）  


* 在您的项目中，打开 your_app | Gradle Scripts | build.gradle 中productFlavors 内添加以下配置，以动态设置游戏配置

	```
	//添加以下配置, 具体参数由SDK对接人员提供 ，比如
	  productFlavors {

       ylj {
            signingConfig signingConfigs.release
            //对接人员提供
            applicationId "com.flyfun.ylj.google"
            minSdkVersion 21
            targetSdkVersion 28
            versionCode 1
            versionName "1.0"
            flavorDimensions "1"

            //对接人员提供一下参数
            esValue "string", "scheme", "fzdld"
            resValue "string", "facebook_app_id", "xxx"
            resValue "string", "facebook_authorities", "com.facebook.app.FacebookContentProviderxxx"
            resValue "string", "fb_login_protocol_scheme", "fb566990643996286"            resValue "string", "facebook_app_name", "xxx"

            //每个游戏的demo设置
            resValue "string", "sdk_name", "Demo"

        }

    }
    ```
 ------------------------------

<h2 id="101">SDK api说明</h2>
以下为sdk api使用示例,具体请查看SDK demo 

* <h3 id="1">实例SDK接口IFLSDK对象</h3>  
`mIFLSDK = FlSdkFactory.create(); ` 
 
* <h3 id="2">Activity生命周期调用</h3> 

	```java
	游戏Activity以下相应的声明周期方法（必须调用）:  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIFLSDK = FlSdkFactory.create();
	    
	    //初始化sdk
        mIFLSDK.initSDK(this, SGameLanguage.zh_TW);
        
		 //在游戏Activity的onCreate生命周期中调用
        mIFLSDK.onCreate(this);
	    
	    
	}
   @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        mIFLSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIFLSDK.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIFLSDK.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        mIFLSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        mIFLSDK.onDestroy(this);
    } 
    
     @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mIFLSDK.onWindowFocusChanged(this,hasFocus);
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
      	PL.i("activity onRequestPermissionsResult");
      	mIFLSDK.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }  
    ```
 
* <h3 id="3">初始化sdk</h3>
```
	游戏启动的时候调用，通常在游戏activity oncreate中调用
	
	mIFLSDK.initSDK(this, SGameLanguage.en_US);
```

* <h3 id="4">设置SDK语言版本</h3> 
	
 ```
 根据游戏的语言，设置相应的SDK语言，默认为繁体中文
 游戏activity oncreate中调用
 /*
    * SGameLanguage.zh_TW 游戏为繁体语言时使用
    * SGameLanguage.en_US  游戏为英文语言时使用
    * */
    mIFLSDK.setGameLanguage(this, SGameLanguage.zh_TW);
 ```
	
* <h3 id="5">设置角色信息</h3> 


 ```
 在游戏获得角色信息的时候调用，每次登陆，切换账号等角色变化时调用
 
	/**
     * 在游戏获得角色信息的时候调用
     * roleId 角色id
     * roleName  角色名
     * rolelevel 角色等级
     * severCode 角色伺服器id
     * serverName 角色伺服器名称
     */
mIFLSDK.registerRoleInfo(this, "roleid_1", "roleName", "rolelevel", "s1001", "serverName");
```

* <h3 id="6">登录接口</h3>  

 ```
//登陆接口 ILoginCallBack为登录成功后的回调
mIFLSDK.login(MainActivity.this, new ILoginCallBack() {
        @Override
        public void onLogin(SLoginResponse sLoginResponse) {
            if (sLoginResponse != null){
                String uid = sLoginResponse.getUserId();
                String accessToken = sLoginResponse.getAccessToken();
                String timestamp = sLoginResponse.getTimestamp();

                PL.i("uid:" + uid);

            }
        }
    });
```

* <h3 id="7">充值接口</h3>    

 ```
    充值接口
    SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
    cpOrderId cp订单号，请保持每次的值都是不会重复的
    productId 充值的商品id
    customize 自定义透传字段（从服务端回调到cp）
    */
    mIFLSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),"com.sku1", "xxx", new IPayListener() {
        @Override
        public void onPayFinish(Bundle bundle) {
            PL.i("支付结束");
        }
    });
```

* <h3 id="9">分享接口</h3>

 ```
//下面的参数请按照实际传值
    
    String shareUrl = "https://www.baidu.com/";
      //分享回调
    ISdkCallBack iSdkCallBack = new ISdkCallBack() {
        @Override
        public void success() {
            PL.i("share  success");
        }

        @Override
        public void failure() {
            PL.i("share  failure");
        }
    };

    mIFLSDK.share(MainActivity.this,iSdkCallBack,shareUrl);
 ```
 
* <h3 id="11">客服接口</h3>

 ```
 	//打开一个客服网页
	mIFLSDK.openCs(MainActivity.this);
 ```
 
* <h3 id="10">上架Google Play相关注意的问题</h3>
1. apk包不要包含有其他渠道的代码资源，不要包含有talkdata sdk相关代码
2. openssl漏洞问题，应用迁移至 OpenSSL 1.02f/1.01r 或更高版本，查看命令 ($ unzip -p YourApp.apk | strings | grep "OpenSSL")，相关说明https://support.google.com/faqs/answer/6376725
3. 检查权限，不要添加多余的、没有使用的权限，使用到的权限越少越好；不能使用Google不建议的权限,如SYSTEM_ALERT_WINDOW and WRITE_SETTINGS 
相关说明:https://developer.android.com/guide/topics/permissions/requesting.html#perm-groups"
4. apk包不能大于100M,超过100m,需要把相关资源做成扩展文件obb,相关文档https://developer.android.com/google/play/expansion-files。超过100M可以参照Google官方的建议做法（推荐） 或者  启动游戏进行cdn加载
5. 最低支持Android系统4.3
6. Libpng 漏洞问题，需要使用合适的版本 文档： https://support.google.com/faqs/answer/7011127
7. WebView SSL 错误处理 https://support.google.com/faqs/answer/7071387?hl=zh-Hans
8. 确保您的应用支持64位设备  https://developer.android.com/distribute/best-practices/develop/64-bit
9. Android support支持库迁移AndroidX https://developer.android.google.cn/jetpack/androidx 参考链接 https://developer.android.google.cn/jetpack/androidx/migrate






