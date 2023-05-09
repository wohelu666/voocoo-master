package com.voocoo.pet.modules.dev.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.voocoo.pet.R;
import com.voocoo.pet.base.activity.AbsBaseActivity;
import com.voocoo.pet.base.presenter.BaseActivityPresenter;

import butterknife.BindView;

public class UseHelpActivity extends AbsBaseActivity {

    @BindView(R.id.top_toolbar)
    Toolbar topToolbar;

    @BindView(R.id.top_title)
    TextView tvTitle;

    @BindView(R.id.pdf_view)
    PDFView pdfView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_use_help;
    }

    @Override
    protected boolean isDarkMode() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(topToolbar);
        tvTitle.setTextColor(getColor(android.R.color.black));


        int type = getIntent().getIntExtra("type", 0);
        if (type == 0 || type == 1) {
            tvTitle.setText(getString(R.string.text_instructions));
        } else if (type == 3) {
            tvTitle.setText(getString(R.string.text_privacy));
        } else {
            tvTitle.setText(getString(R.string.text_license));
        }
        //从assets目录读取pdf
        if (type == 0) {
            displayFromAssets("water_help.pdf");
        } else if (type == 1) {
            displayFromAssets("feed_help.pdf");
        } else if (type == 3) {
            displayFromAssets("privacy.pdf");
        } else if (type == 4) {
            displayFromAssets("license.pdf");
        }
    }

    @Nullable
    @Override
    protected BaseActivityPresenter createPresenter() {
        return null;
    }

    private void displayFromAssets(String assetFileName) {
        pdfView.fromAsset(assetFileName)   //设置pdf文件地址
                .swipeHorizontal(false)
                .defaultPage(0)         //设置默认显示第1页
                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                .load();
    }
}