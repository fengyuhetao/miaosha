package com.ht.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by hetao on 2018/12/27.
 */
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String saltDB) {
        String str = md5(formPass + salt);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String saltDB) {
        return formPassToDBPass(inputPassToFormPass(inputPass), saltDB);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "haha"));
        System.out.println(inputPassToDBPass("123456", "haha"));
    }
}
