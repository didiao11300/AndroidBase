package com.maosong.component.net;

import com.maosong.tools.AbsStaticConstants;
import com.maosong.tools.Constants;
import com.maosong.tools.Environment;
import com.maosong.tools.SPUtils;
import com.maosong.tools.ToolsApp;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tianweiping on 2017/12/11.
 */
public class AbsBuildAPI {
    private static Retrofit headerRetrofit;

    /**
     * 构造有自定义header字段token的Retrofi
     *
     * @return APIServers
     */
    public static <T> T buildApiServers(Class<T> service) {
        if (headerRetrofit == null) {
            headerRetrofit = new Retrofit.Builder().baseUrl(Environment.BASE_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    //这个用来发起网络请求
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getTokenClient())
                    .build();
        }
        return headerRetrofit.create(service);
    }

    public static void clear() {
        headerRetrofit = null;
    }

    /**
     * 添加自定义header
     *
     * @return 自定义的OkHttpClient
     */
    private static OkHttpClient getTokenClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .addHeader("Accept", "application/json;charset=UTF-8")
                                //请求服务器的api版本号码
                                .addHeader("api-version", AbsStaticConstants.API_VERSION)
                                //请求服务器的app本地的版本号码
                                .addHeader("app-version",AbsStaticConstants.APP_VERSION)
                                .addHeader("x-access-token", SPUtils.getInstance().getString(Constants.SP_TOKEN, ""))
                                .addHeader("device", "android")
                                .build();
                        return chain.proceed(request);
                    }
                })
                // FIXME: 18-12-10 
                .addNetworkInterceptor(new SignatureInterceptor())
                .addNetworkInterceptor(new HttpLogInterceptor("httpLog", AbsStaticConstants.IS_DEBUG))
                .addInterceptor(new ResponseInterceptor())
                .addInterceptor(new ChuckInterceptor(ToolsApp.getAppContext()))
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    //构建请求体
//    private static RequestBody buildBody(String object) {
//        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object);
//    }

//    @NonNull
//    public static RequestBody buildRequestBody(Object object) {
//        String req = gson.toJson(object);
//        return Des3.encode(req);
//
//        return buildBody(req);
//    }

}
