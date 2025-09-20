package com.example.demo.Enum;

import java.util.HashMap;
import java.util.Map;

public enum EnumConfig {
    PASSWORD_CHANGE_SUCCESS, LOGIN_FAIL;


    private static final Map<EnumConfig, String> messages = new HashMap<>();

    static {
        messages.put(PASSWORD_CHANGE_SUCCESS, "Thay đổi mật khẩu thành công");
        messages.put(LOGIN_FAIL, "Đăng nhập thất bại");
    }

    public String getText() {
        return messages.get(this);
    }
}
