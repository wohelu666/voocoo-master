package com.voocoo.pet.common.utils;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

public class AliyunUploadFile {

    private OSSCredentialProvider credentialProvider;
    private ClientConfiguration conf;
    private OSS oss;

    /**
     * @param context        上下文
     * @param AccessKeyId    存取键 一般找你们后端拿
     * @param SecretKeyId    密钥 一般找你们后端拿
     * @param SecurityToken  安全符记 一般找你们后端拿
     * @param endpoint       端点 一般找你们后端拿
     * @param bucketName     桶名 一般找你们后端拿
     * @param objectName     文件名
     * @param uploadFilePath 文件路径
     */
    public void UploadFile(Context context, String AccessKeyId, String SecretKeyId, String SecurityToken
            , String endpoint, final String bucketName, final String objectName, String uploadFilePath, final UploadListener uploadListener) {

        OSSStsTokenCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(AccessKeyId, SecretKeyId, SecurityToken);
        conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        oss = new OSSClient(context, endpoint, credentialProvider, conf);

        // 构造上传请求。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, uploadFilePath);

        // 异步上传时可以设置进度回调。
        putObjectRequest.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }
        });

        OSSAsyncTask ossAsyncTask = oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                LogUtil.d("onSuccess");
                uploadListener.UploadSuccess(oss.presignPublicObjectURL(bucketName, objectName));
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                LogUtil.d("onFailure");
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                    LogUtil.e(clientExcepion + "");
                    uploadListener.Uploaddefeated("网络异常");
                }

                if (serviceException != null) {
                    // 服务异常。
                    LogUtil.e(serviceException + "");
                    uploadListener.Uploaddefeated("服务异常");
                }
            }
        });

        // ossAsyncTask.cancel(); // 可以取消任务
        // ossAsyncTask.waitUntilFinished(); // 等待任务完成
    }

    public interface UploadListener {
        void UploadSuccess(String url);

        void Uploaddefeated(String exception);
    }
}