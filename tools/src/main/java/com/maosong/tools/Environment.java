package com.maosong.tools;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class Environment {

    /**
     * 存储环境的 key
     */
    public static final String KEY_ENVIRONMENT = "_env";
    /**
     * online 正式环境标识
     */
    public static final String ONLINE = "ONLINE";
    /**
     * prod prod预上线环境标识
     */
    public static final String PROD = "PROD";
    /**
     * rd 环境标识
     */
    public static final String RD = "RD";
    /**
     * 存储环境标识
     */
    public static final List<String> ENV_S = new ArrayList<>();

    /**
     * online 环境的 host
     */
    private static final String ONLINE_HOST = "https://www.trulylist.com:9989/";
    /**
     * prod 环境的 host
     */
    private static final String PROD_HOST = "http://18.136.167.155:8080/date/";

    /**
     * rd 环境的 host
     */
    private static final String RD_HOST = "http://192.168.31.197:9989";
//    private static final String RD_HOST = "http://192.168.1.3:9989/";

    private static final String WEB_SOCKET_URL_RD = "http://120.78.81.36:9993/";
    //    public static final String WEB_SOCKET_URL_RD = "http://192.168.31.8:9993/";
    private static final String WEB_SOCKET_URL_ONLINE = "http://13.228.250.160:9993/";
    private static final String WEB_SOCKET_URL_PROD = "http://13.228.250.160:9993/";

    //服务地址和隐私协议和帮助中心的默认值，到时候会通过someInfo的那个接口重新获取
    public static String SERVICE_AGREEMENT_URL = "https://www.trulylist.com/mm/#/serviceAgreement";
    public static String PRIVACY_AGREEMENT_URL = "https://www.trulylist.com/mm/#/privacyAgreement";
    public static String HELP_CENTER_URL = "http://120.78.81.36:8080/dh/#/wap/wapHelp";
    public static String APP_SHARE_URL = "http://120.78.81.36:9989/api/index/shareApp";

    static {
        ENV_S.add(ONLINE);
        ENV_S.add(PROD);
        ENV_S.add(RD);
    }

    /**
     * 默认使用线上环境
     */
    public static String BASE_HOST = TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? RD_HOST : ONLINE_HOST;
    /**
     * 默认使用线上环境
     */
    public static String WEB_SOCKET_URL = TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? WEB_SOCKET_URL_RD : WEB_SOCKET_URL_ONLINE;

//    static {
//        chooseBaseHost();
//    }

    /**
     *
     * */
    public static void chooseBaseHost() {
        String env = SPUtils.getInstance().getString(Environment.KEY_ENVIRONMENT, "");
        switch (env) {
            case Environment.ONLINE:
                BASE_HOST = Environment.ONLINE_HOST;
                WEB_SOCKET_URL = WEB_SOCKET_URL_ONLINE;
                break;
            case Environment.PROD:
                BASE_HOST = Environment.PROD_HOST;
                WEB_SOCKET_URL = WEB_SOCKET_URL_PROD;
                break;
            case Environment.RD:
                BASE_HOST = Environment.RD_HOST;
                WEB_SOCKET_URL = WEB_SOCKET_URL_RD;
                break;
            default:
                BASE_HOST = TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? RD_HOST : ONLINE_HOST;
                WEB_SOCKET_URL = TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? WEB_SOCKET_URL_RD : WEB_SOCKET_URL_ONLINE;
                SPUtils.getInstance().put(Environment.KEY_ENVIRONMENT, (TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? RD : ONLINE));
                break;
        }
    }
}
