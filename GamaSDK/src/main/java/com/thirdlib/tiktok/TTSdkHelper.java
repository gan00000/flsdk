package com.thirdlib.tiktok;

import android.content.Context;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.bean.SGameBaseRequestBean;
import com.tiktok.TikTokBusinessSdk;
import com.tiktok.appevents.base.EventName;
import com.tiktok.appevents.base.TTBaseEvent;
import com.tiktok.appevents.contents.TTCheckoutEvent;
import com.tiktok.appevents.contents.TTContentParams;
import com.tiktok.appevents.contents.TTContentsEvent;
import com.tiktok.appevents.contents.TTContentsEventConstants;
import com.tiktok.appevents.contents.TTPurchaseEvent;

import java.util.ArrayList;
import java.util.List;

public class TTSdkHelper {

    public static void init(Context context){

        if (context == null){
            return;
        }
        String tt_app_id = context.getString(R.string.mw_tt_app_id);
        if (SStringUtil.isEmpty(tt_app_id)){
            PL.i("tt_app_id is empty");
            return;
        }

        // Set AppId & TikTok App ID in application code
        TikTokBusinessSdk.TTConfig ttConfig = new TikTokBusinessSdk.TTConfig(context.getApplicationContext())
//                .openDebugMode()
//                .setLogLevel(TikTokBusinessSdk.LogLevel.DEBUG)
                .setAppId(context.getPackageName()) // Android package name or iOS listing ID, eg: com.sample.app (from Play Store) or 9876543 (from App Store)
                .setTTAppId(tt_app_id); // TikTok App ID from TikTok Events Manager
        TikTokBusinessSdk.initializeSdk(ttConfig);

        // TikTok business SDK will actually start sending app events to
        // marketing API when startTrack() function is called. Before this
        // function is called, app events are merely stored locally. Delay
        // calling this function if you need to let the user agree to data terms
        // before actually sending the app events to TikTok.
        TikTokBusinessSdk.startTrack();
        PL.i("tt_app_id init finish");
    }

    public static void regUserInfo(Context context, String userId){

        if (!ttAppIdExist(context)){
            return;
        }
        //Should be called whenever the user info changes
        // - when the user logins in
        //- when a new user signs up
        //- when the user updates his/her profile,logout first, then identify - If the user's previous user info is remembered and the app is reopened.
        TikTokBusinessSdk.identify(userId
                ,""
                ,""
                ,"");
    }

    public static void logout(){

        //Should be called when the user logs out
        //- when the app logs out
        // - when switching to another account, in that case, should call a subsequent identify(String, String, String, String)
        TikTokBusinessSdk.logout();
    }
    public static void trackEvent(Context context, String eventName){

        if (!ttAppIdExist(context)){
            return;
        }
        eventName = findTTStandardEventName(eventName);
        //Report custom events
        if (eventName.equals(EventConstant.EventName.DetailedLevel.name())){
            SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
            TTBaseEvent ttBaseEvent = TTBaseEvent.newBuilder(eventName)
                    .addProperty("value", sGameBaseRequestBean.getRoleLevel()).build(); //event constant
            TikTokBusinessSdk.trackTTEvent(ttBaseEvent);
        }else {
            TTBaseEvent ttBaseEvent = TTBaseEvent.newBuilder(eventName).build(); //event constant
            TikTokBusinessSdk.trackTTEvent(ttBaseEvent);
        }
        PL.i("tt_app_id trackEvent finish eventName=" + eventName);
    }

//    enum Currency {
//        AED, ARS, AUD, BDT, BHD, BIF, BOB, BRL, CAD, CHF, CLP, CNY, COP, CRC, CZK, DKK, DZD, EGP,
//        EUR, GBP, GTQ, HKD, HNL, HUF, IDR, ILS, INR, ISK, JPY, KES, KHR, KRW, KWD, KZT, MAD, MOP,
//        MXN, MYR, NGN, NIO, NOK, NZD, OMR, PEN, PHP, PKR, PLN, PYG, QAR, RON, RUB, SAR, SEK, SGD,
//        THB, TRY, TWD, UAH, USD, VES, VND, ZAR
//    }

