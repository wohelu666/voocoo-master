package com.voocoo.pet.modules.main;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;
import com.voocoo.pet.R;
import com.voocoo.pet.base.fragment.AbsBaseFragment;
import com.voocoo.pet.base.presenter.BaseFragmentPresenter;
import com.voocoo.pet.common.manager.DevicesManager;
import com.voocoo.pet.common.manager.PetsManager;
import com.voocoo.pet.common.utils.LogUtil;
import com.voocoo.pet.common.utils.SharedPreferencesUtil;
import com.voocoo.pet.common.utils.ToastUtil;
import com.voocoo.pet.common.widgets.AppDialog;
import com.voocoo.pet.common.widgets.verlayAdapter.BaseOverlayPageAdapter;
import com.voocoo.pet.common.widgets.verlayAdapter.SimpleOverlayAdapter;
import com.voocoo.pet.entity.BaseEntity;
import com.voocoo.pet.entity.Device;
import com.voocoo.pet.entity.WaitToDoList;
import com.voocoo.pet.http.HttpManage;
import com.voocoo.pet.modules.add.activity.AddDeviceStepThreeActivity;
import com.voocoo.pet.modules.add.activity.AddDeviceStepTwoActivity;
import com.voocoo.pet.modules.add.activity.AddDeviceSuccessActivity;
import com.voocoo.pet.modules.add.activity.ScanDeviceActivity;
import com.voocoo.pet.modules.dev.activity.FeedDevActivity;
import com.voocoo.pet.modules.dev.activity.WaitToDoActivity;
import com.voocoo.pet.modules.dev.activity.WaterDevActivity;
import com.voocoo.pet.modules.main.adapter.TodoListAdapter;
import com.voocoo.pet.modules.mine.SettingActivity;
import com.voocoo.pet.modules.user.LoginByCodeActivity;

import org.apache.http.Header;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


