package com.example.demo.Enum;

import java.util.HashMap;
import java.util.Map;

public enum EnumConfig {
    PASSWORD_CHANGE_SUCCESS, LOGIN_FAIL, VALIDATE, LOGOUT_MESSAGE, SUCCESS, LOCK, UNLOCK, FAIL, ACCESS_DENIED, ERROR,
    TOKEN_FAIL, TOKEN_RECALL, INVALID_TOKEN, NOT_FOUND_USER, USER_EXPIRED;


    private static final Map<EnumConfig, String> messages = new HashMap<>();

    static {
        messages.put(PASSWORD_CHANGE_SUCCESS, "Thay đổi mật khẩu thành công");
        messages.put(LOGIN_FAIL, "Đăng nhập thất bại");
        messages.put(VALIDATE, "Vui lòng nhập đầy đủ thông tin");
        messages.put(LOGOUT_MESSAGE, "Đăng xuất thành công");
        messages.put(SUCCESS, "SUCCESS");
        messages.put(UNLOCK, "Mở khóa thành công");
        messages.put(LOCK, "Khóa thành công");
        messages.put(FAIL, "Fail");
        messages.put(ACCESS_DENIED, "Access Denied");
        messages.put(ERROR, "Error");
        messages.put(TOKEN_FAIL, "Invalid or missing token");
        messages.put(TOKEN_RECALL, "User đã bị khóa");
        messages.put(INVALID_TOKEN, "Invalid token");
        messages.put(NOT_FOUND_USER, "User không tồn tại!");
        messages.put(USER_EXPIRED, "Tài khoản đã hết hạn!");


    }

    public String getText() {
        return messages.get(this);
    }
}
