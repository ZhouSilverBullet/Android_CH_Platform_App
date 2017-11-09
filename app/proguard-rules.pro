# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\android\sdk/tools/proguard/proguard-android.txt
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

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * implements java.io.Serializable {*;}

-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

#友盟统计
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.caohua.games.apps.R$*{
public static final int *;
}

#高德地图
-dontwarn com.amap.api.**
-dontwarn com.autonavi.**
-dontwarn com.loc.**

#友盟推送
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

#自定义控件
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepattributes EnclosingMethod

-keepclasseswithmembernames class * {
    native <methods>;
}

# 支付宝
#-libraryjars libs/alipaySDK-20160825.jar

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

#-libraryjars libs/UPPayPluginExPro.jar
-keep class com.UCMobile.** { *; }
-keep class com.unionpay.** { *; }
-dontwarn com.UCMobile.**
-dontwarn com.unionpay.**

#-libraryjars libs/UPPayAssistEx.jar
-keep class com.unionpay.** { *; }
-dontwarn com.unionpay.**
#-libraryjars libs/nineoldandroids-2.4.0.jar
-keep class com.nineoldandroids.** { *;}
-dontwarn com.nineoldandroids.**

#-libraryjars libs/ipaynow_onlywechat_v2.0.1.jar
-keep class com.ipaynow.wechatpay.plugin.** { *; }
-dontwarn com.ipaynow.wechatpay.plugin.**
#-libraryjars libs/wftsdk2.2.0.jar
-keep class com.switfpass.pay.** { *; }
-dontwarn com.switfpass.pay.**

# for webview
-keepclassmembers class * {
    @android.webkit.JavascriptInterface
    <methods>;
}

-keep class android.webkit.** {
    <fields>;
    <methods>;
}

-keep interface  android.content.pm.** {
    <fields>;
    <methods>;
}

-keep class android.content.pm.** {
    <fields>;
    <methods>;
}

-keep class android.os.Process {
    <fields>;
    <methods>;
}

-keep class * extends java.io.Serializable

-keep class android.view.Window {
    <fields>;
    <methods>;
}

-keep class com.chsdk.ui.h5.ForgetPwd {
	public *;
}

-keep class com.chsdk.ui.h5.GetPwsByEmail {
	public *;
}

-keep class com.chsdk.ui.h5.GetPwsByMobile {
	public *;
}

-keep class com.chsdk.ui.h5.GetPwsByQuestion {
	public *;
}

-keep class com.chsdk.ui.h5.GetPwsList {
	public *;
}

# 将.class信息中的类名重新定义为"Proguard"字符串
-renamesourcefileattribute Proguard
# 并保留源文件名为"Proguard"字符串，而非原始的类名 并保留行号 // blog from sodino.com
-keepattributes SourceFile,LineNumberTable

-keep class cn.sharesdk.onekeyshare.** { *; }
-dontwarn cn.sharesdk.onekeyshare.**

-keep public class * extends WechatHandlerActivity {
         *;
 }
-keep class com.mob.tools.** { *; }
-dontwarn com.mob.tools.**

-keep class com.mob.commons.** { *; }
-dontwarn com.mob.commons.**

-keep class cn.sharesdk.framework.** { *; }
-dontwarn cn.sharesdk.framework.**

-keep class cn.sharesdk.tencent.** { *; }
-dontwarn cn.sharesdk.tencent.**

-keep class cn.sharesdk.sina.** { *; }
-dontwarn cn.sharesdk.sina.**

-keep class com.sina.** { *; }
-dontwarn com.sina.**

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.**{*;}
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class com.mob.tools.utils
-keep class com.xxx.share.onekey.theme.classic.EditPage

#--------------- BEGIN: okhttp ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
#--------------- END: okhttp ----------
#--------------- BEGIN: okio ----------
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
#--------------- END: okio ----------
#--------------- BEGIN: Glide ----------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#--------------- END: Glide ----------
#--------------- 支付宝 找不到 SSLCertificateSocketFactory 所以加入----------
-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}