public class HomeFragment extends AbsBaseFragment {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tv_devices)
    TextView tvDevices;

    @BindView(R.id.ly_main_view)
    View mainView;
    @BindView(R.id.ly_empty_view)
    View emptyView;
    @BindView(R.id.tv_not_todo)
    View tvNotTodo;

    @BindView(R.id.tv_wait_to_do_num)
    TextView tvWaitTodoNum;

    @BindView(R.id.ly_hav_todo)
    View lyTodo;

    @BindView(R.id.rv_todo)
    LinearLayout rvTodo;

    @BindView(R.id.ly_wait_todo_item2)
    View waitTodoItem2;

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_content2)
    TextView tvContent2;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_time2)
    TextView tvTime2;
    // TodoListAdapter todoListAdapter;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    static int devNum = 0;
    private int lastUseDevId = 0;

    private List<WaitToDoList.WaitToDo> waitToDoList = new ArrayList<>();

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat format2 = new SimpleDateFormat("HH:mm");

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDevList();
        getWaitTodoList();
    }

    @OnClick({R.id.ly_wait_todo_item1, R.id.ly_wait_todo_item2, R.id.iv_add, R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
            case R.id.btn_add:
                startActivity(new Intent(getContext(), ScanDeviceActivity.class));
                break;
            case R.id.ly_wait_todo_item1:
            case R.id.ly_wait_todo_item2:
                startActivity(new Intent(getContext(), WaitToDoActivity.class));
                break;
        }
    }

    private void getWaitTodoList() {
        HttpManage.getInstance().getWaitTodo(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("getWaitTodo onError");
            }

            @Override
            public void onSuccess(int code, String response) {
                LogUtil.d("getWaitTodo->" + response);
                BaseEntity<WaitToDoList> result = new Gson().fromJson(response, new TypeToken<BaseEntity<WaitToDoList>>() {
                }.getType());

                if (result.getCode() == 200) {
                    waitToDoList = new ArrayList<>();
                    waitToDoList.addAll(result.getData().waterToDoList);
                    waitToDoList.addAll(result.getData().feederToDoList);
                    if (waitToDoList.size() == 0) {
                        tvNotTodo.setVisibility(View.VISIBLE);
                        lyTodo.setVisibility(View.GONE);
                    } else {
                        tvNotTodo.setVisibility(View.GONE);
                        lyTodo.setVisibility(View.VISIBLE);
                    }
                    tvWaitTodoNum.setText(waitToDoList.size() + "");
                    if (waitToDoList.size() >= 2) {
                        //todoListAdapter.setData(waitToDoList.subList(0, 2));
                        setWaitTodoView(waitToDoList.subList(0, 2));
                    } else {
                        //todoListAdapter.setData(waitToDoList);
                        setWaitTodoView(waitToDoList);
                    }

                }
            }
        });
    }

    private void setWaitTodoView(List<WaitToDoList.WaitToDo> datas) {
        if (datas.size() <= 0) {
            return;
        }
        if (datas.size() == 1) {
            waitTodoItem2.setVisibility(View.GONE);
            tvContent.setText(datas.get(0).content);
            try {
                Date date = format.parse(datas.get(0).time);
                tvTime.setText(format2.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            waitTodoItem2.setVisibility(View.VISIBLE);
            tvContent.setText(datas.get(0).content);
            try {
                Date date = format.parse(datas.get(0).time);
                tvTime.setText(format2.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvContent2.setText(datas.get(1).content);
            try {
                Date date = format.parse(datas.get(1).time);
                tvTime2.setText(format2.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDevList() {
        HttpManage.getInstance().devList(new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                LogUtil.d("devList onError");
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int code, String response) {
                swipeRefreshLayout.setRefreshing(false);
                LogUtil.d("devList->" + response);
                BaseEntity<List<Device>> result = new Gson().fromJson(response, new TypeToken<BaseEntity<List<Device>>>() {
                }.getType());

                if (result.getCode() == 200) {
                    if (result.getData().size() > 0) {
                        DevicesManager.getInstance().setDeviceList(result.getData());
                        emptyView.setVisibility(View.GONE);
                        mainView.setVisibility(View.VISIBLE);
                        devNum = result.getData().size();
                        tvDevices.setText(getString(R.string.text_bind_devices, devNum));
                        SimpleOverlayAdapter adapter = new SimpleOverlayAdapter(getContext());
                        adapter.setDevicesAndBindViewPager(viewPager, result.getData(), devNum);
                        adapter.setClickDevListener(new BaseOverlayPageAdapter.ClickDevListener() {
                            @Override
                            public void onClick(Device device) {
                                lastUseDevId = device.getDeviceId();
                                if (device.getDeviceType().equals("0")) {
                                    //饮水机
                                    Intent intent = new Intent(getContext(), WaterDevActivity.class);
                                    intent.putExtra("device", device);
                                    startActivity(intent);
                                } else if (device.getDeviceType().equals("1")) {
                                    Intent intent = new Intent(getContext(), FeedDevActivity.class);
                                    intent.putExtra("device", device);
                                    startActivity(intent);
                                }
                            }
                        });
                        viewPager.setAdapter(adapter);
                        int index = 0;
                        for (int i = 0; i < result.getData().size(); i++) {
                            if (result.getData().get(i).getDeviceId() == lastUseDevId) {
                                index = i;
                            }
                        }
                        viewPager.setCurrentItem(index); //伪无限循环
                    } else {
                        DevicesManager.getInstance().setDeviceList(new ArrayList<>());
                        emptyView.setVisibility(View.VISIBLE);
                        mainView.setVisibility(View.GONE);
                    }
                } else {
                    if("登录状态已过期".equals(result.getMsg())){
                        JPushInterface. stopPush(getActivity());
                        JPushInterface.deleteAlias(getActivity(),1);
                        //反注册
                        PetsManager.getInstance().deletePet();
                        SharedPreferencesUtil.keepShared("password", "");
                        SharedPreferencesUtil.keepShared("token", "");

                        Intent intent = new Intent(getActivity(), LoginByCodeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    emptyView.setVisibility(View.VISIBLE);
                    mainView.setVisibility(View.GONE);
                    ToastUtil.getInstance().shortToast(result.getMsg());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDevList();
                getWaitTodoList();
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected int initLayoutID() {
        return R.layout.fragment_home;
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected BaseFragmentPresenter createPresenter() {
        return null;
    }

}
