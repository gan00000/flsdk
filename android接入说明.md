#SDK接入说明

* [一. SDK接入配置](#100)
* [二. SDK api说明](#101)
	* [1. 实例SDK接口IStarpy对象](#1)
	* [2. Activity生命周期调用](#2)
	* [3. 初始化sdk](#3)
	* [4. 设置SDK语言版本](#4)
	* [5. 设置角色信息](#5)
	* [6. 登录接口](#6)
	* [7. 充值接口](#7)
	* [9. 分享接口](#9)
	* [10. 打开SDK活动页面接口](#10)
	


----------------

* <h2 id="100">SDK接入配置</h2> 
说明：由于Google 2015年6月不再支持Eclipse，https://developer.android.com/studio/tools/sdk/eclipse-adt.html
，Android 24之后的版本SDK和Google play servcie库，google support v4 v7库，还有很多第三方库都已经不支持Eclipse，固本SDK不支持Eclipse，请使用AndroidStudio接入。


* 在您的项目中，打开 your_app | Gradle Scripts | build.gradle (Module: app) 并添加以下依赖的aar和其他库，以便编译SDK:

	```

	dependencies {
	    compile fileTree(include: ['*.jar'], dir: 'libs')
	
	    //SDK   添加以下配置以依赖SDK,具体版本对接的时候与SDK人员沟通确定
	    compile 'com.starpy.sdk:starpy-sdk:2.+'

	}		
	```
* 添加游戏配置文件，在您的项目中，打开 your_app | assets下创建starpy目录并添加 starpy\_game\_config 配置文件（该文件SDK对接人员提供）

* 在您的项目中，打开 your_app | Gradle Scripts | build.gradle 中productFlavors 内添加以下配置，以动态设置游戏配置

	```
	//添加以下配置 ，比如
	  productFlavors {

        twzj {
            applicationId "xxx.xxx.xxx"
            minSdkVersion 18
            targetSdkVersion 26
            versionCode 5
            versionName "2.0"
            flavorDimensions "1"

            resValue "string", "facebook_app_name", "xxxx"
            resValue "string", "facebook_app_id", "xxxxxxx"
            resValue "string", "facebook_authorities", "com.facebook.app.FacebookContentProviderxxxxxxx"
           }


    }
    ```
 ------------------------------

<h2 id="101">SDK api说明</h2>
以下为sdk api使用示例,具体请查看SDK demo 

* <h3 id="1">实例SDK接口IStarpy对象</h3>  
`iStarpy = StarpyFactory.create(); ` 
 
* <h3 id="2">Activity生命周期调用</h3> 

	```java
	游戏Activity以下相应的声明周期方法（必须调用）:  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		iStarpy = StarpyFactory.create();
	    //初始化sdk
	    iStarpy.initSDK(this);
		//在游戏Activity的onCreate生命周期中调用
	    iStarpy.onCreate(this);
	}
   @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        iStarpy.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        iStarpy.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        iStarpy.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        iStarpy.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        iStarpy.onDestroy(this);
    } 
    
     @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        iStarpy.onWindowFocusChanged(this,hasFocus);
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
      	iStarpy.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }  
    ```
 
* <h3 id="3">初始化sdk</h3>
```
	游戏启动的时候调用，通常在游戏activity oncreate中调用
	
	iStarpy.initSDK(this);
```

* <h3 id="4">设置SDK语言版本</h3> 
	
 ```
 根据游戏的语言，设置相应的SDK语言，默认为繁体中文
 游戏activity oncreate中调用
 /*
    * SGameLanguage.zh_TW 游戏为繁体语言时使用
    * SGameLanguage.en_US  游戏为英文语言时使用
    * */
    iStarpy.setGameLanguage(this, SGameLanguage.zh_TW);
 ```
	
* <h3 id="5">设置角色信息</h3> 


 ```
 在游戏获得角色信息的时候调用，每次登陆，切换账号等角色变化时调用（必须调用）
 
	/**
     * 在游戏获得角色信息的时候调用
     * roleId 角色id
     * roleName  角色名
     * rolelevel 角色等级
     * severCode 角色伺服器id
     * serverName 角色伺服器名称
     */
iStarpy.registerRoleInfo(this, "roleid_1", "roleName", "rolelevel", "s1001", "serverName");
```

* <h3 id="6">登录接口</h3>  

 ```
//登陆接口 ILoginCallBack为登录成功后的回调
iStarpy.login(MainActivity.this, new ILoginCallBack() {
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
/*
充值接口
SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
cpOrderId cp订单号，请保持每次的值都是不会重复的
productId 充值的商品id
roleLevel 角色等级
customize 自定义透传字段（从服务端回调到cp）
注意：设置SPayType.OTHERS为第三方储值时不需要传cpOrderId，productId，customize，传了会被忽略掉
*/
iStarpy.pay(MainActivity.this, SPayType.GOOGLE, “cpOrderId”, "productId", "roleLevel", "customize");
```

* <h3 id="9">分享接口</h3>

 ```
//下面的参数请按照实际传值
    
    String shareUrl = "http://www.starb168.com/brmmd_201703171125.html";
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

    iStarpy.share(MainActivity.this,iSdkCallBack,shareUrl);
 ```
 
* <h3 id="10">打开SDK活动页面接口</h3>

 ```
 该功能按需求接入
/**
 * 打开一个活动页面接口
 * level：游戏等级
 * vipLevel：vip等级，没有就写""
 */  
 
 iStarpy.openWebview(MainActivity.this,"roleLevel","vipLevel");
 ```








