package com.mw.sdk.login.model;

import java.io.Serializable;


public class QooAppLoginModel implements Serializable {
//    {"code":200,"message":"success","data":{"user_id":"84872740","name":"甘甘","avatar":"https:\/\/img.qoo-img.com\/common\/202108\/11\/202108\/Rl4zZwLOBYmIylQ4DsMIRahSiUEhk6qX.png",
//            "is_anonymous":false,"purchased":false},
//        "signature":"WiPfD2vnBkI+fy8JoFe8EYYyXvJ8KLJVIOkKWAOjooH1pz09hQQ0JCARj7m8RUELT4H+C6EAbut\/EST9xResdhLwwleDNP6wEo+nZFU5a0mmI0jO9rDLIjsU5ZcC8CASbN9w4KgBzgLrITTqpgk+rr9aXc5cUrlLWvoBfbl3bx7O9nv0+Z\/b1eFmY7HJwxRZEsnoAIdMI7rVlhIaAKzIHFDkms5t01ti6z1RAWL\/0K5mL8+Nk4X1yhWsF5lU31XU2rG9WJOPo7sRPKt3FiFim7wa55u9rpMU2H6rkbLV+WOw9dSX0x6cjeGliLOIXK\/wLV0vwfXdrGQV45rNlrTM3Q==",
//            "algorithm":"sha256"}
    private int code;
    private String message;
    private String signature;
    private String algorithm;

    private UserData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public static class UserData{
        private String user_id;
        private String name;
        private String avatar;
        private Boolean is_anonymous;
        private Boolean purchased;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Boolean getIs_anonymous() {
            return is_anonymous;
        }

        public void setIs_anonymous(Boolean is_anonymous) {
            this.is_anonymous = is_anonymous;
        }

        public Boolean getPurchased() {
            return purchased;
        }

        public void setPurchased(Boolean purchased) {
            this.purchased = purchased;
        }
    }
}
