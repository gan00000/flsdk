//package com.mw.fcm;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//
//import androidx.core.app.NotificationCompat;
//
//import com.core.base.utils.PL;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//
//public class MWFirebaseMessagingService extends FirebaseMessagingService {
//
///*
//    应用状态	    通知	                        数据	                    两个都
//    前景	    onMessageReceived	        onMessageReceived	    onMessageReceived
//    背景	    系统托盘	                    onMessageReceived	    通知：系统托盘 | 数据：在意图的附加值中。
//*/
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        PL.i("remoteMessage From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            PL.i("Message data payload: " + remoteMessage.getData());
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            PL.i("Message Notification Body: " + remoteMessage.getNotification().getBody());
//            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//    // [END receive_message]
//
//
//    private void sendNotification(String messageTitle, String messageBody) {
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if(notificationManager == null) {
//            return;
//        }
//
//        PendingIntent pendingIntent = null;
//        String pkName = getApplicationContext().getPackageName();
//        if (!TextUtils.isEmpty(pkName)) {
//            Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkName);
//            if(intent != null) {
//
//// Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                pendingIntent = PendingIntent.getActivity(this, 0, intent,
////                        PendingIntent.FLAG_ONE_SHOT);
//
//                pendingIntent = PendingIntent.getActivity(this,
//                        0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
//            }
//        }
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        int icon = getPushIconId(getApplicationContext());
//        if(TextUtils.isEmpty(messageTitle)) {
//            messageTitle = getApplicationName(this);
//        }
//
////        小图标：必须提供，通过 setSmallIcon() 进行设置。
////        应用名称：由系统提供。
////        时间戳：由系统提供，但您可以使用 setWhen() 替换它或者使用 setShowWhen(false) 隐藏它。
////        大图标：可选内容（通常仅用于联系人照片，请勿将其用于应用图标），通过 setLargeIcon() 进行设置。
////        标题：可选内容，通过 setContentTitle() 进行设置。
////        文本：可选内容，通过 setContentText() 进行设置
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle(messageTitle)
//                .setSmallIcon(icon)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri);
//
//        if (pendingIntent != null) {
//            notificationBuilder.setContentIntent(pendingIntent);
//        }
//
//        if (Build.VERSION.SDK_INT >= 26) {
//
//            // The id of the channel.
//            String id = "nw_notify_channel";
//            // The user-visible name of the channel.
////            String name = context.getApplicationInfo().name;
//            String name = "new message";
//            //范围是 IMPORTANCE_NONE(0) 至 IMPORTANCE_HIGH(4)。默认重要性级别为 3：在所有位置显示，发出提示音，但不会对用户产生视觉干扰。
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//            // Configure the notification channel.
//            mChannel.enableLights(true);
//            // Sets the notification light color for notifications posted to this
//            // channel, if the device supports this feature.
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            notificationManager.createNotificationChannel(mChannel);
//            notificationBuilder.setChannelId(id);
//        }
//
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//
//    public static String getApplicationName(Context context) {
//        ApplicationInfo applicationInfo = context.getApplicationInfo();
//        int stringId = applicationInfo.labelRes;
//        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
//    }
//
////     * 获取推送icon的id
////     * @return 先读取meta-data配置(com.google.firebase.messaging.default_notification_icon)，然后读取应用icon。
//
//
//    public static int getPushIconId(Context context) {
//        int iconId = 0;
//        try {
//            ApplicationInfo appInfoappInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
//                    PackageManager.GET_META_DATA);
//            Bundle metaData = appInfoappInfo.metaData;
//            if(metaData != null) {
//                iconId = metaData.getInt("com.google.firebase.messaging.default_notification_icon");
//            } else {
//                PL.i("当前应用没有在meta-data配置");
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        if(iconId == 0) {
//            ApplicationInfo info = context.getApplicationInfo();
//            iconId = info.icon;
//            PL.i("没有找到推送icon，使用应用icon : " + iconId);
//        }
//        return iconId;
//    }
//
//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        PL.i("fcm token is " + s);
////        GamaAj.setPushToken(getApplicationContext(), s);
//    }
//}
