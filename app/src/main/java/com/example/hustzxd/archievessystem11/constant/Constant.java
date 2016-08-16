package com.example.hustzxd.archievessystem11.constant;

/**
 * 常量类
 * Created by buxiaoyao on 2016/7/8.
 */
public class Constant {
    public static String username;//当前用户名

    public static boolean isLogin = false;

    private static final String URL_HEAD = "http://202.114.15.113:8081/php_project/";

    public static final String URL_LOGIN = URL_HEAD + "login.php";
    public static final String URL_LOGOUT = URL_HEAD + "logout.action";

    public static final String URL_SEND_TAG = URL_HEAD + "send_tag.action";


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String MOBILE = "mobile";
    public static final String TRUE = "True";
}
