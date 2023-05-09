package com.voocoo.pet.modules.dev.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.BindDevSmartFeedPlanFinishEvent;
import com.voocoo.pet.common.event.PetUpdateEvent;
import com.voocoo.pet.common.event.SmartPlanSaveEvent;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.BaseRowsEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.Diet;
import com.voocoo.pet.entity.FeedPlan;
import com.voocoo.pet.entity.Pet;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.adapter.PetListAdapter;
import com.voocoo.pet.modules.pet.AddPetActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class SmartFeedPlanActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.rv_pet)
    RecyclerView rvPet;

    @BindView(R.id.tv_not_pet)
    TextView tvNotPet;

    PetListAdapter petListAdapter;

    Device device;
    int planNumber = -1;
    int planId;

    List<Integer> curRecycler = null;
    List<FeedPlan.FeedPlanDiets> feedPlanDiets = new ArrayList<>();
    List<FeedPlan.FeedPlanDiets> delFeedPlanDiets = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        device = (Device) getIntent().getSerializableExtra("device");
        planNumber = getIntent().getIntExtra("plan", -1);
        petListAdapter = new PetListAdapter(SmartFeedPlanActivity.this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        HttpManage.getInstance().petList(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                cn.forward.androids.utils.LogUtil.d("petList onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                cn.forward.androids.utils.LogUtil.d("petList->" + response);
                BaseRowsEntity<List<Pet>> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseRowsEntity<List<Pet>>>() {
                }.getType());
                if (result.getCode() == 200) {
                    PetsManager.getInstance().setPetList(result.getRows());
                    rvPet.setLayoutManager(new LinearLayoutManager(SmartFeedPlanActivity.this));
                    rvPet.setAdapter(petListAdapter);
                    petListAdapter.setData(result.getRows());
                    if (PetsManager.getInstance().getPetList().size() == 0) {
                        rvPet.setVisibility(View.GONE);
                        tvNotPet.setVisibility(View.VISIBLE);
                    } else {
                        rvPet.setVisibility(View.VISIBLE);
                        tvNotPet.setVisibility(View.GONE);
                    }
//                    EventBus.getDefault().post(new PetUpdateEvent());
                } else {
                    showToast(result.getMsg());
                }
            }
        });

        if (device == null) {
            return;
        }
