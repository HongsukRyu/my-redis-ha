package com.backend.api.common.object;

public interface Const {

    int USER_TYPE_USER = 1;
    int USER_TYPE_ADMIN = 3;

    String ROLE_USER = "user";
    String ROLE_ADMIN = "admin";

    String ACCOUNT_USER_ROLE = "user";
    String ACCOUNT_ADMIN_ROLE = "admin";

    //	로그인 성공 / 실패
    int LOGIN_SUCCESS = 0;
    String SUCCESS = "SUCCESS";
    String FAIL = "FAIL";
    String ALREADY = "ALREADY";

    String DUPLICATE = "DUPLICATE";
    String UNAUTHORIZED = "Unauthorized";
}

