package com.flyfun.sdk.login.widget.v2;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.flyfun.sdk.login.widget.v2.age.GamaAgeSyleBase;
import com.flyfun.sdk.login.widget.v2.age.impl.GamaAgeImpl;
import com.gama.sdk.R;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class GamaAgeStyleOne extends GamaAgeSyleBase implements View.OnClickListener {

    private TextView gama_age_style_one_confirm;

    public GamaAgeStyleOne(@NonNull Context context, GamaAgeImpl presenter) {
        super(context, presenter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.v2_jp_age_style_one);

        gama_age_style_one_confirm = findViewById(R.id.gama_age_style_one_confirm);

        gama_age_style_one_confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.gama_age_style_one_confirm) {
            dismissSelf();
            if(presenter.getAgeCallback() != null) {
                presenter.getAgeCallback().onFailure();
            }
        }
    }
}
