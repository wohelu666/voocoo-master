package com.voocoo.pet.common.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileImageUpload {

    public static void uploadMultiFile(String image, String url, UploadLinstener uploadLinstener) {
        String imageType = "multipart/form-data";
        File file = new File(image);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatarfile", "avatarfile.jpg", fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", SharedPreferencesUtil.queryValue("token"))
                .post(requestBody)
                .build();
        final OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpBuilder
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uploadLinstener.onFail();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                LogUtil.d("result", str);
                uploadLinstener.onSuccess(str);
            }
        });
    }

    /**
     * Encode image to Base64 string
     *
     * @param srcPath
     * @return Base64 encoded string
     */
    public static String encodeImageToBase64String(String srcPath) {

        Log.d("srcPath:", srcPath);

        String imageString = null;
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(srcPath);
//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                inputStream.close();

                bytes = output.toByteArray();
                imageString = Base64.encodeToString(bytes, 0, bytes.length, Base64.NO_WRAP);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
// TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return imageString;
    }

    public interface UploadLinstener {
        void onSuccess(String ret);

        void onFail();
    }
}
