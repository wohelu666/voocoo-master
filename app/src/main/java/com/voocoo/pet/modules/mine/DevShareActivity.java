package com.voocoo.pet.modules.mine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.common.widgets.verlayAdapter.BaseOverlayPageAdapter;
import com.voocoo.pet.common.widgets.verlayAdapter.SimpleOverlayAdapter;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.DevShare;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.dev.activity.FeedDevActivity;
import com.voocoo.pet.modules.dev.activity.WaterDevActivity;
import com.voocoo.pet.modules.dev.adapter.ShareDevListAdapter;

import org.apache.http.Header;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class DevShareActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.ly_not_dev)
    View notDev;

    @BindView(R.id.rv_dev)
    RecyclerView recyclerView;

    ShareDevListAdapter shareListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dev_share;
    }

    /**
     * 检测是否安装微信 * * @param context * @return
     */
    public static boolean isWxInstall(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_black_ic);
        tvTitle.setTextColor(getResources().getColor(R.color.color_000000));
        tvTitle.setText(getString(R.string.text_home_share));

        showLoading();
        HttpManage.getInstance().devList(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("devList onError");
                dismissLoading();
            }

            @Override
            public void onSuccess(int code, String response) {
                dismissLoading();
                LogUtil.d("devList->" + response);
                BaseEntity<List<Device>> result = new Gson().fromJson(response, new com.google.gson.reflect.TypeToken<BaseEntity<List<Device>>>() {
                }.getType());

                if (result.getCode() == 200) {
                    if (result.getData().size() > 0) {
                        DevicesManager.getInstance().setDeviceList(result.getData());
                    }else{
                        DevicesManager.getInstance().setDeviceList(new ArrayList<>());
                    }
                }

                if (DevicesManager.getInstance().getDeviceList().size() == 0) {
                    notDev.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    notDev.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    shareListAdapter = new ShareDevListAdapter(DevShareActivity.this);
                    shareListAdapter.setData(DevicesManager.getInstance().getDeviceList());

                    recyclerView.setLayoutManager(new LinearLayoutManager(DevShareActivity.this));
                    recyclerView.setAdapter(shareListAdapter);
                    shareListAdapter.setCancelShareListener(new ShareDevListAdapter.ShareListener() {
                        @Override
                        public void onShare(Device shareBean) {
                            Intent intent = new Intent(DevShareActivity.this, DevShareManagerActivity.class);
                            intent.putExtra("device", shareBean);
                            startActivity(intent);
                        }
                    });
                }

                initData();
            }
        });
    }

    private void initData() {
        for (int i = 0; i < DevicesManager.getInstance().getDeviceList().size(); i++) {
            int finalI = i;
            HttpManage.getInstance().devShareList(DevicesManager.getInstance().getDeviceList().get(i).getDeviceId(), new HttpManage.ResultCallback<String>() {
                @Override
                public void onError(Header[] headers, HttpManage.Error error) {

                }

                @Override
                public void onSuccess(int code, String response) {
                    BaseEntity<List<DevShare>> result = new Gson().fromJson(response, new TypeToken<BaseEntity<List<DevShare>>>() {
                    }.getType());
                    if (result.getCode() == 200) {
                        DevicesManager.getInstance().getDeviceList().get(finalI).setDevShareList(result.getData());
                        shareListAdapter.setData(DevicesManager.getInstance().getDeviceList());
                    }
                    LogUtil.d(response);
                }
            });
        }
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