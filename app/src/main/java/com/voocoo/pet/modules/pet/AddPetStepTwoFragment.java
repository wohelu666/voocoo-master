package com.voocoo.pet.modules.pet;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.voocoo.pet.R;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class AddPetStepTwoFragment extends AbsBaseFragment {

    @BindView(R.id.iv_miao_xing_ren)
    ImageView ivMiaoXingRen;
    @BindView(R.id.iv_wang_xing_ren)
    ImageView ivWangXingRen;

    @BindView(R.id.iv_gg)
    ImageView ivGG;
    @BindView(R.id.iv_mm)
    ImageView ivMM;

    private int type = -1;
    private int sex = -1;

    public int getType() {
        return type;
    }

    public int getSex() {
        return sex;
    }

    @OnClick({R.id.rl_miao_xing_ren, R.id.rl_wang_xing_ren, R.id.rl_gg, R.id.rl_mm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_miao_xing_ren:
                type = 0;
                ivMiaoXingRen.setImageResource(R.mipmap.checkbox_select);
                ivWangXingRen.setImageResource(R.mipmap.check_box_normal);
                break;
            case R.id.rl_wang_xing_ren:
                type = 1;
                ivWangXingRen.setImageResource(R.mipmap.checkbox_select);
                ivMiaoXingRen.setImageResource(R.mipmap.check_box_normal);
                break;
            case R.id.rl_gg:
                sex = 0;
                ivGG.setImageResource(R.mipmap.checkbox_select);
                ivMM.setImageResource(R.mipmap.check_box_normal);
                break;
            case R.id.rl_mm:
                sex = 1;
                ivMM.setImageResource(R.mipmap.checkbox_select);
                ivGG.setImageResource(R.mipmap.check_box_normal);
                break;
        }
    }


    @Override
    protected int initLayoutID() {
        return R.layout.fragment_add_pet_step_two;
    }

    public static AddPetStepTwoFragment newInstance() {
        return new AddPetStepTwoFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
