package com.example.nhahang.Utils;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordUtil {

    // Hàm này mã hóa mật khẩu và trả về chuỗi mã hóa
    public static String hashPassword(String plainTextPassword) {
        // Tạo một salt ngẫu nhiên để bảo mật hơn
        String salt = BCrypt.gensalt(12); // 12 là số lượt mã hóa

        // Sử dụng BCrypt để mã hóa mật khẩu

        return BCrypt.hashpw(plainTextPassword, salt);
    }

    // Hàm này kiểm tra mật khẩu đúng hay không bằng cách so sánh với chuỗi mã hóa đã lưu trữ
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

}
