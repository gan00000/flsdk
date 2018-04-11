package com.starpy.data.login.execute;

import android.app.Activity;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.starpy.data.login.request.QueryFbToGamaUserIdBean;
import com.starpy.data.login.request.GamaUserRelateFbBean;
import com.starpy.data.login.response.FbUser;
import com.starpy.data.login.response.QueryFbUserResponse;
import com.starpy.data.login.response.SLoginResponse;
import com.starpy.sdk.utils.DialogUtil;
import com.starpy.thirdlib.facebook.FbSp;
import com.starpy.thirdlib.facebook.FriendProfile;
import com.starpy.thirdlib.facebook.SFacebookProxy;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gan on 2018/1/31.
 */

public class QueryFbToGamaUserIdTask {

    private SFacebookProxy sFacebookProxy;
    private SFacebookProxy.RequestFriendsCallBack requestFriendsCallBack;

    private Activity activity;

    private List<FriendProfile> allFriendProfiles = new ArrayList<>();

    public QueryFbToGamaUserIdTask(Activity activity, SFacebookProxy sFacebookProxy, SFacebookProxy.RequestFriendsCallBack requestFriendsCallBack) {
        this.activity = activity;
        this.sFacebookProxy = sFacebookProxy;
        this.requestFriendsCallBack = requestFriendsCallBack;
    }



    public void query(){

        if (sFacebookProxy != null){
            sFacebookProxy.requestMyFriends(activity, new SFacebookProxy.RequestFriendsCallBack() {
                @Override
                public void onError() {
                    PL.i("onError");
                    requestFriendsCallBack.onError();
                }

                @Override
                public void onSuccess(JSONObject graphObject, final List<FriendProfile> friendProfiles) {
                    PL.i("onSuccess:" + graphObject.toString());

                    if (friendProfiles != null) {
                        allFriendProfiles.addAll(friendProfiles);
                    }
                    final GamaUserRelateFbBean sGameBaseRequestBean = new GamaUserRelateFbBean(activity);
                    sGameBaseRequestBean.setRequestUrl("http://access.starpytw.com/");
                    sGameBaseRequestBean.setRequestMethod("userRelateFbAccount");
                    sGameBaseRequestBean.setFbId(FbSp.getFbId(activity));

                    final AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
                        @Override
                        public BaseReqeustBean createRequestBean() {
                            return sGameBaseRequestBean;
                        }
                    };
                    absHttpRequest.setLoadDialog(DialogUtil.createLoadingDialog(activity, "Loading..."));
                    absHttpRequest.setReqCallBack(new ISReqCallBack<SLoginResponse>() {
                        @Override
                        public void success(SLoginResponse sLoginResponse, String rawResult) {
                            //todo 判断是否关联成功

                            if (sLoginResponse != null && sLoginResponse.isRequestSuccess()) {
                                queryFbUser(friendProfiles);

                            }else {
                                if (SStringUtil.isNotEmpty(sLoginResponse.getMessage())) {
                                    ToastUtils.toast(activity,sLoginResponse.getMessage());
                                }
                                requestFriendsCallBack.onError();
                            }

                        }


                        @Override
                        public void timeout(String code) {
                            requestFriendsCallBack.onError();
                        }

                        @Override
                        public void noData() {
                            requestFriendsCallBack.onError();
                        }
                    });
                    absHttpRequest.excute(SLoginResponse.class);
                }

            });
        }

    }

    private void queryFbUser(List<FriendProfile> friendProfiles) {

        if (friendProfiles == null || friendProfiles.isEmpty()){
            reqestInviteFriends();
            return;
        }

        StringBuffer idsStringBuffer = new StringBuffer();
        for (FriendProfile theGameFriendProfile : friendProfiles) {
            idsStringBuffer.append(theGameFriendProfile.getId()).append(",");
        }
        String theGameFbUserId = idsStringBuffer.substring(0, idsStringBuffer.lastIndexOf(","));

        final QueryFbToGamaUserIdBean queryGameBaseRequestBean = new QueryFbToGamaUserIdBean(activity);
        queryGameBaseRequestBean.setRequestUrl("http://access.starpytw.com/");
        queryGameBaseRequestBean.setRequestMethod("queryFbAccountUserId");

        queryGameBaseRequestBean.setFbIds(theGameFbUserId);

        final AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {
                return queryGameBaseRequestBean;
            }
        };
        absHttpRequest.setLoadDialog(DialogUtil.createLoadingDialog(activity, "Loading..."));
        absHttpRequest.setReqCallBack(new ISReqCallBack<QueryFbUserResponse>() {

            @Override
            public void success(QueryFbUserResponse queryFbUserResponse, String rawResult) {

                if (queryFbUserResponse != null && queryFbUserResponse.isRequestSuccess()){

                    List<FbUser> fbUsers = queryFbUserResponse.getFbUsers();
                    if (fbUsers != null && !fbUsers.isEmpty()){
                        for (FbUser fbUser : fbUsers) {

                            String fbUserId = fbUser.getFbUserId();
                            String userId = fbUser.getUserId();//设置 starpy user id
                            if (SStringUtil.isNotEmpty(fbUserId) && SStringUtil.isNotEmpty(userId) && allFriendProfiles != null && !allFriendProfiles.isEmpty()) {
                                for (FriendProfile friendProfile : allFriendProfiles) {
                                    if (fbUserId.equals(friendProfile.getId())){
                                        friendProfile.setUserId(userId);
                                        break;
                                    }
                                }
                            }


                        }
                    }

                }

                reqestInviteFriends();
            }

            @Override
            public void timeout(String code) {
                requestFriendsCallBack.onError();
            }

            @Override
            public void noData() {
                requestFriendsCallBack.onError();
            }
        });

        absHttpRequest.excute(QueryFbUserResponse.class);
    }


    private void reqestInviteFriends() {

        sFacebookProxy.requestInviteFriends(activity, null, new SFacebookProxy.RequestFriendsCallBack() {
            @Override
            public void onError() {
                requestFriendsCallBack.onError();
            }

            @Override
            public void onSuccess(JSONObject graphObject, List<FriendProfile> inviteFriendProfiles) {
                if (inviteFriendProfiles != null) {
                    allFriendProfiles.addAll(inviteFriendProfiles);
                }
                requestFriendsCallBack.onSuccess(new JSONObject(),allFriendProfiles);
            }
        });
    }

}
