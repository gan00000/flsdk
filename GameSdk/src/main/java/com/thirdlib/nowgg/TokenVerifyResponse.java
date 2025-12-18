package com.thirdlib.nowgg;

import com.google.gson.annotations.SerializedName;

public class TokenVerifyResponse {
        boolean success;
        String code;
        String msg;
        @SerializedName("decodedData")
        UserDataVerified userDataVerified;

        public TokenVerifyResponse(boolean success, String code, String msg, UserDataVerified userDataVerified) {
            this.success = success;
            this.code = code;
            this.msg = msg;
            this.userDataVerified = userDataVerified;
        }

        public UserDataVerified getUserDataVerified() {
            return userDataVerified;
        }

        public boolean isSuccess() {
            return success;
        }
        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "TokenVerifyResponse{" +
                    "success=" + success +
                    ", code='" + code + '\'' +
                    ", msg='" + msg + '\'' +
                    ", userDataVerified=" + userDataVerified +
                    '}';
        }
    }