    public static void trackPay(Context context, String userId, String roleId, String orderId, String productId, double amount, String des){

        if (!ttAppIdExist(context)){
            return;
        }

        //Report using TTContentsEvent
        TTContentsEvent info = TTPurchaseEvent.newBuilder()
                .setDescription(des)//Description of the item or page.
                .setCurrency(TTContentsEventConstants.Currency.USD)//The ISO 4217 currency code.
                .setValue(amount)//Total value of the order basket or items sold.
                .setContents(TTContentParams.newBuilder()//Relevant products in an event with product information.
                        .setContentId(orderId)//Unique ID of the product or content.
                        .setContentCategory("game")//Category of the page or product.
                        .setBrand("mw")//Brand name of the product item.
                        .setPrice((float) amount)//The price of the item.
                        .setQuantity(1)//The number of items.
                        .setContentName(productId).build())//Name of the page or product.
                .setContentType("inapp")//The type of content in the event.
                .build();
        TikTokBusinessSdk.trackTTEvent(info);
        PL.i("tt trackPay finish");

    }
    public static void trackEventRevenue(Context context, String eventName, String userId, String roleId, String orderId, String productId, double amount){

        if (!ttAppIdExist(context)){
            return;
        }

        if (SStringUtil.isEmpty(eventName)){
            return;
        }

        eventName = findTTStandardEventName(eventName);

        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(context);
        //Report custom events
        TTBaseEvent ttBaseEvent = TTBaseEvent.newBuilder(eventName) //event constant
        //If you need to add eventID:
        //TTBaseEvent testInfo = TTBaseEvent.newBuilder(EventName.IN_APP_AD_IMPR.toString(), "<event_id value>")
                .addProperty("currency", TTContentsEventConstants.Currency.USD) //The ISO 4217 currency code
                .addProperty("value", amount) // Value of the order or items sold
                .addProperty("content_id", productId) //Unique ID of the product or content
                .addProperty("content_type", "inapp") //The type of content in the event
//                .addProperty("price", amount) //The price of the item
                .addProperty("quantity", 1) //The number of items
                .addProperty("userId", userId)
                .addProperty("roleId", roleId)
                .addProperty("roleLevel", sGameBaseRequestBean.getRoleLevel())
                .addProperty("orderId", orderId)
                .build();
        TikTokBusinessSdk.trackTTEvent(ttBaseEvent);
        PL.i("tt_app_id trackEventRevenue finish eventName=" + eventName);
    }

    public static void trackCheckout(Context context, String orderId, String productId, double amount){
//        TTCheckoutEvent.newBuilder().
        //Report using TTContentsEvent

        if (!ttAppIdExist(context)){
            return;
        }

        TTContentsEvent info = TTCheckoutEvent.newBuilder()
        .setDescription(productId)//Description of the item or page.
        .setCurrency(TTContentsEventConstants.Currency.USD)//The ISO 4217 currency code.
        .setValue(amount)//Total value of the order basket or items sold.
        .setContents(TTContentParams.newBuilder()//Relevant products in an event with product information.
                .setContentId(orderId)//Unique ID of the product or content.
                .setContentCategory("game")//Category of the page or product.
                .setBrand("mw")//Brand name of the product item.
                .setPrice((float) amount)//The price of the item.
                .setQuantity(1)//The number of items.
                .setContentName(productId).build())//Name of the page or product.
        .setContentType("inapp")//The type of content in the event.
        .build();
        TikTokBusinessSdk.trackTTEvent(info);
        PL.i("tt trackCheckout finish");

    }


    public static boolean ttAppIdExist(Context context){
        String tt_app_id = context.getString(R.string.mw_tt_app_id);
        return SStringUtil.isNotEmpty(tt_app_id);
    }

    private static String findTTStandardEventName(String eventName){
//        List<String> standardEventNames = new ArrayList<>();
//        standardEventNames.add("Complete Tutorial");//用户完成 新手教程/指南/攻略浏览，等游戏内指导用户的过程
//        standardEventNames.add("Registration");
//        standardEventNames.add("Login");
//        standardEventNames.add("Achieve Level");//用户达成等级
//        standardEventNames.add("Create Role");//用户创建游戏角色
//        standardEventNames.add("Checkout");//当用户打开购买页面或单击商品按钮时发生事件
//        standardEventNames.add("Achieve Level");

        String standardEventName = "";
        if (eventName.equals(EventConstant.EventName.START_GUIDE.name())){
            standardEventName = EventName.COMPLETE_TUTORIAL.toString();//"Complete Tutorial";
        }else if (eventName.equals(EventConstant.EventName.REGISTER_SUCCESS.name())){
            standardEventName = EventName.REGISTRATION.toString();//"Registration";
        }else if (eventName.equals(EventConstant.EventName.LOGIN_SUCCESS.name())){
            standardEventName = EventName.LOGIN.toString();//"Login";
        }else if (eventName.equals(EventConstant.EventName.CREATE_ROLE.name())){
            standardEventName = EventName.CREATE_ROLE.toString();//"Create Role";
        }
//        else if (eventName.equals(EventConstant.EventName.Initiate_Checkout.name())){
//            standardEventName = TTContentsEventConstants.ContentsEventName.EVENT_NAME_CHECK_OUT;// "Checkout";
//        }
        else if (eventName.equals("Join_Ally")){
            standardEventName = EventName.JOIN_GROUP.toString();//"JoinGroup";
        }else if (eventName.equals("Purchase_Over9")){//单笔超过9usd
            standardEventName = EventName.UNLOCK_ACHIEVEMENT.toString();//"Unlock Achievement";
        }else if (eventName.equals(EventConstant.EventName.DetailedLevel.name())){
            standardEventName = EventConstant.EventName.DetailedLevel.name();
        }else if (eventName.equals(EventConstant.EventName.AchieveLevel_40.name())){
            standardEventName = EventName.ACHIEVE_LEVEL.toString();
        }
        if (SStringUtil.isEmpty(standardEventName)){
            return eventName;
        }
        return standardEventName;
    }
}
