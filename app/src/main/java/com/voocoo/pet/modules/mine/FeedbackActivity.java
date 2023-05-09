package com.voocoo.pet.modules.mine;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.voocoo.pet.BuildConfig;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.utils.AliyunUploadFile;
import com.voocoo.pet.common.utils.CommonUtil;
import com.voocoo.pet.common.utils.FileImageUpload;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.AliSts;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Pet;
import com.voocoo.pet.entity.UploadHeadEntity;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;
import com.hss01248.dialog.StyledDialog;

import com.hss01248.dialog.interfaces.MyItemDialogListener;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.os.Environment.DIRECTORY_PICTURES;

public class FeedbackActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_img1)
    ImageView ivImg1;
    @BindView(R.id.iv_img2)
    ImageView ivImg2;
    @BindView(R.id.iv_img3)
    ImageView ivImg3;
    @BindView(R.id.iv_img4)
    ImageView ivImg4;

    @BindView(R.id.iv_del1)
    ImageView ivDel1;
    @BindView(R.id.iv_del2)
    ImageView ivDel2;
    @BindView(R.id.iv_del3)
    ImageView ivDel3;
    @BindView(R.id.iv_del4)
    ImageView ivDel4;

    @BindView(R.id.btn_commit)
    Button btnCommit;

    @BindView(R.id.et_feekback_text)
    EditText etFeekBackText;

    private String img1Url, img2Url, img3Url, img4Url;

    private int imgSelectIndex = 0;
    private AppDialog appDialog;

    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    private Uri imageUri;//相机拍照图片保存地址
    private Uri outputUri;//裁剪万照片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪

    public void setLoginBtnEnabled(boolean isEnabled) {
        if (isEnabled) {
            btnCommit.setEnabled(true);
            btnCommit.setBackgroundResource(R.drawable.bg_btn_able);
        } else {
            btnCommit.setEnabled(false);
            btnCommit.setBackgroundResource(R.drawable.bg_btn_unable);
        }
    }

    private void setUi() {
        if (TextUtils.isEmpty(img1Url)) {
            ivDel1.setVisibility(View.GONE);
            ivImg1.setImageResource(R.mipmap.pic_upload_btn);
            ivImg2.setVisibility(View.GONE);
        } else {
            ivDel1.setVisibility(View.VISIBLE);
            ivImg2.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(img2Url)) {
            ivDel2.setVisibility(View.GONE);
            ivImg2.setImageResource(R.mipmap.pic_upload_btn);
            ivImg3.setVisibility(View.GONE);
        } else {
            ivDel2.setVisibility(View.VISIBLE);
            ivImg3.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(img3Url)) {
            ivDel3.setVisibility(View.GONE);
            ivImg3.setImageResource(R.mipmap.pic_upload_btn);
            ivImg4.setVisibility(View.GONE);
        } else {
            ivImg4.setVisibility(View.VISIBLE);
            ivDel3.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(img4Url)) {
            ivDel4.setVisibility(View.GONE);
            ivImg4.setImageResource(R.mipmap.pic_upload_btn);
        } else {
            ivDel4.setVisibility(View.VISIBLE);
        }
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                UploadHeadEntity result = new Gson().fromJson((String) msg.obj, new TypeToken<UploadHeadEntity>() {
                }.getType());
                dismissLoading();
                if (result.getCode() == 200) {
                    if (imgSelectIndex == 0) {
                        img1Url = result.getImgUrlList().get(0);
                    } else if (imgSelectIndex == 1) {
                        img2Url = result.getImgUrlList().get(0);
                    } else if (imgSelectIndex == 2) {
                        img3Url = result.getImgUrlList().get(0);
                    } else if (imgSelectIndex == 3) {
                        img4Url = result.getImgUrlList().get(0);
                    }
                    setUi();
                } else {
                    showToast(result.getMsg());
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyledDialog.init(this);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_feedback));

        etFeekBackText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    setLoginBtnEnabled(false);
                } else {
                    setLoginBtnEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    setLoginBtnEnabled(false);
                } else {
                    setLoginBtnEnabled(true);
                }
            }
        });

        setUi();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }

    @OnClick({R.id.iv_del1, R.id.iv_del2, R.id.iv_del3, R.id.iv_del4, R.id.iv_img1, R.id.iv_img2, R.id.iv_img3, R.id.iv_img4, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_del1:
                img1Url = null;
                setUi();
                break;
            case R.id.iv_del2:
                img2Url = null;
                setUi();
                break;
            case R.id.iv_del3:
                img3Url = null;
                setUi();
                break;
            case R.id.iv_del4:
                img4Url = null;
                setUi();
                break;
            case R.id.btn_commit:
                if (TextUtils.isEmpty(etFeekBackText.getText().toString())) {
                    showToast(getString(R.string.text_hint_feedback));
                    return;
                }

                List<String> imgUrl = new ArrayList<>();
                if (!TextUtils.isEmpty(img1Url)) {
                    imgUrl.add(img1Url);
                }
                if (!TextUtils.isEmpty(img2Url)) {
                    imgUrl.add(img2Url);
                }
                if (!TextUtils.isEmpty(img3Url)) {
                    imgUrl.add(img3Url);
                }
                if (!TextUtils.isEmpty(img4Url)) {
                    imgUrl.add(img4Url);
                }
                showLoading();
                HttpManage.getInstance().feedback(etFeekBackText.getText().toString(), imgUrl, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d(response);
                        showToast(getString(R.string.text_feedback_success));
                        dismissLoading();
                        finish();
                    }
                });
                break;
            case R.id.iv_img1:
                imgSelectIndex = 0;
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_picture_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 1) {
                            //动态权限
                            if (ContextCompat.checkSelfPermission(FeedbackActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                            } else {
                                openCamera();//打开相机ø
                            }
                        } else if (position == 0) {
                            pickFromGalary();
                        }
                    }
                }).show();
                break;
            case R.id.iv_img2:
                imgSelectIndex = 1;
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_picture_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 1) {
                            //动态权限
                            if (ContextCompat.checkSelfPermission(FeedbackActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                            } else {
                                openCamera();//打开相机ø
                            }
                        } else if (position == 0) {
                            pickFromGalary();
                        }
                    }
                }).show();
                break;
            case R.id.iv_img3:
                imgSelectIndex = 2;
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_picture_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 1) {
                            //动态权限
                            if (ContextCompat.checkSelfPermission(FeedbackActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                            } else {
                                openCamera();//打开相机ø
                            }
                        } else if (position == 0) {
                            pickFromGalary();
                        }
                    }
                }).show();
                break;
            case R.id.iv_img4:
                imgSelectIndex = 3;
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_picture_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 1) {
                            //动态权限
                            if (ContextCompat.checkSelfPermission(FeedbackActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                            } else {
                                openCamera();//打开相机ø
                            }
                        } else if (position == 0) {
                            pickFromGalary();
                        }
                    }
                }).show();
                break;
        }
    }

    private void openCamera() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(FeedbackActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //虽然getExternalStoragePublicDirectory方法被淘汰了，但是不影响使用
            file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "crop_image.jpg");
        } else {
            file = new File(getExternalCacheDir(), "crop_image.jpg");
        }
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        //intent.putExtra("outputX", 400);
        //intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }

    private void pickFromGalary() {
        //动态权限
        if (ContextCompat.checkSelfPermission(FeedbackActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            selectFromAlbum();//打开相册
        }
    }

    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    public void postPhotoError() {
        AppDialog.doubleTextOneButton(FeedbackActivity.this, getString(R.string.post_photo_error_title), getString(R.string.post_photo_error_content)).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    showToast(getString(R.string.text_denied_permission));
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    showToast(getString(R.string.text_denied_permission));
                }
                break;
            default:
        }
    }

    //图片结果处理方法
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO://拍照
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);//裁剪图片
                }
                break;
            case CHOOSE_PHOTO://打开相册
                // 判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {
                        final String pic_path = outputUri.getPath();
                        String targetPath = getExternalCacheDir() + "compressPic.jpg";
                        //调用压缩图片的方法，返回压缩后的图片path
                        final String compressImage = compressImage(pic_path, targetPath, 30);
                        final File compressedPic = new File(compressImage);
                        if (compressedPic.exists()) {
                            LogUtil.d("图片压缩上传");
                            uploadPhoto(compressedPic.getPath());
                        } else {//直接上传
                            uploadPhoto(pic_path);
                        }
                    } else {
                        final String pic_path = imagePath;
                        String targetPath = getExternalCacheDir() + "compressPic.jpg";
                        //调用压缩图片的方法，返回压缩后的图片path
                        final String compressImage = compressImage(pic_path, targetPath, 30);
                        final File compressedPic = new File(compressImage);
                        if (compressedPic.exists()) {
                            LogUtil.d("图片压缩上传");
                            uploadPhoto(compressedPic.getPath());
                        } else {//直接上传
                            uploadPhoto(pic_path);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public static String compressImage(String filePath, String targetPath, int quality) {
        Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(targetPath);
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                //outputFile.createNewFile();
            } else {
                outputFile.delete();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (Exception e) {
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 获取照片角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        if (data == null) {
            return;
        }
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        cropPhoto(uri);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }

    private void uploadPhoto(final String filePath) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (imgSelectIndex == 0) {
            ivImg1.setImageBitmap(BitmapFactory.decodeFile(filePath));
        } else if (imgSelectIndex == 1) {
            ivImg2.setImageBitmap(BitmapFactory.decodeFile(filePath));
        } else if (imgSelectIndex == 2) {
            ivImg3.setImageBitmap(BitmapFactory.decodeFile(filePath));
        } else if (imgSelectIndex == 3) {
            ivImg4.setImageBitmap(BitmapFactory.decodeFile(filePath));
        }

        showLoading();
        FileImageUpload.uploadMultiFile(filePath, HttpManage.host + HttpManage.uploadSuggest, new FileImageUpload.UploadLinstener() {
            @Override
            public void onSuccess(String response) {
                BaseEntity<UserInfo> result = new Gson().fromJson(response, new TypeToken<BaseEntity<UserInfo>>() {
                }.getType());

                LogUtil.d("uploadMultiFile-》" + response);
                Message message = new Message();
                message.obj = response;
                message.what = 2;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFail() {
                dismissLoading();
            }
        });
    }

}
