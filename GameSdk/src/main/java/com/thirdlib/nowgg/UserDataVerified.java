package com.thirdlib.nowgg;

public class UserDataVerified {
   String iss;
   String sub;
   String aud;
   String exp;
   String iat;
   String auth_time;
   String tokenId;
   String sessionId;
   String scope;

   String email;
   String name;
   String picture;
   String mobile;
   String userId;

   public UserDataVerified(String iss, String sub, String aud, String exp, String iat,
                           String auth_time, String email, String mobile, String userId,
                           String tokenId, String sessionId, String scope, String name, String picture) {
       this.iss = iss;
       this.sub = sub;
       this.aud = aud;
       this.exp = exp;
       this.iat = iat;
       this.auth_time = auth_time;
       this.email = email;
       this.mobile = mobile;
       this.userId = userId;
       this.tokenId = tokenId;
       this.sessionId = sessionId;
       this.scope = scope;
       this.name = name;
       this.picture = picture;
  }

   @Override
   public String toString() {
       return "UserDataVerified{" +
               "iss='" + iss + '\'' +
               ", sub='" + sub + '\'' +
               ", aud='" + aud + '\'' +
               ", exp='" + exp + '\'' +
               ", iat='" + iat + '\'' +
               ", auth_time='" + auth_time + '\'' +
               ", tokenId='" + tokenId + '\'' +
               ", sessionId='" + sessionId + '\'' +
               ", scope='" + scope + '\'' +
               ", email='" + email + '\'' +
               ", name='" + name + '\'' +
               ", picture='" + picture + '\'' +
               ", mobile='" + mobile + '\'' +
               ", userId='" + userId + '\'' +
               '}';
   }
 }