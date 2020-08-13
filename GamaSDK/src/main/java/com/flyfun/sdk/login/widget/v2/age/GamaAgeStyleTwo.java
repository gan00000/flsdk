package com.flyfun.sdk.login.widget.v2.age;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.gama.sdk.R;
import com.flyfun.sdk.login.widget.v2.age.impl.GamaAgeImpl;

/**
 * Created by GanYuanrong on 2017/2/6.
 */

public class GamaAgeStyleTwo extends GamaAgeSyleBase implements View.OnClickListener {

    private TextView gama_age_style_two_confirm, gama_age_style_two_cancel;

    public GamaAgeStyleTwo(@NonNull Context context, GamaAgeImpl presenter) {
        super(context, presenter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.v2_jp_age_style_two);

        gama_age_style_two_confirm = findViewById(R.id.gama_age_style_two_confirm);
        gama_age_style_two_cancel = findViewById(R.id.gama_age_style_two_cancel);

        gama_age_style_two_confirm.setOnClickListener(this);
        gama_age_style_two_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.gama_age_style_two_confirm) {
            if(presenter != null) {
                presenter.sendAgeRequest(getContext(), this);
            }
        } else if (i == R.id.gama_age_style_two_cancel) {
            if(presenter != null) {
                presenter.goAgeStyleThree(getContext());
                dismissSelf();
            }
        }
    }
}
