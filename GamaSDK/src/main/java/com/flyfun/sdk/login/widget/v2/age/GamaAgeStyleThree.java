package com.flyfun.sdk.login.widget.v2.age;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flyfun.base.utils.GamaUtil;
import com.gama.sdk.R;
import com.flyfun.sdk.login.widget.v2.age.impl.GamaAgeImpl;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class GamaAgeStyleThree extends GamaAgeSyleBase implements View.OnClickListener {

    private TextView gama_age_style_three_btn1, gama_age_style_three_btn2,
            gama_age_style_three_btn3, gama_age_style_three_close;

    public GamaAgeStyleThree(@NonNull Context context, GamaAgeImpl presenter) {
        super(context, presenter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.v2_jp_age_style_three);
//        this.setCancelable(false);

        gama_age_style_three_btn1 = findViewById(R.id.gama_age_style_three_btn1);
        gama_age_style_three_btn2 = findViewById(R.id.gama_age_style_three_btn2);
        gama_age_style_three_btn3 = findViewById(R.id.gama_age_style_three_btn3);
        gama_age_style_three_close = findViewById(R.id.gama_age_style_three_close);

        gama_age_style_three_btn1.setOnClickListener(this);
        gama_age_style_three_btn2.setOnClickListener(this);
        gama_age_style_three_btn3.setOnClickListener(this);
        gama_age_style_three_close.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        int age = 0;
        if (i == R.id.gama_age_style_three_btn1) {
            age = 15;
            if(presenter != null) {
                presenter.setAge(age);
                presenter.goAgeStyleTwo(getContext());
            }
            dismissSelf();
        } else if (i == R.id.gama_age_style_three_btn2) {
            age = 16;
            if(presenter != null) {
                presenter.setAge(age);
                presenter.goAgeStyleTwo(getContext());
            }
            dismissSelf();
        } else if (i == R.id.gama_age_style_three_btn3) {
            age = 20;
            GamaUtil.saveAgeAndTime(getContext(), age);
            if(presenter != null) {
                presenter.setAge(age);
                if(presenter.getAgeCallback() != null) {
                    presenter.getAgeCallback().canBuy();
                }
            }
            dismissSelf();
        } else if (i == R.id.gama_age_style_three_close) {
            dismissSelf();
            if(presenter.getAgeCallback() != null) {
                presenter.getAgeCallback().onFailure();
            }
        }
    }
}
