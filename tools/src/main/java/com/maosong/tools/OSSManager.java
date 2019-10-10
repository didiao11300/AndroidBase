package com.maosong.tools;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.io.File;

/**
 * region: "oss-cn-shenzhen.aliyuncs.com",
 * secure: true,
 * endpoint: 'oss-cn-shenzhen.aliyuncs.com',
 * accessKeyId: "LTAICMG13T89a5ZK",
 * accessKeySecret: "d1dReZE1A0qMVKk47CIAw1OHy6ErGw",
 * bucket: 'jfy2018'
 */
public class OSSManager {

    private static OSSManager mInstance;
    private OSSClient ossClient;
//    private final String region = "oss-cn-shenzhen.aliyuncs.com";
//    private final boolean secure = true;

    private OSSManager(Context context) {
        init(context);
    }

    public static OSSManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new OSSManager(context);
        }
        return mInstance;
    }

    private void init(Context context) {
        String accessKeyId = "LTAIdssUAlocyMLJ";
        String accessKeySecret = "ZE3pz4rpQ97Uz29B7d5aHVv41hCGcR";
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, "");
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(3); // 失败后最大重试次数
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        ossClient = new OSSClient(context, endpoint, credentialProvider);
    }

    public String upLoadImgSync(File file) throws ClientException, ServiceException {
        // 构造上传请求
        String bucket = "yt-bullet";
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, "userHead/" + file.getName(), file.getAbsolutePath());
        ossClient.putObject(put);
        return getFileUrl("userHead/", file.getName());
    }


    public String upLoadRecordSync(File file) throws ClientException, ServiceException {
        // 构造上传请求
        String bucket = "yt-bullet";
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, "leaderRecord/" + file.getName(), file.getAbsolutePath());
        ossClient.putObject(put);
        return getFileUrl("leaderRecord/", file.getName());
    }

    public String upLoadVideoSync(File file) throws ClientException, ServiceException {
        // 构造上传请求
        String bucket = "yt-bullet";
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, "video/" + file.getName(), file.getAbsolutePath());
        ossClient.putObject(put);
        return getFileUrl("video/", file.getName());
    }

    private String getFileUrl(String prex, String fileName) {
        String fileUrlFix = "https://yt-bullet.oss-cn-shenzhen.aliyuncs.com/" + prex;
        return fileUrlFix + fileName;
    }
}
