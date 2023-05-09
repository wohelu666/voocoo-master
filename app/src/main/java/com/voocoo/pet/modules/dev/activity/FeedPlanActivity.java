package com.voocoo.pet.modules.dev.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.event.AddFeedPlanFinishEvent;
import com.voocoo.pet.common.event.BindDevSmartFeedPlanFinishEvent;
import com.voocoo.pet.common.event.ChangeRecyclerEvent;
import com.voocoo.pet.common.event.DelFeedPlanFinishEvent;
import com.voocoo.pet.common.event.SmartPlanSaveEvent;
import com.voocoo.pet.common.event.UpdateFeedPlanFinishEvent;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.common.widgets.SwitchView;
import com.voocoo.pet.entity.AddDeviceEntity;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.BindDevFeedPlan;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.Diet;
import com.voocoo.pet.entity.FeedPlan;
import com.voocoo.pet.entity.UpdateDiet;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.activity.BindDeviceFailActivity;
import com.voocoo.pet.modules.dev.adapter.FeedPlanListAdapter;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;

public class FeedPlanActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.switch_plan_start)
    SwitchView svPlanStart;

    @BindView(R.id.ly_progress)
    View lyProgress;

    @BindView(R.id.tv_left_food)
    TextView tvLeftFood;
    @BindView(R.id.tv_plan_out_food)
    TextView tvPlanOutFood;
    @BindView(R.id.tv_recycle)
    TextView tvRecycle;

    @BindView(R.id.rv_plan)
    RecyclerView rvPlan;

    @BindView(R.id.tv_plan_start_title)
    TextView tvPlanStartTitle;

    @BindView(R.id.line)
    View line;
    @BindView(R.id.ly_get_smart_plan)
    View smartPlan;

    @BindView(R.id.btn_only_save)
    Button btnOnlySave;

    FeedPlanListAdapter planListAdapter;

    List<FeedPlan.FeedPlanDiets> feedPlanDiets = new ArrayList<>();
    List<FeedPlan.FeedPlanDiets> finalFeedPlanDiets = new ArrayList<>();

    Device device;

    int planId;

    String mac;
    String name;

    int selMode = 3;
    int retryNum = 0;

    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_not_seve)
    Button btnNotSave;

    BindDevFeedPlan bindDevFeedPlan = new BindDevFeedPlan();

    private int type = 0;

    private int cIndex;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (retryNum >= 20) {
                Intent intent = new Intent(FeedPlanActivity.this, BindDeviceFailActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (type == 0) {
                    retryNum++;
                    HttpManage.getInstance().addDev(name, selMode + "", bindDevFeedPlan, mac, wifiName, new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                            LogUtil.d("addDev onError");
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            LogUtil.d("addDev onSuccess->" + response);
                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else if (result.getCode() == 500) {
                                if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                    finish();
                                } else {
                                    mHandler.sendEmptyMessageDelayed(0, 3000);
                                }
                            } else if (result.getCode() == 502) {
                                dismissLoading();
                                StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                    @Override
                                    public void onFirst() {
                                        showLoading();

                                        HttpManage.getInstance().reAddDev(name, selMode + "", bindDevFeedPlan, mac, new HttpManage.ResultCallback<String>() {
                                            @Override
                                            public void onError(Header[] headers, HttpManage.Error error) {
                                                dismissLoading();
                                                LogUtil.d("addDev onError");
                                            }

                                            @Override
                                            public void onSuccess(int code, String response) {
                                                dismissLoading();
                                                LogUtil.d("addDev onSuccess->" + response);
                                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                                }.getType());
                                                if (result.getCode() == 200) {
                                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                    finish();
                                                } else {
                                                    showToast(result.getMsg());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSecond() {

                                    }
                                }).show();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        }
                    });
                } else {
                    retryNum++;
                    HttpManage.getInstance().addDev(name, selMode + "", mac, new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                            LogUtil.d("addDev onError");
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            LogUtil.d("addDev onSuccess->" + response);
                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                            }.getType());
                            if (result.getCode() == 200) {
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else if (result.getCode() == 500) {
                                if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                    finish();
                                } else {
                                    mHandler.sendEmptyMessageDelayed(0, 3000);
                                }
                            } else if (result.getCode() == 502) {
                                dismissLoading();
                                StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                    @Override
                                    public void onFirst() {
                                        showLoading();

                                        HttpManage.getInstance().reAddDev(name, selMode + "", mac, wifiName, new HttpManage.ResultCallback<String>() {
                                            @Override
                                            public void onError(Header[] headers, HttpManage.Error error) {
                                                dismissLoading();
                                                LogUtil.d("addDev onError");
                                            }

                                            @Override
                                            public void onSuccess(int code, String response) {
                                                dismissLoading();
                                                LogUtil.d("addDev onSuccess->" + response);
                                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                                }.getType());
                                                if (result.getCode() == 200) {
                                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                    finish();
                                                } else {
                                                    showToast(result.getMsg());
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onSecond() {

                                    }
                                }).show();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        }
                    });
                }
            }
        }
    };
    private String wifiName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        device = (Device) getIntent().getSerializableExtra("device");
        selMode = getIntent().getIntExtra("mode", 3);
        mac = getIntent().getStringExtra("mac");
        name = getIntent().getStringExtra("name");
        wifiName = getIntent().getStringExtra("wifiName");
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().post(new AddDeviceSuccessEvent());
    }

    private void initData() {
        if (device == null) {
            lyProgress.setVisibility(View.VISIBLE);
            btnOnlySave.setVisibility(View.GONE);
            curRecycler.add(1);
            curRecycler.add(2);
            curRecycler.add(3);
            curRecycler.add(4);
            curRecycler.add(5);
            curRecycler.add(6);
            curRecycler.add(7);
            getRecycler(curRecycler);
            btnSave.setVisibility(View.VISIBLE);
            btnNotSave.setVisibility(View.VISIBLE);
//            smartPlan.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            btnOnlySave.setVisibility(View.VISIBLE);
            lyProgress.setVisibility(View.GONE);
//            smartPlan.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnNotSave.setVisibility(View.GONE);
            showLoading();
            getPlanList(device.getDeviceId());
        }
    }

    /**
     * 获取设备计划
     */
    private void getPlanList(int deviceId) {
        /**
         * 获取喂食计划
         */
        HttpManage.getInstance().getDevFeedPlan(deviceId, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
                LogUtil.d("getDevFeedPlan onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("getDevFeedPlan->" + response);
                BaseEntity<FeedPlan> result = new Gson().fromJson(response, new TypeToken<BaseEntity<FeedPlan>>() {
                }.getType());
                if (result.getCode() == 200) {

                    feedPlanDiets = new ArrayList<>();
                    //tvLeftFood.setText(result.getData().feedingTimes + "");
                    //tvPlanOutFood.setText(result.getData().feedingAmount + "");
                    planId = result.getData().feederPlanId;

                    getRecycler(result.getData().feederPlanCycle);
                    svPlanStart.setOpened(result.getData().status.equals("0"));
                    tvPlanStartTitle.setText(result.getData().status.equals("0") ? "计划开启" : "计划关闭");
                    feedPlanDiets.addAll(result.getData().planDiets);
                    finalFeedPlanDiets.addAll(result.getData().planDiets);
                    Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
                        @Override
                        public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                            return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
                        }
                    });
                    planListAdapter.setData(feedPlanDiets);

                    int all = 0;
                    for (int i = 0; i < feedPlanDiets.size(); i++) {
                        all += feedPlanDiets.get(i).dietAmount;
                    }
                    tvLeftFood.setText(feedPlanDiets.size() + "");
                    tvPlanOutFood.setText(all + "");

                }
            }
        });
    }

    ArrayList<Integer> curRecycler = new ArrayList<>();

    private void getRecycler(ArrayList<Integer> recycler) {
        curRecycler = recycler;
        if (recycler.size() == 7) {
            tvRecycle.setText("每天");
        } else if (recycler.size() == 0) {
            tvRecycle.setText("");
        } else {
            String recylerStr = "";
            if (recycler.contains(1)) {
                recylerStr += getString(R.string.text_day1) + " ";
            }
            if (recycler.contains(2)) {
                recylerStr += getString(R.string.text_day2) + " ";
            }
            if (recycler.contains(3)) {
                recylerStr += getString(R.string.text_day3) + " ";
            }
            if (recycler.contains(4)) {
                recylerStr += getString(R.string.text_day4) + " ";
            }
            if (recycler.contains(5)) {
                recylerStr += getString(R.string.text_day5) + " ";
            }
            if (recycler.contains(6)) {
                recylerStr += getString(R.string.text_day6) + " ";
            }
            if (recycler.contains(7)) {
                recylerStr += getString(R.string.text_day7) + " ";
            }
            recylerStr = recylerStr.substring(0, recylerStr.length() - 1);
            tvRecycle.setText(recylerStr);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
        if (feedPlanDiets.size() == 0) {
            svPlanStart.setOpened(false);
        }
    }

    private boolean isModify = false;

    @OnClick({R.id.btn_only_save, R.id.ly_recycle, R.id.btn_save, R.id.btn_not_seve, R.id.ly_add_feed_plan, R.id.ly_get_smart_plan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_only_save://保存按钮
                if (device == null) {
                    if (feedPlanDiets.size() == 0) {
                        AppDialog.doubleTextDoubleButton(FeedPlanActivity.this, "温馨提示", "您还没为您的爱宠制定喂食计划哦。", "智能计划", "前往制定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(FeedPlanActivity.this, SmartFeedPlanActivity.class);
                                intent.putExtra("device", device);
                                intent.putExtra("plan", feedPlanDiets.size());

                                startActivity(intent);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                                intent.putIntegerArrayListExtra("data", curRecycler);
                                intent.putExtra("size", feedPlanDiets.size());
                                startActivity(intent);
                            }
                        }).show();
                    } else {
                        List<Diet> addDietList = new ArrayList<>();
                        for (int i = 0; i < feedPlanDiets.size(); i++) {
                            addDietList.add(new Diet(feedPlanDiets.get(i).dietAmount, feedPlanDiets.get(i).dietTag, feedPlanDiets.get(i).dietTime));
                        }
                        bindDevFeedPlan.insertDietList = addDietList;
                        bindDevFeedPlan.feederPlanCycle = curRecycler;
                        bindDevFeedPlan.status = svPlanStart.isOpened() ? "0" : "1";

                        type = 0;
                        retryNum++;
                        HttpManage.getInstance().addDev(name, selMode + "", bindDevFeedPlan, mac, wifiName, new HttpManage.ResultCallback<String>() {
                            @Override
                            public void onError(Header[] headers, HttpManage.Error error) {
                                dismissLoading();
                                LogUtil.d("addDev onError");
                            }

                            @Override
                            public void onSuccess(int code, String response) {
                                LogUtil.d("addDev onSuccess->" + response);
                                BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                }.getType());
                                if (result.getCode() == 200) {
                                    EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                    finish();
                                } else if (result.getCode() == 500) {
                                    if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                        EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                        finish();
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(0, 3000);
                                    }
                                } else if (result.getCode() == 502) {
                                    dismissLoading();
                                    StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                        @Override
                                        public void onFirst() {
                                            showLoading();

                                            HttpManage.getInstance().reAddDev(name, selMode + "", mac, wifiName, new HttpManage.ResultCallback<String>() {
                                                @Override
                                                public void onError(Header[] headers, HttpManage.Error error) {
                                                    dismissLoading();
                                                    LogUtil.d("addDev onError");
                                                }

                                                @Override
                                                public void onSuccess(int code, String response) {
                                                    dismissLoading();
                                                    LogUtil.d("addDev onSuccess->" + response);
                                                    BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                                    }.getType());
                                                    if (result.getCode() == 200) {
                                                        EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                        finish();
                                                    } else {
                                                        showToast(result.getMsg());
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onSecond() {

                                        }
                                    }).show();
                                } else {
                                    mHandler.sendEmptyMessageDelayed(0, 3000);
                                }
                            }
                        });
                    }
                } else {
                    save(device.getDeviceId());
                }
                break;
            case R.id.btn_save: //保存计划，开始使用
                if (feedPlanDiets.size() == 0) {
                    AppDialog.doubleTextDoubleButton(FeedPlanActivity.this, "温馨提示", "您还没为您的爱宠制定喂食计划哦。", "智能计划", "前往制定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FeedPlanActivity.this, SmartFeedPlanActivity.class);
                            intent.putExtra("device", device);
                            intent.putExtra("plan", feedPlanDiets.size());

                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                            intent.putIntegerArrayListExtra("data", curRecycler);
                            intent.putExtra("size", feedPlanDiets.size());
                            intent.putExtra("planList", (Serializable) feedPlanDiets);

                            startActivity(intent);
                        }
                    }).show();
                    return;
                }
                if (svPlanStart.isOpened() == false){
                    AppDialog   alertDialog = AppDialog.showAlertDialog(FeedPlanActivity.this, "请打开喂食计划");
                    alertDialog.show();
                    return;
                }

                //dietTime 喂食时间  dietTag 喂食类型  dietAmount  喂食克数
                List<Diet> addDietList = new ArrayList<>();
                for (int i = 0; i < feedPlanDiets.size(); i++) {
                    addDietList.add(new Diet(feedPlanDiets.get(i).dietAmount, feedPlanDiets.get(i).dietTag, feedPlanDiets.get(i).dietTime));
                }
                bindDevFeedPlan.insertDietList = addDietList;
                bindDevFeedPlan.feederPlanCycle = curRecycler;
                bindDevFeedPlan.status = "0";

                type = 0;
                retryNum++;
                HttpManage.getInstance().addDev(name, selMode + "", bindDevFeedPlan, mac, wifiName, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                        LogUtil.d("addDev onError");
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d("addDev onSuccess->" + response);


                         BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            EventBus.getDefault().post(new AddDeviceSuccessEvent());
                            finish();
                        } else if (result.getCode() == 500) {
                            if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                showToast(result.getMsg());
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        } else if (result.getCode() == 502) {
                            dismissLoading();
                            StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                @Override
                                public void onFirst() {
                                    showLoading();
                                    HttpManage.getInstance().reAddDev(name, selMode + "",bindDevFeedPlan, mac,  new HttpManage.ResultCallback<String>() {
                                        @Override
                                        public void onError(Header[] headers, HttpManage.Error error) {
                                            dismissLoading();
                                            LogUtil.d("addDev onError");
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            dismissLoading();
                                            LogUtil.d("addDev onSuccess->" + response);
                                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                            }.getType());
                                            if (result.getCode() == 200) {
                                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                finish();
                                            } else {
                                                showToast(result.getMsg());
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onSecond() {

                                }
                            }).show();
                        } else {
                            mHandler.sendEmptyMessageDelayed(0, 3000);
                        }
                    }
                }) ;
                break;

            case R.id.ly_get_smart_plan:
                Intent intent = new Intent(FeedPlanActivity.this, SmartFeedPlanActivity.class);
                intent.putExtra("device", device);
                intent.putExtra("plan", feedPlanDiets.size());
                startActivity(intent);
                Log.e("TAG", "onClick: " + SharedPreferencesUtil.queryValue("token"));
                Log.e("TAG", "onClick@@@: " + SharedPreferencesUtil.queryIntValue("userId"));

                break;
            case R.id.ly_recycle:
                intent = new Intent(FeedPlanActivity.this, SelRepeatActivity.class);
                intent.putIntegerArrayListExtra("data", curRecycler);
                startActivity(intent);
                break;

            case R.id.btn_not_seve:
                type = 1;
                HttpManage.getInstance().addDev(name, selMode + "", mac, new HttpManage.ResultCallback<String>() {
                    @Override
                    public void onError(Header[] headers, HttpManage.Error error) {
                        dismissLoading();
                        LogUtil.d("addDev onError");
                    }

                    @Override
                    public void onSuccess(int code, String response) {
                        LogUtil.d("addDev onSuccess->" + response);
                        BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                        }.getType());
                        if (result.getCode() == 200) {
                            EventBus.getDefault().post(new AddDeviceSuccessEvent());
                            finish();
                        } else if (result.getCode() == 500) {
                            if (!TextUtils.isEmpty(result.getMsg()) && result.getMsg().contains("已绑定")) {
                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                finish();
                            } else {
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                            }
                        } else if (result.getCode() == 502) {
                            dismissLoading();
                            StyledDialog.buildIosAlert(result.getMsg(), "", new MyDialogListener() {
                                @Override
                                public void onFirst() {
                                    showLoading();

                                    HttpManage.getInstance().reAddDev(name, selMode + "", mac, wifiName, new HttpManage.ResultCallback<String>() {
                                        @Override
                                        public void onError(Header[] headers, HttpManage.Error error) {
                                            dismissLoading();
                                            LogUtil.d("addDev onError");
                                        }

                                        @Override
                                        public void onSuccess(int code, String response) {
                                            dismissLoading();
                                            LogUtil.d("addDev onSuccess->" + response);
                                            BaseEntity result = new Gson().fromJson(response, new TypeToken<BaseEntity>() {
                                            }.getType());
                                            if (result.getCode() == 200) {
                                                EventBus.getDefault().post(new AddDeviceSuccessEvent());
                                                finish();
                                            } else {
                                                showToast(result.getMsg());
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onSecond() {

                                }
                            }).show();
                        } else {
                            mHandler.sendEmptyMessageDelayed(0, 3000);
                        }
                    }
                });
                break;
            case R.id.ly_add_feed_plan:
                if (feedPlanDiets.size() >= 4) {
                    showToast("最多只能设定4条喂食计划");
                    return;
                }
                intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                intent.putIntegerArrayListExtra("data", curRecycler);
                intent.putExtra("size", feedPlanDiets.size());
                intent.putExtra("planList", (Serializable) feedPlanDiets);

                startActivity(intent);
                break;
        }
    }

    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_food_plan));

        svPlanStart.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                if (feedPlanDiets.size() == 0) {
                    AppDialog.doubleTextDoubleButton(FeedPlanActivity.this, "温馨提示", "您还没为您的爱宠制定喂食计划哦。", "智能计划", "前往制定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            svPlanStart.setOpened(true);
                            Intent intent = new Intent(FeedPlanActivity.this, SmartFeedPlanActivity.class);
                            intent.putExtra("device", device);
                            intent.putExtra("plan", feedPlanDiets.size());
                            startActivity(intent);
                            tvPlanStartTitle.setText("计划开启");
                            showToast(getString(R.string.text_ensure_open_plan));



                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            svPlanStart.setOpened(true);
                            Intent intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                            intent.putIntegerArrayListExtra("data", curRecycler);
                            intent.putExtra("size", feedPlanDiets.size());
                            intent.putExtra("planList", (Serializable) feedPlanDiets);

                            startActivity(intent);

                            tvPlanStartTitle.setText("计划开启");
                            showToast(getString(R.string.text_ensure_open_plan));
                        }
                    }).show();
                    return;
                }
                svPlanStart.setOpened(true);
                tvPlanStartTitle.setText("计划开启");
                showToast(getString(R.string.text_ensure_open_plan));
            }

            @Override
            public void toggleToOff(SwitchView view) {
                StyledDialog.buildIosAlert(getString(R.string.text_ensure_close_plan), "", new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        svPlanStart.setOpened(false);
                        tvPlanStartTitle.setText("计划关闭");
                    }

                    @Override
                    public void onSecond() {
                        svPlanStart.setOpened(true);
                    }
                }).show();

            }
        });

        planListAdapter = new FeedPlanListAdapter(FeedPlanActivity.this);
        Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
            @Override
            public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
            }
        });
        planListAdapter.setData(feedPlanDiets);
        planListAdapter.setDietClickListener(new FeedPlanListAdapter.DietClickListener() {
            @Override
            public void onClick(int index, FeedPlan.FeedPlanDiets diets) {
                cIndex = index;
                Intent intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                intent.putIntegerArrayListExtra("data", curRecycler);
                intent.putExtra("device", device);
                intent.putExtra("diet", diets);
                intent.putExtra("size", feedPlanDiets.size());
                intent.putExtra("index", index);
                intent.putExtra("planList", (Serializable) feedPlanDiets);
                startActivity(intent);
            }
        });

        rvPlan.setLayoutManager(new LinearLayoutManager(FeedPlanActivity.this));
        rvPlan.setAdapter(planListAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed_plan;
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

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecycler(ChangeRecyclerEvent event) {
        isModify = true;
        getRecycler(event.recycler);
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSmartPlanSave(SmartPlanSaveEvent event) {
        isModify = true;
        initData();
    }

    /**
     * 修改喂食计划
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePlan(UpdateFeedPlanFinishEvent event) {
        isModify = true;
        FeedPlan.FeedPlanDiets feedPlanDiet = event.feedPlanDiets;
        int all = 0;
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            if (i == cIndex) {
                //编辑
                feedPlanDiets.get(i).dietTag = feedPlanDiet.dietTag;
                feedPlanDiets.get(i).dietTime = feedPlanDiet.dietTime;
                feedPlanDiets.get(i).dietAmount = feedPlanDiet.dietAmount;
            }
            all += feedPlanDiets.get(i).dietAmount;
        }
        tvLeftFood.setText(feedPlanDiets.size() + "");
        tvPlanOutFood.setText(all + "");

        Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
            @Override
            public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
            }
        });
        planListAdapter.setData(feedPlanDiets);
    }

    /**
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDelPlan(DelFeedPlanFinishEvent event) {
        isModify = true;
        FeedPlan.FeedPlanDiets feedPlanDiet = event.feedPlanDiets;
        int all = 0;
        //编辑
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            if (feedPlanDiets.get(i).dietId == feedPlanDiet.dietId) {
                feedPlanDiets.remove(i);
                break;
            }
        }
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            all += feedPlanDiets.get(i).dietAmount;
        }


//        all = feedPlanDiets.stream().mapToInt(i -> feedPlanDiet.dietAmount).sum();

        tvLeftFood.setText(feedPlanDiets.size() + "");
        tvPlanOutFood.setText(all + "");
        Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
            @Override
            public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
            }
        });
        planListAdapter.setData(feedPlanDiets);
    }


    /**
     * 智能喂食列表数据返回
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindDevSmartFeedPlan(BindDevSmartFeedPlanFinishEvent event) {
        isModify = true;
        finalFeedPlanDiets = feedPlanDiets;
        feedPlanDiets = event.feedPlanDiets;
        int all = 0;
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            all += feedPlanDiets.get(i).dietAmount;
        }
        tvLeftFood.setText(feedPlanDiets.size() + "");
        tvPlanOutFood.setText(all + "");
        Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
            @Override
            public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
            }
        });
        planListAdapter.setData(feedPlanDiets);
    }


    /**
     * 添加喂食计划数据
     *
     * @param event
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPlan(AddFeedPlanFinishEvent event) {
        isModify = true;
        FeedPlan.FeedPlanDiets feedPlanDiet = event.feedPlanDiets;
        feedPlanDiets.add(feedPlanDiet);
        int all = 0;
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            all += feedPlanDiets.get(i).dietAmount;
        }
        tvLeftFood.setText(feedPlanDiets.size() + "");
        tvPlanOutFood.setText(all+"");
        Collections.sort(feedPlanDiets, new Comparator<FeedPlan.FeedPlanDiets>() {
            @Override
            public int compare(FeedPlan.FeedPlanDiets a, FeedPlan.FeedPlanDiets b) {
                return Integer.valueOf(a.dietTime.split(":")[0]) - Integer.valueOf(b.dietTime.split(":")[0]);
            }
        });
        planListAdapter.setData(feedPlanDiets);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isModify && svPlanStart.isOpened()) {
                    StyledDialog.buildIosAlert(getString(R.string.text_ensure_back), "", new MyDialogListener() {
                        @Override
                        public void onFirst() {
                        }

                        @Override
                        public void onSecond() {

                        }
                    }).show();
                } else {
                    return super.onOptionsItemSelected(item);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加喂食计划
     *
     * @param deviceId
     *
     */
    private void save(int deviceId) {
        if (feedPlanDiets.size() == 0 && svPlanStart.isOpened()) {
            AppDialog.doubleTextDoubleButton(FeedPlanActivity.this, "温馨提示", "您还没为您的爱宠制定喂食计划哦。",  "智能计划", "前往制定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FeedPlanActivity.this, SmartFeedPlanActivity.class);
                    intent.putExtra("device", device);
                    intent.putExtra("plan", feedPlanDiets.size());

                    startActivity(intent);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FeedPlanActivity.this, AddFeedPlanActivity.class);
                    intent.putIntegerArrayListExtra("data", curRecycler);
                    intent.putExtra("size", feedPlanDiets.size());
                    intent.putExtra("planList", (Serializable) feedPlanDiets);

                    startActivity(intent);
                }
            }).show();
            return;
        }
        if(svPlanStart.isOpened() == false){
            StyledDialog.buildIosAlert(getString(R.string.tips_plan_close), "", new MyDialogListener() {
                @Override
                public void onFirst() {
                    svPlanStart.setOpened(false);
                    tvPlanStartTitle.setText("计划关闭");
                    saveNewPlan(deviceId);
                }

                @Override
                public void onSecond() {
                    svPlanStart.setOpened(true);
                    tvPlanStartTitle.setText("计划开启");

                    saveNewPlan(deviceId);
                }
            }).show();
        }else{
            saveNewPlan(deviceId);
        }


    }

    private void saveNewPlan(int deviceId) {

        //匹配后保存
        List<Integer> delDietList = new ArrayList<>();

        for (int i = 0; i < finalFeedPlanDiets.size(); i++) {
            boolean isHave = false;
            for (int y = 0; y < feedPlanDiets.size(); y++) {
                if (finalFeedPlanDiets.get(i).dietId == feedPlanDiets.get(y).dietId) {
                    //依然存在
                    isHave = true;
                }
            }
            if (!isHave) {
                delDietList.add(finalFeedPlanDiets.get(i).dietId);
            }
        }


        List<Diet> addDietList = new ArrayList<>();
        List<UpdateDiet> editDietList = new ArrayList<>();
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            boolean isHave = false;
            for (int y = 0; y < finalFeedPlanDiets.size(); y++) {
                if (finalFeedPlanDiets.get(y).dietId == feedPlanDiets.get(i).dietId) {
                    //依然存在
                    isHave = true;
                }
            }
            if (!isHave) {
                addDietList.add(new Diet(feedPlanDiets.get(i).dietAmount, feedPlanDiets.get(i).dietTag, feedPlanDiets.get(i).dietTime));
            } else {
                editDietList.add(new UpdateDiet(feedPlanDiets.get(i).dietId, feedPlanDiets.get(i).dietAmount, feedPlanDiets.get(i).dietTag, feedPlanDiets.get(i).dietTime));
            }
        }

        HttpManage.getInstance().newFeedPlan(planId, deviceId, curRecycler, delDietList, addDietList, editDietList, svPlanStart.isOpened() ? "0" : "1", new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d(response);
                BaseEntity result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity>() {
                }.getType());
                if (result.getCode() == 200) {
                    finish();
                } else {
                    showToast(result.getMsg());
                }
            }
        });
    }
}
