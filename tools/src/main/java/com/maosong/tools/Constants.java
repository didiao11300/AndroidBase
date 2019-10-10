package com.maosong.tools;

import android.os.Environment;

import com.amazonaws.services.s3.model.Region;

//常量类
public interface Constants {
    //图片压缩路径
    String photoCompressDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FourOneNineCache";

    String SP_USER_ID = "userId";
    String SP_TOKEN = "_token";
    String SP_COUNTRY_CODE = "_country_code";
    String SP_COUNTRY_NAME = "_country_name";
    String SP_PUSHID = "pushID";
    String KEY_DEBUG = "_debug";
    String SP_USER_NAME = "_name";
    String SP_USER_HEAD = "_head";
    //存储的订单id
    String SP_PAY_ORDER_ID = "_order_id";
    //存储支付唯一的验证
    String SP_PAY_UNIQUE_ID = "_order_unique_id";
    //最后一次支付的类型
    String SP_LAST_PAY_TYPE = "_order_pay_type";
    String PAY_TYPE_GOOGLEPAY = "googlePay";
    String PAY_TYPE_PINGPONG = "pingpong";
    String PAY_TYPE_PAYPAL = "payPal";

    //上一次分享得票的时间
    String SP_LAST_SHARE_PICK = "_sp_last_share_pick";
    //上一次登录时间
    String SP_LAST_LOGIN = "_sp_last_login";


    String AWS_PIC_URL = "https://s3-ap-southeast-1.amazonaws.com/jfy2018/";
    String BUCKET_NAME = "jfy2018";
    String BUCKET_REGION = Region.AP_Singapore.toString();
    String COGNITO_POOL_ID = "ap-southeast-1:677bbdc5-06be-4378-8eca-010b9911a1cd";
    String COGNITO_POOL_REGION = Region.AP_Singapore.toString();
    String CHANNEL_ID_PUSH = "channel_id_push";
}
