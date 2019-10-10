package com.maosong.component.net;

import android.text.TextUtils;

import com.maosong.tools.LogUtil;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by tianweiping on 2017/12/11.
 */

public class HttpLogInterceptor implements Interceptor {

    private String tag;
    private boolean showLog;

    public HttpLogInterceptor(String tag, boolean showLog) {
        this.tag = "HTTP#" + tag;
        this.showLog = showLog;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (showLog) {
            logForRequest(request);
        }
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            //===>response log
            if (showLog) {
                LogUtil.e(tag, "********响应日志开始********");
                Response.Builder builder = response.newBuilder();
                Response clone = builder.build();
                LogUtil.e(tag, "url : " + clone.request().url());
                LogUtil.e(tag, "code : " + clone.code());
                if (!TextUtils.isEmpty(clone.message()))
                    LogUtil.e(tag, "message : " + clone.message());
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        if (isText(mediaType)) {
                            String resp = body.string();
//                            String res = Des3.decode(resp);
                            LogUtil.e(tag, "响应内容: " + resp);
                            LogUtil.e(tag, "********响应日志结束********");
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            LogUtil.e(tag, "响应内容 : " + " 发生错误");
                        }
                    }
                }
                LogUtil.e(tag, "********响应日志结束********");
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            LogUtil.e(tag, "========请求日志开始=======");
            LogUtil.e(tag, "请求方式 : " + request.method());
            LogUtil.e(tag, "url : " + url);
            LogUtil.e(tag, "token = : " + request.header("x-access-token"));
            LogUtil.e(tag, "sign = : " + request.header("sign"));
            LogUtil.e(tag, "timeStamp = : " + request.header("timeStamp"));
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    LogUtil.e(tag, "请求内容类别 : " + mediaType.toString());
                    if (isText(mediaType)) {
                        LogUtil.e(tag, "请求内容 : " + bodyToString(request));
                    } else {
                        LogUtil.e(tag, "请求内容 : " + " 无法识别。");
                    }
                }
            }
            LogUtil.e(tag, "========请求日志结束=======");
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType) {
        return mediaType.type() != null && mediaType.type().equals("text")
                || mediaType.subtype() != null && (mediaType.subtype().equals("json")
                || mediaType.subtype().equals("xml") || mediaType.subtype().equals("html")
                || mediaType.subtype().equals("webviewhtml") || mediaType.subtype().equals("x-www-form-urlencoded"));
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            String message = buffer.readUtf8();
//            String sub = message.substring("message=".length());
            String urlsub = URLDecoder.decode(message, "utf-8");
//            return Des3.decode(urlsub);
            return urlsub;
        } catch (final IOException e) {
            return "在解析请求内容时候发生了异常";
        }
    }
}
