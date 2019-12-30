package com.dome.push.net;


import com.dome.push.base.BaseUrl;
import com.dome.push.base.IApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * ProjectName:    Bicycle
 * Package:        com.chetianxia.yundanche.utils
 * ClassName:      NetRequest
 * Description:    描述
 * Author:         张继
 * CreateDate:     19-10-19 下午8:25
 * UpdateUser:     更新者
 * UpdateDate:     19-10-19 下午8:25
 * UpdateRemark:   更新说明
 * Version:        1.0
 */
public final class NetRequest {
    private static NetRequest request;
    private OkHttpClient client;
    private Retrofit retrofit;
    private static final int REQUEST_TIME_OUT = 45;


    public static NetRequest getInstance() {
        if (request == null) {
            synchronized (NetRequest.class) {
                if (request == null) {
                    request = new NetRequest();
                }
            }
        }
        return request;
    }

    private NetRequest() {
        client = getClient();
        retrofit = getRetrofit();
    }

    /**
     * 初始化 Retrofit 实例对象
     *
     * @return Retrofit 实例对象
     */
    private Retrofit getRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BaseUrl.BASE_URL);
        builder.client(client);
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder.build();
    }

    /**
     * 初始化 okHttpClient实例对象
     *
     * @return OkHttpClient
     */
    private OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(s -> {
            //ToolUtil.logI("NetRequest",s);
        });
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        builder.connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS);
        builder.pingInterval(REQUEST_TIME_OUT, TimeUnit.SECONDS);
        builder.followRedirects(false);
        return builder.build();
    }

    /**
     * 请求接口
     *
     * @return 返回IApiService
     */
    public IApiService getService() {
        return retrofit.create(IApiService.class);
    }
}
