package com.gama.sdk.login.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gama.sdk.R;

class SDKClickButtion extends RelativeLayout {


    private LayoutInflater inflater;

    private ImageView iconImage;
    private ImageView eyeImage;
    private TextView labTextView;
    private EditText inputEditText;

    public SDKClickButtion(Context context) {
        super(context);
        initInputView();
    }

    public SDKClickButtion(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInputView();
    }

    public SDKClickButtion(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInputView();
    }


    private void initInputView(){

        inflater = LayoutInflater.from(getContext());
        View contentView =  inflater.inflate(R.layout.sdk_input_item_et, null);
        if (contentView != null) {

            LayoutParams l = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(contentView, l);

            iconImage = contentView.findViewById(R.id.sdk_input_item_icon);
            labTextView = contentView.findViewById(R.id.sdk_input_item_lable);
            eyeImage = contentView.findViewById(R.id.sdk_input_item_eye);
            inputEditText = contentView.findViewById(R.id.sdk_input_item_et);
        }

    }


    //设置输入框的类型
    public void setInputType(SDKInputType inputType){

        int iconId = 0;
        String labName = "";

        switch (inputType){

            case SDKInputType_Account:
                iconId = R.drawable.fl_sdk_ren;
                labName = getResources().getString(R.string.py_account);
                break;

            case SDKInputType_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_password);
                break;
            case SDKInputType_Old_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_old_pwd);
                break;
            case SDKInputType_New_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_new_pwd);
                break;

            case SDKInputType_Vf_Code:
                iconId = R.drawable.fl_sdk_dx;
                break;

            default:
        }

    iconImage.setImageResource(iconId);
    labTextView.setText(labName);

    }

}
