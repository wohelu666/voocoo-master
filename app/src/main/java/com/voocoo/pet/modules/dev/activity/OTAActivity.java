package com.voocoo.pet.modules.dev.activity;

import static android.content.ContentValues.TAG;
import static com.hss01248.dialog.ScreenUtil.dip2px;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.umeng.commonsdk.debug.E;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.queryOTAbean;
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class OTAActivity extends AbsBaseActivity {
    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;
    @BindView(R.id.tvNew)
    TextView tvNew;
    @BindView(R.id.tvCurrent)
    TextView tvCurrent;
    int progress = 0;

    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.llupdate)
    ConstraintLayout llupdate;
    @BindView(R.id.tvProgress)
    TextView tvProgress;
    @BindView(R.id.pbProgress)
    ProgressBar progressBar;

    @BindView(R.id.llcontent)
    ConstraintLayout llcontent;
    @BindView(R.id.llmessage)
    LinearLayout llmessage;
    @BindView(R.id.llprogress)
    LinearLayout llprogress;
    @BindView(R.id.llIsinstall)
    LinearLayout llIsinstall;
    @BindView(R.id.ivIs)
    ImageView ivIs;
    @BindView(R.id.llfail)
    LinearLayout llfail;
    private ObjectAnimator objectAnimator;
    private boolean isUpdate = false;
    private Device device;
    private boolean isNew = false;
    Timer timer = new Timer();
    private Timer timerSuc;
    private Handler handler;

    @Override
    protected int getLayoutId() {
        return R.layout.ota_activity;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        device = (Device) getIntent().getSerializableExtra("device");
        int powerPlug = getIntent().getIntExtra("powerPlug", -1);
        String onLine = getIntent().getStringExtra("onLine");
        objectAnimator = ObjectAnimator.ofFloat(ivIs, "rotation", 0f, 360f);
        timerSuc = new Timer();
        handler = new Handler();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.update));
        HttpManage.getInstance().selectFeederVersion(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                BaseEntity<queryOTAbean> result = new Gson().fromJson(response, new TypeToken<BaseEntity<queryOTAbean>>() {
                }.getType());
                if (result.getCode() == 200) {
                    if (result.getData().getLastVersion() == 0) {
                        tvNew.setText(getString(R.string.isNewVersion) + result.getData().getCurrentVersion());
                        tvCurrent.setText(getString(R.string.current_version) + result.getData().getCurrentVersion());
                        btnUpdate.setVisibility(View.GONE);
                        isNew = true;
                    } else {
                        tvNew.setText(getString(R.string.new_version) + result.getData().getDeviceVersionNumber());
                        tvCurrent.setText(getString(R.string.current_version) + result.getData().getCurrentVersion());
                        isNew = false;
                    }
                }
            }
        });
        if ("0".equals(onLine)) {
            btnUpdate.setBackgroundResource(R.drawable.bg_btn_able);
        } else if ("1".equals(onLine)) {
            btnUpdate.setBackgroundResource(R.drawable.bg_btn_cancel);
        }
        if (powerPlug == 0) {
            btnUpdate.setBackgroundResource(R.drawable.bg_btn_cancel);
        } else if (powerPlug == 1) {
            btnUpdate.setBackgroundResource(R.drawable.bg_btn_able);

        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(onLine)) {
                    showToast(getString(R.string.device_not_online));
                    return;
                }
                if (0 == powerPlug) {
                    showToast(getString(R.string.battery_mode_not));
                    return;
                }
                if (isNew) {
                    AppDialog.showAlertDialog(OTAActivity.this, getString(R.string.isNewVersion)).show();
                    return;
                }

                HttpManage.getInstance().feederVersionUpdate(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {

                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        Log.e(TAG, "onSuccess: " + response);
                        BaseEntity<queryOTAbean> result = new Gson().fromJson(response, new TypeToken<BaseEntity<queryOTAbean>>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            showUpdateDialog();
                            timerSuc.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            queryUpdate();
                                        }
                                    });

                                }
                            }, 0, 5000);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    llupdate.setVisibility(View.GONE);
                                    llfail.setVisibility(View.VISIBLE);
                                    isUpdate = false;
                                    objectAnimator.cancel();
                                    timer.cancel();
                                    timerSuc.cancel();
                                }
                            }, 300000);
                        }
                    }
                });
            }
        });


    }

    /**
     * 查询升级
     */
    public void queryUpdate() {
        HttpManage.getInstance().selectFeederVersion(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                Log.e(TAG, "onSuccess: " + response);
                BaseEntity<queryOTAbean> result = new Gson().fromJson(response, new TypeToken<BaseEntity<queryOTAbean>>() {
                }.getType());
                if (result.getCode() == 200) {
                    if (result.getData().getLastVersion() == 0) {
                        AppDialog.showAlertDialog(OTAActivity.this, getString(R.string.update_success)).show();
                        objectAnimator.cancel();
                        timer.cancel();
                        timerSuc.cancel();
                        isUpdate = false;
                        llIsinstall.setVisibility(View.GONE);
                        btnUpdate.setVisibility(View.GONE);
                        llcontent.setVisibility(View.GONE);
                        llprogress.setVisibility(View.GONE);
                        llmessage.setVisibility(View.VISIBLE);
                        tvCurrent.setText(getString(R.string.current_version) + result.getData().getCurrentVersion());
                        tvNew.setText(getString(R.string.new_version) + result.getData().getCurrentVersion());
                        handler.removeCallbacksAndMessages(null);
                        isNew = true;
                    } else {
//
                    }
                } else {
                    showToast(getString(R.string.update_fail));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isUpdate == true) {
                    showToast(getString(R.string.isUpdating));
                } else {
                    supportFinishAfterTransition();

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ota 更新
     */
    private void showUpdateDialog() {
        progress = 0;
        isUpdate = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llmessage.setVisibility(View.GONE);
                        llprogress.setVisibility(View.VISIBLE);
                        llcontent.setVisibility(View.VISIBLE);
                        btnUpdate.setVisibility(View.GONE);

                        progress = progress + 10;

                        if (progress == 100) {
                            timer.cancel();
                            updateOTA();
                        }
                        tvProgress.setText(progress + "%");
                        progressBar.setProgress(progress);
                    }
                });

            }
        }, 0, 1000);

//        MessageDialog dialog = MessageDialog.show("升级中", "")
//                //设置自定义布局
//                .setCustomView(new OnBindView<MessageDialog>(R.layout.item_progress) {
//                    @Override
//                    public void onBind(MessageDialog dialog, View v) {
//                        TextView tvProgress = v.findViewById(R.id.tvProgress);
//                        ProgressBar progressBar = v.findViewById(R.id.pbProgress);
//
//                        //添加布局边距
//                        dialog.getDialogImpl().boxCustom.setPadding(dip2px(20), dip2px(10), dip2px(20), dip2px(10));
//                    }
//                });
//        dialog.setCancelable(false);
    }

    private void updateOTA() {
        llIsinstall.setVisibility(View.VISIBLE);
        llprogress.setVisibility(View.GONE);
        //旋转的角度可有多个

        objectAnimator.setDuration(1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);//匀速
        objectAnimator.start();//开始(重新开始)


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isUpdate == true) {
            showToast(getString(R.string.isUpdating));
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isUpdate == true) {
                showToast(getString(R.string.isUpdating));
                return true;
            } else {
                finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        objectAnimator.cancel();
        timer.cancel();
        timerSuc.cancel();
        handler.removeCallbacksAndMessages(null);

    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }
}
