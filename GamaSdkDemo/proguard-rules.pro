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

-keep public class com.ldy.sdk.pub.** { *; }
-keep public class * extends java.lang.Enum { *; }
-keep public class * extends com.mybase.bean.BaseResultModel { *; }
-keep public class * extends com.mybase.bean.AbsReqeustBean { *; }
-keep public class com.ldy.sdk.login.model.response.SLoginResponse$Data { *; }
-keep public class com.ldy.callback.ILoginCallBack { *; }
-keep public class com.ldy.callback.IPayListener { *; }
-keep public class com.ldy.base.bean.SPayType { *; }
-keep public class com.ldy.callback.SFCallBack { *; }
-keep public class com.ldy.sdk.pay.WebPayJs { *; }
-keep public class com.mybase.utils.PL { *; }
-keep public class com.ldy.sdk.ads.EventConstant { *; }
-keep public class com.mybase.bean.BaseResultModel { *; }
-keep public class com.mybase.bean.AbsReqeustBean { *; }
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