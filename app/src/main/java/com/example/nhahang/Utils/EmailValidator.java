package com.example.nhahang.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    // Biểu thức chính quy để kiểm tra địa chỉ email
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@(.+)$";

    // Kiểm tra xem email có đúng định dạng hay không
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
