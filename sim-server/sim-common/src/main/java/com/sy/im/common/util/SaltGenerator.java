package com.sy.im.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {

    // 盐值的长度
    public static int SaltLength = 6;
    public static void main(String[] args) {
        String salt = generateSalt();
        System.out.println("Generated salt: " + salt);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SaltLength];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
