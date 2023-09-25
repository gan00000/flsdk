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

#-keep class com.appsflyer.** { *; }
#-keep public class com.android.installreferrer.** { *; }

# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

-keep public class com.mw.sdk.out.** { *; }
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
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface


# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class android.** { *; }
-keep public class androidx.** { *; }
-keep public class com.facebook.** { *; }
-keep public class com.google.** { *; }
-keep public class com.linecorp.** { *; }
-keep public class com.appflyer.** { *; }
-keep public class io.** { *; }
-keep public class com.zhy.** { *; }
-keep public class org.spongycastle.** { *; }
-keep public class javax.** { *; }
-keep public class com.adjust.** { *; }

-keep public class com.mw.** { *; }
-keep public class com.core.** { *; }
-keep public class com.thirdlib.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}