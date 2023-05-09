package com.zxing.qrcode.produce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 全屏二维码
 */
public class QrCodeShowActivity extends Activity {

    private static final String EXTRA_CODE = "code";

    public static Intent actionView(Context context, String code) {
        Intent intent = new Intent(context, QrCodeShowActivity.class);
        intent.putExtra(EXTRA_CODE, code);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setScreenBrightness(255);
        setContentView(R.layout.zxing__qrcode_show_activity);
        String code = getIntent().getStringExtra(EXTRA_CODE);
        ImageView dragImageView = (ImageView) findViewById(R.id.iv_image);
        dragImageView.setImageBitmap(GenerateQrCodeHelp.getInstance().generateQRImage(code,dragImageView.getWidth(),dragImageView.getHeight()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            QrCodeShowActivity.this.finish();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }
}