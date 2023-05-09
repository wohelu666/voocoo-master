package com.voocoo.pet.modules.pet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.adapter.BaseFragmentAdapter;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.event.AddPetControlEvent;
import com.voocoo.pet.common.event.PetUpdateEvent;
import com.voocoo.pet.common.event.RefreshPetEvent;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.widgets.NoScrollViewPager;
import com.voocoo.pet.entity.BaseRowsEntity;
import com.voocoo.pet.entity.Pet;
import com.voocoo.pet.http.HttpManage;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AddPetActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.view3)
    View view3;

    private int step = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);

        initView();
        setupViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    AddPetStepOneFragment addPetStepOneFragment;
    AddPetStepTwoFragment addPetStepTwoFragment;
    AddPetStepThreeFragment addPetStepThreeFragment;

    private void setupViewPager(NoScrollViewPager viewPager) {
        ArrayList<AbsBaseFragment> fragList = new ArrayList<>();
        addPetStepOneFragment = AddPetStepOneFragment.newInstance();
        addPetStepTwoFragment = AddPetStepTwoFragment.newInstance();
        addPetStepThreeFragment = AddPetStepThreeFragment.newInstance();
        fragList.add(addPetStepOneFragment);
        fragList.add(addPetStepTwoFragment);
        fragList.add(addPetStepThreeFragment);

        ArrayList<CharSequence> fragTags = new ArrayList<>();
        fragTags.add(getString(R.string.tab_home));
        fragTags.add(getString(R.string.tab_mail));
        fragTags.add(getString(R.string.tab_mine));

        BaseFragmentAdapter adapter = new BaseFragmentAdapter<>(getSupportFragmentManager(), fragList, fragTags);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(0);
    }

    public void setBtnEnabled(boolean isEnabled) {
        if (isEnabled) {
            btnNext.setEnabled(true);
            btnNext.setBackgroundResource(R.drawable.bg_btn_able);
        } else {
            btnNext.setEnabled(false);
            btnNext.setBackgroundResource(R.drawable.bg_btn_unable);
        }
    }

    private void setPoi(int poi) {
        if (poi == 0) {
            view1.setBackgroundColor(Color.parseColor("#FB7E37"));
            view2.setBackgroundColor(Color.parseColor("#9FA6BD"));
            view3.setBackgroundColor(Color.parseColor("#9FA6BD"));
            btnNext.setText(getString(R.string.next));
        } else if (poi == 1) {
            view2.setBackgroundColor(Color.parseColor("#FB7E37"));
            view1.setBackgroundColor(Color.parseColor("#9FA6BD"));
            view3.setBackgroundColor(Color.parseColor("#9FA6BD"));
            btnNext.setText(getString(R.string.next));
        } else if (poi == 2) {
            setBtnEnabled(false);
            view3.setBackgroundColor(Color.parseColor("#FB7E37"));
            view1.setBackgroundColor(Color.parseColor("#9FA6BD"));
            view2.setBackgroundColor(Color.parseColor("#9FA6BD"));
            btnNext.setText(getString(R.string.complete));
        }
    }

    private void initView() {
        setPoi(0);
    }

    @OnClick({R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (step == 0) {
                  /*  if (TextUtils.isEmpty(addPetStepOneFragment.getUrl())) {
                        showToast("请选择头像");
                        return;
                    }*/
                    if (TextUtils.isEmpty(addPetStepOneFragment.getNickname())) {
                        showToast("请输入昵称");
                        return;
                    }
                    if (TextUtils.isEmpty(addPetStepOneFragment.getBrand())) {
                        showToast("请输入品种");
                        return;
                    }
                    step = 1;
                    setPoi(1);
                    viewPager.setCurrentItem(1);
                } else if (step == 1) {
                    if (addPetStepTwoFragment.getType() == -1) {
                        showToast("请选择宠物类型");
                        return;
                    }
                    if (addPetStepTwoFragment.getSex() == -1) {
                        showToast("请选择宠物性别");
                        return;
                    }
                    step = 2;
                    setPoi(2);
                    viewPager.setCurrentItem(2);
                } else {
                    //保存
                    showLoading();
                    String head = addPetStepOneFragment.getUrl();
                    String nickName = addPetStepOneFragment.getNickname();
                    String brand = addPetStepOneFragment.getBrand();
                    int type = addPetStepTwoFragment.getType();
                    int sex = addPetStepTwoFragment.getSex();
                    String age = addPetStepThreeFragment.getAgeStr();
                    float weight = addPetStepThreeFragment.getWeight();
                    HttpManage.getInstance().addPet(head, nickName, brand, type + "", sex + "", age, weight, new HttpManage.ResultCallback<String>() {
                        @Override
                        public void onError(Header[] headers, HttpManage.Error error) {
                            dismissLoading();
                            LogUtil.d("onError");
                        }

                        @Override
                        public void onSuccess(int code, String response) {
                            EventBus.getDefault().post(new RefreshPetEvent());
                            //这里先查询下在返回
                            HttpManage.getInstance().petList(new HttpManage.ResultCallback<String>() {
                                @Override
                                public void onError(Header[] headers, HttpManage.Error error) {
                                    dismissLoading();
                                }

                                @Override
                                public void onSuccess(int code, String response) {
                                    dismissLoading();
                                    BaseRowsEntity<List<Pet>> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseRowsEntity<List<Pet>>>() {
                                    }.getType());
                                    if (result.getCode() == 200) {
                                        PetsManager.getInstance().setPetList(result.getRows());
                                        EventBus.getDefault().post(new PetUpdateEvent());
                                        finish();
                                    }
                                }
                            });

                            LogUtil.d("onSuccess=" + response);
                        }
                    });
                }
                break;
        }
    }


    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onControl(AddPetControlEvent event) {
        if (addPetStepThreeFragment.isControl1() && addPetStepThreeFragment.isControl2()) {
            setBtnEnabled(true);
        } else {
            setBtnEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addPetStepOneFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_pet;
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
}
