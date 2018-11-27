package com.gama.thirdlib.facebook;

import android.net.Uri;

public class FaceBookUser {
    private String userId;

    private  String name;

    private String gender;
    private String birthday;
    private Uri pictureUri;

    private String tokenForBusiness;
    private String accessTokenString;
    private String facebookAppId;
    private String businessId;

    public String getUserFbId() {
        return userId;
    }

    public void setUserFbId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getTokenForBusiness() {
        return tokenForBusiness;
    }

    public void setTokenForBusiness(String tokenForBusiness) {
        this.tokenForBusiness = tokenForBusiness;
    }

    public String getAccessTokenString() {
        return accessTokenString;
    }

    public void setAccessTokenString(String accessTokenString) {
        this.accessTokenString = accessTokenString;
    }

    public String getFacebookAppId() {
        return facebookAppId;
    }

    public void setFacebookAppId(String facebookAppId) {
        this.facebookAppId = facebookAppId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return "FaceBookUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", pictureUri=" + pictureUri +
                ", tokenForBusiness='" + tokenForBusiness + '\'' +
                ", accessTokenString='" + accessTokenString + '\'' +
                ", facebookAppId='" + facebookAppId + '\'' +
                ", businessId='" + businessId + '\'' +
                '}';
    }
}
