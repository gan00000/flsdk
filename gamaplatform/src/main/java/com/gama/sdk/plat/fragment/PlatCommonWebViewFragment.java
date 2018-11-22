package com.gama.sdk.plat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gama.sdk.R;
import com.gama.sdk.SWebViewFragment;


public class PlatCommonWebViewFragment extends SWebViewFragment {

    private String webTitle;
    private View titleLayout;

    TextView titleView;
    View closeView;
    View backView;

    private boolean isShowBackView;
    private boolean isShowTitleView = true;

    public void setShowBackView(boolean showBackView) {
        isShowBackView = showBackView;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public boolean isShowTitleView() {
        return isShowTitleView;
    }

    public void setShowTitleView(boolean showTitleView) {
        isShowTitleView = showTitleView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private View.OnClickListener deleteViewOnClickListener;

    public void setDeleteViewOnClickListener(View.OnClickListener deleteViewOnClickListener) {
        this.deleteViewOnClickListener = deleteViewOnClickListener;
    }

    @Override
    public View onCreateViewForTitle(LayoutInflater inflater, @Nullable ViewGroup container,View contentView) {
        titleLayout = inflater.inflate(R.layout.plat_menu_content_title_layout,container,false);
        backView = titleLayout.findViewById(R.id.plat_title_back_tv);
        if (isShowBackView) {
            backView.setVisibility(View.VISIBLE);
        }else {
            backView.setVisibility(View.GONE);
        }
        closeView = titleLayout.findViewById(R.id.plat_title_close_tv);
        titleView = (TextView) titleLayout.findViewById(R.id.plat_title_tv);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        if (isShowTitleView){
            titleLayout.setVisibility(View.VISIBLE);
        }else {
            titleLayout.setVisibility(View.GONE);
        }

        if (deleteViewOnClickListener != null) {
            View deleteView = contentView.findViewById(R.id.s_base_web_view_delete);
            deleteView.setVisibility(View.VISIBLE);
            deleteView.setOnClickListener(deleteViewOnClickListener);
        }


        return titleLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (titleView != null){
            titleView.setText(webTitle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
