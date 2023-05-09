package com.voocoo.pet.modules.pet;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h3c.shengshiqu.ShengShiQuDialog;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.umeng.commonsdk.debug.D;
import com.voocoo.pet.BuildConfig;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.DelFeedPlanFinishEvent;
import com.voocoo.pet.common.event.RefreshPetEvent;
import com.voocoo.pet.common.utils.FileImageUpload;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Pet;
import com.voocoo.pet.entity.UploadHeadEntity;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class PetInfoActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.iv_head)
    RoundImageView ivHead;
    @BindView(R.id.tv_nickname)
    TextView tvNickName;
    @BindView(R.id.tv_brand)
    TextView tvBrand;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_weight)
    TextView tvWeight;

    private TimePickerView pvTime; //时间选择器对象

    private AppDialog appDialog;

    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    private Uri imageUri;//相机拍照图片保存地址
    private Uri outputUri;//裁剪万照片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪

    private Pet pet;

    private String url;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ivHead.setImageBitmap(BitmapFactory.decodeFile((String) msg.obj));
            } else {
                UploadHeadEntity result = new Gson().fromJson((String) msg.obj, new TypeToken<UploadHeadEntity>() {
                }.getType());
                if (result.getCode() == 200) {
                    dismissLoading();
                    Map<String, Object> params = new HashMap<>();
                    params.put("petId", pet.petId);
                    params.put("petUser", pet.petUser);
                    params.put("petImg", result.getImgUrl());
                    HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {

                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            LogUtil.d(response);
                            EventBus.getDefault().post(new RefreshPetEvent());
                        }
                    });
                } else {
                    showToast(result.getMsg());
                }
            }
        }
    };

    private String getFormatAge(int day) {
        int year = day / 365;
        int month = (day - year * 365) / 30;
        int date = day - ((year * 365) + (month * 30));
        String content = "";
        if (year > 0) {
            content += year + "岁";
        }
        if (month > 0) {
            content += month + "月";
        }
        if (date > 0) {
            content += date + "天";
        }
        return content;
    }

    private void getData() {
        tvNickName.setText(pet.petNickname);
        tvSex.setText(pet.petGender.equals("1") ? getResources().getStringArray(R.array.select_pet_sex_array)[0] : getResources().getStringArray(R.array.select_pet_sex_array)[1]);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(pet.petAge);
            tvAge.setText(getFormatAge(getGapCount(date,new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvBrand.setText(pet.petBreed);
        tvWeight.setText(pet.petWeight + "kg");
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH)
                .placeholder(R.mipmap.app_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(PetInfoActivity.this).load(pet.petImg).apply(options).into(ivHead);
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 根据出生的时间戳获取当前年龄
     *
     * @param birthday 生日时间戳(毫秒秒)
     */
    public static int getAge(long birthday) {
        Calendar currentCalendar = Calendar.getInstance();//实例化calendar
        currentCalendar.setTimeInMillis(System.currentTimeMillis());//调用setTimeInMillis方法和System.currentTimeMillis()获取当前时间

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(birthday);//这个解析传进来的时间戳

        if (currentCalendar.get(Calendar.MONTH) >= targetCalendar.get(Calendar.MONTH)) {//如果现在的月份大于生日的月份
            return currentCalendar.get(Calendar.YEAR) - targetCalendar.get(Calendar.YEAR);//那就直接减,因为现在的年月都大于生日的年月
        } else {
            return currentCalendar.get(Calendar.YEAR) - targetCalendar.get(Calendar.YEAR) - 1;//否则,减掉一年
        }
    }

    @OnClick({R.id.ly_brand, R.id.ly_nickname, R.id.ly_head, R.id.ly_sex, R.id.ly_weight, R.id.ly_age, R.id.btn_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_age:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = dateFormat.parse(pet.petAge);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                AppDialog.showSetPetAge(PetInfoActivity.this,getGapCount(date,new Date()), new AppDialog.PetAgeSetListener() {
                    @Override
                    public void setSize(int size,String str) {
                        tvAge.setText(getFormatAge(size));
                        Map<String, Object> params = new HashMap<>();
                        params.put("petId", pet.petId);
                        params.put("petUser", pet.petUser);
                        params.put("petAge", str);
                        HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                            }
                        });
                    }
                }).show();
                break;
            case R.id.ly_weight:
                AppDialog.showSetPetWeight(PetInfoActivity.this, pet.petWeight, new AppDialog.PetWeightSetListener() {
                    @Override
                    public void setSize(float size) {
                        tvWeight.setText(size + "kg");
                        Map<String, Object> params = new HashMap<>();
                        params.put("petId", pet.petId);
                        params.put("petUser", pet.petUser);
                        params.put("petWeight", size);
                        HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                            }
                        });
                    }
                }).show();
                break;
            case R.id.btn_del:
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_del_pet), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        HttpManage.getInstance().delPet(pet.petId, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                LogUtil.d("delPet onError");
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d("delPet onSuccess=" + response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();
                break;
            case R.id.ly_sex:
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_pet_sex_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("petId", pet.petId);
                        params.put("petUser", pet.petUser);
                        params.put("petGender", position == 0 ? "1" : "0");
                        HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                            }
                        });
                        tvSex.setText(position == 0 ? getResources().getStringArray(R.array.select_pet_sex_array)[0] : getResources().getStringArray(R.array.select_pet_sex_array)[1]);
                    }
                }).show();
                break;
            case R.id.ly_nickname:
                StyledDialog.buildNormalInput("", getString(R.string.text_nickname), "", tvNickName.getText().toString(), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {

                    }

                    @Override
                    public void onSecond() {
                        //save name
                    }

                    @Override
                    public boolean onInputValid(CharSequence input1, CharSequence input2, EditText editText1, EditText editText2) {
                        if (TextUtils.isEmpty(input1)) {
                            showToast(getString(R.string.hint_nick_name));
                            return super.onInputValid(input1, input2, editText1, editText2);
                        }
                        if (input1.length() > 10) {
                            showToast(getString(R.string.hint_nick_name_too_long));
                            return super.onInputValid(input1, input2, editText1, editText2);
                        }
                        tvNickName.setText(input1);
                        pet.petNickname = input1.toString();

                        Map<String, Object> params = new HashMap<>();
                        params.put("petId", pet.petId);
                        params.put("petUser", pet.petUser);
                        params.put("petNickname", pet.petNickname);
                        HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                            }
                        });
                        return super.onInputValid(input1, input2, editText1, editText2);
                    }
                }).setBtnColor(R.color.color_tab_select, R.color.color_tab_select, R.color.color_tab_select).setBtnText(getString(R.string.confirm), getString(R.string.cancel)).show();
                break;
            case R.id.ly_brand:
                StyledDialog.buildNormalInput("", getString(R.string.text_pin), "", tvBrand.getText().toString(), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {

                    }

                    @Override
                    public void onSecond() {
                        //save name
                    }

                    @Override
                    public boolean onInputValid(CharSequence input1, CharSequence input2, EditText editText1, EditText editText2) {
                        if (TextUtils.isEmpty(input1)) {
                            showToast(getString(R.string.hint_brand));
                            return super.onInputValid(input1, input2, editText1, editText2);
                        }
                        if (input1.length() > 10) {
                            showToast(getString(R.string.hint_brand_too_long));
                            return super.onInputValid(input1, input2, editText1, editText2);
                        }
                        tvBrand.setText(input1);
                        pet.petBreed = input1.toString();

                        Map<String, Object> params = new HashMap<>();
                        params.put("petId", pet.petId);
                        params.put("petUser", pet.petUser);
                        params.put("petBreed", pet.petBreed);
                        HttpManage.getInstance().updatePet(params, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {

                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d(response);
                                EventBus.getDefault().post(new RefreshPetEvent());
                            }
                        });
                        return super.onInputValid(input1, input2, editText1, editText2);
                    }
                }).setBtnColor(R.color.color_tab_select, R.color.color_tab_select, R.color.color_tab_select).setBtnText(getString(R.string.confirm), getString(R.string.cancel)).show();
                break;
            case R.id.ly_head:
                StyledDialog.buildBottomItemDialog(Arrays.asList(getResources().getStringArray(R.array.select_picture_array)), getString(R.string.text_cancel), new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 1) {
                            //动态权限
                            if (ContextCompat.checkSelfPermission(PetInfoActivity.this,
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PetInfoActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_pet_info));
        pet = (Pet) getIntent().getSerializableExtra("pet");

        initView();
        getData();
        StyledDialog.init(this);
    }

    private void initView() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pet_info;
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
            imageUri = FileProvider.getUriForFile(PetInfoActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }

    private void pickFromGalary() {
        //动态权限
        if (ContextCompat.checkSelfPermission(PetInfoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PetInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            selectFromAlbum();//打开相册
        }
    }

    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(PetInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PetInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        AppDialog.doubleTextOneButton(PetInfoActivity.this, getString(R.string.post_photo_error_title), getString(R.string.post_photo_error_content)).show();
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
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
        showLoading();
        LogUtil.d("uploadPhoto->" + filePath);
        Message message = new Message();
        message.obj = filePath;
        message.what = 1;
        mHandler.sendMessage(message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileImageUpload.uploadMultiFile(filePath, HttpManage.host + HttpManage.uploadPetHead, new FileImageUpload.UploadLinstener() {
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
        }).start();
    }

}
