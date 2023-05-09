package com.xlink.ext;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.xlink.ext.decode.CaptureActivityHandler;
import com.xlink.ext.decode.InactivityTimer;
import com.zbar.lib.R;
import com.zbar.lib.camera.CameraManager;

import java.io.IOException;


public abstract class CaptureBaseActivity extends Activity implements Callback {

    private static final long VIBRATE_DURATION = 200L;
    protected static final int SCAN_RESULT_SUCC = 1;
    protected static final int SCAN_RESULT_FAILE = 2;
    protected static final int SCAN_RESULT_CANCEL = 3;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    private int x = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;

    /**
     * 是否需要保存扫描截图
     */
    private boolean isNeedCapture = false;

    /**
     * 是否已经打开闪光灯
     */
    boolean isOpenedLight = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 CameraManager
        CameraManager.init(getApplicationContext());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    protected abstract void handleResult(int state, String result);

    protected abstract SurfaceView getSurfaceView();

    public abstract int getScanAreaWidth();

    public abstract int getScanAreaHeight();

    public abstract int getScanAreaPointX();

    public abstract int getScanAreaPointY();

    public abstract int getContainerWidth();

    public abstract int getContainerHeight();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    protected void toggleLight() {
        if (isOpenedLight) {
            isOpenedLight = false;
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            isOpenedLight = true;
            // 关闪光灯
            CameraManager.get().offLight();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = getSurfaceView().getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    public void handleDecode(String result) {
        if (!TextUtils.isEmpty(result)) {
            inactivityTimer.onActivity();
            playBeepSoundAndVibrate();
            handleResult(SCAN_RESULT_SUCC, result);
            return;
        }

        // 重新扫描，不发送此消息扫描一次结束后就不能再次扫描
        // restartScan();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            //width height的值相当于手机屏幕分辨率
            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;
            int x = getScanAreaPointX() * width / getContainerWidth();
            int y = getScanAreaPointY() * height / getContainerHeight();
//
            int cropWidth = getScanAreaWidth() * width / getContainerWidth();
            int cropHeight = getScanAreaHeight() * height / getContainerHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);

            //CameraManager.get().initCamera(getContainerWidth(), getContainerHeight());
            // 设置是否需要截图
            setNeedCapture(true);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            Toast.makeText(CaptureBaseActivity.this, "摄像头打开失败，请检查摄像头是否被禁用", Toast.LENGTH_SHORT).show();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureBaseActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    protected void restartScan() {
        handler.sendEmptyMessage(R.id.restart_preview);
    }

    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

}