//        showLoading();
//        HttpManage.getInstance().getDevFeedPlan(device.getDeviceId(), new HttpManage.ResultCallback<String>() {
//            @Override
//            public void onError(Header[] headers, HttpManage.Error error) {
//                dismissLoading();
//                LogUtil.d("getDevFeedPlan onError");
//            }
//
//            @Override
//            public void onSuccess(int code, String response) {
//                dismissLoading();
//                LogUtil.d("getDevFeedPlan->" + response);
//                BaseEntity<FeedPlan> result = new Gson().fromJson(response, new TypeToken<BaseEntity<FeedPlan>>() {
//                }.getType());
//                if (result.getCode() == 200) {
//                    //tvLeftFood.setText(result.getData().feedingTimes + "");
//                    //tvPlanOutFood.setText(result.getData().feedingAmount + "");
//                    curRecycler = result.getData().feederPlanCycle;
//                    planId = result.getData().feederPlanId;
//                    delFeedPlanDiets.addAll(result.getData().planDiets);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StyledDialog.init(this);
//        List<Pet> petList = PetsManager.getInstance().getPetList();
//        petListAdapter.setData(petList);
        initView();
        initData();
    }

    @OnClick({R.id.btn_ensure, R.id.btn_add_pet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ensure:
                if (petListAdapter.getPetIds().size() == 0) {
                    showToast("请选择宠物");
                    return;
                }
                if (planNumber != 0 && planNumber != -1) {
                    StyledDialog.buildIosAlert(getString(R.string.text_use_smart_feed_plan_tips), "", new MyDialogListener() {
                        @Override
                        public void onFirst() {
                            getSmartPlanList();
                        }

                        @Override
                        public void onSecond() {

                        }
                    }).show();
                } else if (planNumber == 0) {
                    getSmartPlanList();
                }


                break;
            case R.id.btn_add_pet:
                startActivity(new Intent(SmartFeedPlanActivity.this, AddPetActivity.class));
                break;
        }
    }

    private void getSmartPlanList() {
        showLoading();
        String petIds = StringUtils.join(petListAdapter.getPetIds(), ",");
        Log.e("TAG", "getSmartPlanList: " + petIds);
        Log.e("TAG", "getSmartPlanList@@@@@: " + petListAdapter.getPetIds().get(0) + "");

//        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(true, 80, 443);
//        RequestParams params = new RequestParams();
//        params.put("petIds", petIds);
//        params.put("userId", SharedPreferencesUtil.queryIntValue("userId"));
//        Map<String, String> head = new HashMap<>();
//        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//        head.put("x-terminal-type", "Android");
//        head.put("Authorization", SharedPreferencesUtil.queryValue("token"));
//        asyncHttpClient.get(SmartFeedPlanActivity.this,"http://106.52.132.14:8081/ownerApp/plan/generateSmartPlan",HttpManage.getInstance().map2Header(head), params, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                dismissLoading();
//                Log.e("TAG", "onFailure: " + throwable.toString() );
//            }
//
//            @Override
//            public void onSuccess(int i, Header[] headers, String s) {
//                dismissLoading();
//                LogUtil.d(s);
//                BaseEntity<FeedPlan> result = new Gson().fromJson(s, new TypeToken<BaseEntity<FeedPlan>>() {
//                }.getType());
//                if (result.getCode() == 200) {
//                    //tvLeftFood.setText(result.getData().feedingTimes + "");
//                    //tvPlanOutFood.setText(result.getData().feedingAmount + "");
//                    feedPlanDiets.addAll(result.getData().planDiets);
//                    // curRecycler = result.getData().feederPlanCycle;
//                    //将数据传回去
//                    EventBus.getDefault().post(new BindDevSmartFeedPlanFinishEvent(feedPlanDiets));
//                    finish();
//                    return;
//
//                    //save();
//                } else {
//                    dismissLoading();
//                    showToast(result.getMsg());
//                }
//            }
//        });


        HttpManage.getInstance().generateSmartPlan(petIds, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d(response);
                BaseEntity<FeedPlan> result = new Gson().fromJson(response, new TypeToken<BaseEntity<FeedPlan>>() {
                }.getType());
                if (result.getCode() == 200) {
                    //tvLeftFood.setText(result.getData().feedingTimes + "");
                    //tvPlanOutFood.setText(result.getData().feedingAmount + "");
                    feedPlanDiets.addAll(result.getData().planDiets);
                    // curRecycler = result.getData().feederPlanCycle;
                    //将数据传回去
                    EventBus.getDefault().post(new BindDevSmartFeedPlanFinishEvent(feedPlanDiets));
                    finish();
//                    save();
                    return;


                } else {
                    dismissLoading();
                    showToast(result.getMsg());
                }
            }
        });
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPetListUpdate(PetUpdateEvent event) {
        petListAdapter.setData(PetsManager.getInstance().getPetList());
        if (PetsManager.getInstance().getPetList().size() == 0) {
            rvPet.setVisibility(View.GONE);
            tvNotPet.setVisibility(View.VISIBLE);
        } else {
            rvPet.setVisibility(View.VISIBLE);
            tvNotPet.setVisibility(View.GONE);
        }
    }


    private void initView() {
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_smart_feed_plan));


    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_smart_feed_plan;
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

    private void save() {
        //匹配后保存
        List<Integer> delDietList = new ArrayList<>();

        for (int i = 0; i < delFeedPlanDiets.size(); i++) {
            delDietList.add(delFeedPlanDiets.get(i).dietId);
        }

        List<Diet> addDietList = new ArrayList<>();
        for (int i = 0; i < feedPlanDiets.size(); i++) {
            addDietList.add(new Diet(feedPlanDiets.get(i).dietAmount, feedPlanDiets.get(i).dietTag, feedPlanDiets.get(i).dietTime));
        }

        HttpManage.getInstance().newFeedPlan(planId, device.getDeviceId(), curRecycler, delDietList, addDietList, new ArrayList<>(), "0", new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                dismissLoading();
                LogUtil.d("onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d(response);
                BaseEntity result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity>() {
                }.getType());
                if (result.getCode() == 200) {
                    EventBus.getDefault().post(new SmartPlanSaveEvent());
                    finish();
                } else {
                    showToast(result.getMsg());
                }
            }
        });
    }

}
