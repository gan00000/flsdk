package com.gama.sdk.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class GamaFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageTitle FCM message title received.
     * @param messageBody  FCM message body received.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager == null) {
            return;
        }

        PendingIntent pendingIntent = null;
        String pkName = getApplicationContext().getPackageName();
        if (!TextUtils.isEmpty(pkName)) {
            Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkName);
            if(intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
            }
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int icon = getPushIconId(getApplicationContext());
        if(TextUtils.isEmpty(messageTitle)) {
            messageTitle = getApplicationName(this);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(messageTitle)
                .setSmallIcon(icon)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        if (Build.VERSION.SDK_INT >= 26) {

            // The id of the channel.
            String id = "baplay_notify_channel";
            // The user-visible name of the channel.
//            String name = context.getApplicationInfo().name;
            String name = "new message";
            //范围是 IMPORTANCE_NONE(0) 至 IMPORTANCE_HIGH(4)。默认重要性级别为 3：在所有位置显示，发出提示音，但不会对用户产生视觉干扰。
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder.setChannelId(id);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    /**
     * 获取推送icon的id
     * @return 先读取meta-data配置(com.google.firebase.messaging.default_notification_icon)，然后读取应用icon。
     */
    public static int getPushIconId(Context context) {
        int iconId = 0;
        try {
            ApplicationInfo appInfoappInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle metaData = appInfoappInfo.metaData;
            if(metaData != null) {
                iconId = metaData.getInt("com.google.firebase.messaging.default_notification_icon");
            } else {
                Log.i(TAG, "当前应用没有在meta-data配置");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(iconId == 0) {
            ApplicationInfo info = context.getApplicationInfo();
            iconId = info.icon;
            Log.i(TAG, "没有找到推送icon，使用应用icon : " + iconId);
        }
        return iconId;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(TAG, "fcm token is " + s);
    }
}
