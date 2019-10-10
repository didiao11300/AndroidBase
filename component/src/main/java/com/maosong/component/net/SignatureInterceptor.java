package com.maosong.component.net;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.maosong.tools.LogUtil;
import com.maosong.tools.MD5Util;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * create by colin 2018/11/23
 */
public class SignatureInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(addSignature(request));
    }

    private class KeyValueBean implements Comparable<KeyValueBean> {
        String key;
        Object value;

        KeyValueBean(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(@NonNull KeyValueBean o) {
            return key.compareTo(o.key);
        }
    }

    private Request addSignature(Request request) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String salt = "ijihf123kfae";
        ArrayList<KeyValueBean> paramList = new ArrayList<>();
        paramList.add(new KeyValueBean("salt", salt));
        paramList.add(new KeyValueBean("timeStamp", timeStamp));
        if (request.method().equals("POST")) {
            RequestBody requestBody = request.body();
            if (null != requestBody) {
                MediaType mediaType = requestBody.contentType();
                if (null != mediaType && isText(mediaType)) {
                    String bodyString = bodyToString(request); //otherIds=["1b84ae35aa5248d78b6e3e045a81c929","b72c44eea6a9454a9a7948a0f2036d14"]&targetName=""
                    String[] parSpilt = bodyString.split("&");
                    for (String par : parSpilt) {
                        String[] keyValueSpilt = par.split("=");//["otherIds","["1b84ae35aa5248d78b6e3e045a81c929","b72c44eea6a9454a9a7948a0f2036d14"]"]
                        if (keyValueSpilt.length == 2 && keyValueSpilt[1] != null && !keyValueSpilt[1].isEmpty()) {
                            KeyValueBean keyValueBean = new KeyValueBean(keyValueSpilt[0], keyValueSpilt[1]);
                            paramList.add(keyValueBean);
                        }
                    }
                }
            }
        } else if (request.method().equals("GET")) {
            Set<String> keys = request.url().queryParameterNames();
            for (String key : keys) {
                String value = request.url().queryParameter(key);
                if (TextUtils.isEmpty(value))
                    continue;
                KeyValueBean keyValueBean = new KeyValueBean(key, value);
                paramList.add(keyValueBean);
            }
        }

        //对key进行排序
        Collections.sort(paramList);
        //取出排序完的参数，加上眼，和time
        StringBuilder builder = new StringBuilder();
        for (KeyValueBean keyValue : paramList) {
            builder.append(keyValue.key)
                    .append("=")
                    .append(keyValue.value)
                    .append("&");
        }
        String params = builder.toString();
        if (!params.isEmpty()) {
            params = params.substring(0, params.length() - 1);
        }
        LogUtil.e("sign MD5之前 = " + params);
        String sign = MD5Util.encrypt(params.toLowerCase());
        return request.newBuilder()
                .addHeader("timeStamp", timeStamp)
                .addHeader("sign", sign)
                .build();
    }

    private boolean isText(MediaType mediaType) {
        return mediaType.type() != null && mediaType.type().equals("text")
                || mediaType.subtype() != null && (mediaType.subtype().equals("json")
                || mediaType.subtype().equals("xml") || mediaType.subtype().equals("html")
                || mediaType.subtype().equals("webviewhtml") || mediaType.subtype().equals("x-www-form-urlencoded"));
    }

    private String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            if (copy.body() != null) {
                copy.body().writeTo(buffer);
            }
            String message = buffer.readUtf8();
            return URLDecoder.decode(message, "utf-8");
        } catch (final IOException e) {
            e.printStackTrace();
            LogUtil.e("在解析内容时候发生异常");
            return "";
        }
    }

}
