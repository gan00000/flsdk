# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

-keep public class com.mw.sdk.out.** { *; }
#-keep public class com.thirdlib.vk.VKPurchaseManger { *; }
-keep public class * extends java.lang.Enum { *; }
-keep public class * extends com.core.base.bean.BaseResponseModel { *; }
-keep public class * extends com.core.base.bean.AbsReqeustBean { *; }
-keep public class com.mw.sdk.login.model.response.SLoginResponse$Data { *; }
-keep public class com.mw.sdk.login.ILoginCallBack { *; }
-keep public class com.mw.sdk.callback.IPayListener { *; }
-keep public class com.mw.base.bean.SPayType { *; }
-keep public class com.core.base.callback.SFCallBack { *; }
-keep public class com.mw.sdk.pay.WebPayJs { *; }
-keep public class com.core.base.utils.PL { *; }
-keep public class com.mw.sdk.ads.EventConstant { *; }
-keep public class com.core.base.bean.BaseResponseModel { *; }
-keep public class com.core.base.bean.AbsReqeustBean { *; }
-keep public class com.mw.sdk.bean.res.GPExchangeRes$Data { *; }
-keep public class com.mw.sdk.bean.res.GPCreateOrderIdRes$PayData { *; }
-keep public class com.mw.sdk.bean.res.ConfigBean { *; }
-keep public class com.mw.sdk.bean.res.ConfigBean$VersionData { *; }
-keep public class com.mw.sdk.bean.res.ConfigBean$UrlData { *; }
-keep public class com.mw.sdk.bean.PhoneInfo { *; }
-keep public class com.mw.sdk.login.model.QooAppLoginModel { *; }
-keep public class com.mw.sdk.login.model.QooAppLoginModel$UserData { *; }
-keep public class com.mw.sdk.bean.SUserInfo { *; }
-keep public class com.mw.sdk.bean.res.ToggleResult { *; }
-keep public class com.mw.sdk.bean.res.ToggleResult$Data { *; }
# 保留所有类的 public 方法名：
-keepclassmembers class com.core.base.callback.IGameLifeCycle {
    public *;
}

-keep public class com.mw.**.*Data { *; }
-keep public class com.mw.**.*Model { *; }
-keep public class com.mw.**.*Result { *; }
-keep public class com.mw.**.*Bean { *; }
-keep public class com.mw.**.*Res { *; }

-keep public class com.mw.**$*Data { *; }
-keep public class com.mw.**$*Model { *; }
-keep public class com.mw.**$*Result { *; }
-keep public class com.mw.**$*Bean { *; }
-keep public class com.mw.**$*Res { *; }
-keep public class com.mw.sdk.bean.res.FloatMenuResData$** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class com.bumptech.** { *; }

-keep public class * extends java.lang.Enum { *; }

-keep public class android.** { *; }
-keep public class androidx.** { *; }
-keep public class com.facebook.** { *; }
-keep public class com.google.** { *; }
-keep public class com.linecorp.** { *; }
-keep public class io.** { *; }
#-keep public class com.zhy.** { *; }
-keep public class org.spongycastle.** { *; }
-keep public class javax.** { *; }
-keep public class com.adjust.** { *; }
-keep public class com.gaa.** { *; }
-keep public class com.navercorp.** { *; }
-keep public class com.nhn.** { *; }

-keep public class retrofit2.** { *; }
-keep public class io.jsonwebtoken.** { *; }
-keep public class io.reactivex.** { *; }
-keep public class okhttp3.** { *; }

-keep public class cn.thinkingdata.** { *; }

#-keep public class com.mw.** { *; }
#-keep public class com.core.** { *; }
#-keep public class com.thirdlib.** { *; }


-dontwarn java.lang.invoke.StringConcatFactory
-keep public class com.xlsdk.mediator.** { *; }
-keep public class com.samsung.** { *; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

#tiktok
-keep class com.tiktok.** { *; }
-keep class com.android.billingclient.api.** { *; }

#af
-keep class com.appsflyer.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keep public class com.android.installreferrer.** { *; }
-keep class com.huawei.hms.**{*;}
#小米
-keep public class com.miui.** {*;}
-keep public class com.xiaomi.** {*;}

#nowgg
-keep public class com.thirdlib.nowgg.TokenVerifyResponse { *; }
-keep public class com.thirdlib.nowgg.UserDataVerified { *; }
-keep public class com.thirdlib.nowgg.TokenVerifyRequest { *; }
#singular
-keep class com.singular.sdk.** { *; }
-keep public class com.android.installreferrer.** { *; }
# Uncomment this line in case your are calling the 'revenue' function using the Google billing library
-keep public class com.android.** { *; }
