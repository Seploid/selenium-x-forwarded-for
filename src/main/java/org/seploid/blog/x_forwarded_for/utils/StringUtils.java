package org.seploid.blog.x_forwarded_for.utils;

import java.util.Random;

public class StringUtils {

    public static String generateRandomString(String prefix) {
        return prefix + generateRandomString(6);
    }

    public static String generateRandomString(final int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = (char)(r.nextInt((int)(Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }
}
