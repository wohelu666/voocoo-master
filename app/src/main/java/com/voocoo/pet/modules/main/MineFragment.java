package com.voocoo.pet.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.umeng.commonsdk.debug.I;
import com.voocoo.pet.R;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;
import com.voocoo.pet.common.event.AddDeviceSuccessEvent;
import com.voocoo.pet.common.event.PetUpdateEvent;
import com.voocoo.pet.common.event.RefreshPetEvent;
import com.voocoo.pet.common.event.UpdateDevNumEvent;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.widgets.RoundImageView;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.BaseRowsEntity;
import com.voocoo.pet.entity.Pet;
import com.voocoo.pet.entity.UserInfo;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.mine.AboutUsActivity;
import com.voocoo.pet.modules.mine.BaiKeActivity;
import com.voocoo.pet.modules.mine.DevShareActivity;
import com.voocoo.pet.modules.mine.FeedbackActivity;
import com.voocoo.pet.modules.mine.PersonInfoActivity;
import com.voocoo.pet.modules.mine.SettingActivity;
import com.voocoo.pet.modules.pet.AddPetActivity;
import com.voocoo.pet.modules.pet.PetInfoActivity;
import com.voocoo.pet.modules.shop.ShopListActivity;
import com.voocoo.pet.modules.web.WebActivity;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.forward.androids.utils.LogUtil;

public class MineFragment extends AbsBaseFragment {

    @BindView(R.id.ly_pet)
    LinearLayout lyPet;

    @BindView(R.id.tv_nickname)
    TextView tvNickName;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.iv_head)
    RoundImageView ivHead;
    @BindView(R.id.tv_dev_num)
    TextView tvDevNum;

    @BindView(R.id.tv_add_pet)
    View tvAddPet;

    @BindView(R.id.ly_baike)
    View lyBaike;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        getData();
        setPetBanner(new ArrayList<>());

        if (System.currentTimeMillis()/1000 < 1670947200) {
            lyBaike.setVisibility(View.GONE);
        } else {
            lyBaike.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshPet(RefreshPetEvent event) {
        getData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销前判断是否已经注册
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void setPetBanner(List<Pet> mPetList) {
        lyPet.removeAllViews();

        for (int j = 0; j < mPetList.size(); j++) {
            View view = getLayoutInflater().inflate(R.layout.item_dev_banner, null);
            RoundImageView ivHead = view.findViewById(R.id.iv_head);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.app_icon);
            Glide.with(getContext()).load(mPetList.get(j).petImg).apply(options).into(ivHead);
            TextView tvNickNam = view.findViewById(R.id.tv_nickname);
            tvNickNam.setText(mPetList.get(j).petNickname);
            lyPet.addView(view);
            int finalJ = j;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PetInfoActivity.class);
                    intent.putExtra("pet", mPetList.get(finalJ));
                    startActivity(intent);
                }
            });
        }

        if (mPetList.size() < 4) {
            View view = getLayoutInflater().inflate(R.layout.item_dev_banner, null);
            RoundImageView ivHead = view.findViewById(R.id.iv_head);
            ivHead.setImageResource(R.mipmap.add_round);
            TextView tvNickNam = view.findViewById(R.id.tv_nickname);
            tvNickNam.setText("");
            tvNickNam.setVisibility(View.GONE);
            lyPet.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), AddPetActivity.class));
                }
            });
            tvAddPet.setVisibility(View.GONE);
        } else {
            tvAddPet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tvDevNum.setText(getString(R.string.text_add_x_device, DevicesManager.getInstance().getDeviceList().size() + ""));
        HttpManage.getInstance().getUserInfo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getUserInfo->" + response);
                BaseEntity<UserInfo> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity<UserInfo>>() {
                }.getType());
                if (result.getCode() == 200) {
                    SharedPreferencesUtil.keepShared("userId", result.getData().getUserId());
                    SharedPreferencesUtil.keepShared("phone", result.getData().getPhoneNumber());
                    SharedPreferencesUtil.keepShared("userName", result.getData().getNickName());

                    tvNickName.setText(result.getData().getNickName());
                    ivSex.setImageResource(result.getData().getSex() == 0 ? R.mipmap.men : R.mipmap.women);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .priority(Priority.HIGH)
                            .placeholder(R.mipmap.app_icon)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    Glide.with(getContext()).load(result.getData().getPhoto()).apply(options).into(ivHead);
                }
            }
        });
    }


    private void getData() {
        HttpManage.getInstance().petList(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("petList onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("petList->" + response);
                BaseRowsEntity<List<Pet>> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseRowsEntity<List<Pet>>>() {
                }.getType());
                if (result.getCode() == 200) {
                    setPetBanner(result.getRows());
                    PetsManager.getInstance().setPetList(result.getRows());
                    EventBus.getDefault().post(new PetUpdateEvent());
                }
            }
        });
    }

    @OnClick({R.id.tv_huiyuan, R.id.tv_order, R.id.ly_about_us, R.id.ly_top, R.id.ly_setting, R.id.tv_add_pet, R.id.ly_feedback, R.id.ly_share, R.id.ly_baike})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_huiyuan:
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("url", "https://shop91447720.youzan.com/wsctrade/cart?kdt_id=91255552&shopAutoEnter=1");
                startActivity(intent);
                break;
            case R.id.tv_order:
                intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("url", "https://shop91447720.youzan.com/wsctrade/order/list?sub_kdt_id=91255552&kdt_id=91255552&type=all");
                startActivity(intent);
                break;
            case R.id.ly_about_us:
                startActivity(new Intent(getContext(), AboutUsActivity.class));
                break;
            case R.id.ly_baike:
                startActivity(new Intent(getContext(), BaiKeActivity.class));
                break;
            case R.id.ly_share:
                startActivity(new Intent(getContext(), DevShareActivity.class));
                break;
            case R.id.ly_feedback:
                startActivity(new Intent(getContext(), FeedbackActivity.class));
                break;
            case R.id.ly_setting:
                startActivity(new Intent(getContext(), SettingActivity.class));
                break;
            case R.id.ly_top:
                startActivity(new Intent(getContext(), PersonInfoActivity.class));
                break;
            case R.id.tv_add_pet:
                startActivity(new Intent(getContext(), AddPetActivity.class));
                break;
        }
    }

    @Override
    protected int initLayoutID() {
        return R.layout.fragment_mine;
    }

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateNum(UpdateDevNumEvent event) {
        tvDevNum.setText(getString(R.string.text_add_x_device, DevicesManager.getInstance().getDeviceList().size() + ""));
    }
}
