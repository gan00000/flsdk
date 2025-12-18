package com.thirdlib.nowgg;

public class TokenVerifyRequest {
        String token_type;
        String token;
        String client_id;

        public TokenVerifyRequest(String token_type, String token, String client_id) {
            this.token_type = token_type;
            this.token = token;
            this.client_id = client_id;
        }
    }