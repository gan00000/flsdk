package com.mw.sdk.social.bean;

import android.net.Uri;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String userThirdId = "";
    private String name = "";
    private String gender = "";
    private String birthday = "";
    private Uri pictureUri;
    private String tokenForBusiness = "";
    private String accessTokenString = "";

    public String getUserThirdId() {
        return userThirdId;
    }

    public void setUserThirdId(String userThirdId) {
        this.userThirdId = userThirdId;
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

}
