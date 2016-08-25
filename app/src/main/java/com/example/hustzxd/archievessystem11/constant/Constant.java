package com.example.hustzxd.archievessystem11.constant;

/**
 * 常量类
 * Created by buxiaoyao on 2016/7/8.
 */
public class Constant {
    public static String username;//当前用户名

    public static boolean isLogin = false;

    private static final String URL_HEAD = "http://202.114.15.92:80/php_project/";

    public static final String URL_LOGIN = URL_HEAD + "m_login.php";
    public static final String URL_RECEIVE_TWO = URL_HEAD + "m_receive.php";
    public static final String URL_RECEIVE_ONE = URL_HEAD + "m_receive_1.php";
    public static final String URL_LOAN = URL_HEAD + "m_loan.php";
    public static final String URL_BACK = URL_HEAD + "m_back.php";
    public static final String URL_DESTROY = URL_HEAD + "m_destroy.php";

    public static final String URL_GETINFO = URL_HEAD + "m_getInfo.php";


    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
}
