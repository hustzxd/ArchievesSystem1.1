package com.example.hustzxd.archievessystem11.constant;

/**
 * 常量类
 * Created by buxiaoyao on 2016/7/8.
 */
public class Constant {
    public static boolean isLogin = false;
    public static String username;

    private static final String URL_HEAD = "http://202.114.15.107:8080//";

    public static final String URL_LOGIN = URL_HEAD + "mobile_login.action";
    public static final String URL_LOGOUT = URL_HEAD + "logout.action";

    public static final String URL_CHECK_TAG = URL_HEAD + "check_tag_used.action";


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String MOBILE = "mobile";
    public static final String TRUE = "True";
}
