package com.xlink.ext.decode;

import android.os.Handler;
import android.os.Message;

import com.xlink.ext.CaptureBaseActivity;
import com.zbar.lib.R;
import com.zbar.lib.camera.CameraManager;

import timber.log.Timber;


public final class CaptureActivityHandler extends Handler {

    DecodeThread decodeThread = null;
    CaptureBaseActivity activity = null;
    private State state;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureActivityHandler(CaptureBaseActivity activity) {
        this.activity = activity;
        if (decodeThread == null) {
            decodeThread = new DecodeThread(activity);
            decodeThread.start();
        }

        state = State.SUCCESS;
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        int id = message.what;
        if (id == R.id.auto_focus) {
            if (state == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (id == R.id.restart_preview) {
            restartPreviewAndDecode();
        } else if (id == R.id.decode_succeeded) {
            state = State.SUCCESS;
            activity.handleDecode((String) message.obj);// 解析成功，回调
        } else if (id == R.id.decode_failed) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        CameraManager.get().stopPreview();
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
        removeMessages(R.id.decode);
        removeMessages(R.id.auto_focus);
        //终止线程
        decodeThread.exit();
    }

    private void restartPreviewAndDecode() {
        Timber.v("---restartPreviewAndDecode---restartPreviewAndDecode---");
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }

}
