package com.voocoo.pet.modules.mine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.entity.Baike;
import com.voocoo.pet.modules.main.HomeDetileActivity;
import com.voocoo.pet.modules.main.adapter.HomeListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BaiKeActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.rv_dev)
    RecyclerView recyclerView;

    HomeListAdapter homeListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dev_baike;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_baike));

        homeListAdapter = new HomeListAdapter(this);
        List<Baike> baikeList=new ArrayList<>();
        Baike baike=new Baike("飞机耳","http://pet.voocoo.co:8083/petFile/encyclopedia/AircraftEar.mp4");
        Baike baike2=new Baike("猫咪不能吃的水果","http://pet.voocoo.co:8083/petFile/encyclopedia/CatCanotEatFruits.mp4");
        Baike baike3=new Baike("猫咪能吃的水果","http://pet.voocoo.co:8083/petFile/encyclopedia/CatCanEatFruits.mp4");
        Baike baike4=new Baike("猫薄荷","http://pet.voocoo.co:8083/petFile/encyclopedia/CatMint.mp4");
        Baike baike5=new Baike("猫泪痕","http://pet.voocoo.co:8083/petFile/encyclopedia/CatTears.mp4");
        Baike baike6=new Baike("词典-农民揣","http://pet.voocoo.co:8083/petFile/encyclopedia/Dictionary.mp4");
        Baike baike7=new Baike("猫软便","http://pet.voocoo.co:8083/petFile/encyclopedia/FelineFeces.mp4");
        Baike baike8=new Baike("蒜瓣毛发","http://pet.voocoo.co:8083/petFile/encyclopedia/GarlicCloveHair.mp4");
        Baike baike9=new Baike("母鸡蹲","http://pet.voocoo.co:8083/petFile/encyclopedia/HenSquatting.mp4");
        Baike baike10=new Baike("原始袋","http://pet.voocoo.co:8083/petFile/encyclopedia/OriginalBag.mp4");
        Baike baike11=new Baike("撸猫","http://pet.voocoo.co:8083/petFile/encyclopedia/StrokeACat.mp4");
        Baike baike12=new Baike("适宜温度","http://pet.voocoo.co:8083/petFile/encyclopedia/SuitableTem.mp4");

        baikeList.add(baike);
        baikeList.add(baike2);
        baikeList.add(baike3);
        baikeList.add(baike4);
        baikeList.add(baike5);
        baikeList.add(baike6);
        baikeList.add(baike7);
        baikeList.add(baike8);
        baikeList.add(baike9);
        baikeList.add(baike10);
        baikeList.add(baike11);
        baikeList.add(baike12);
        homeListAdapter.setData(baikeList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(homeListAdapter);
        homeListAdapter.setClickListener(new HomeListAdapter.ClickListener() {
            @Override
            public void onClick(String title,String url) {
                Intent intent=new Intent(BaiKeActivity.this, HomeDetileActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
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