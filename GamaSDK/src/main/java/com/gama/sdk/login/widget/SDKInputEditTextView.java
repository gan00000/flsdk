package com.gama.sdk.login.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gama.sdk.R;

public class SDKInputEditTextView extends RelativeLayout {


    private LayoutInflater inflater;

    private ImageView iconImage;
    private ImageView eyeImageView;
    private TextView labTextView;
    private EditText inputEditText;

    public SDKInputEditTextView(Context context) {
        super(context);
        initInputView();
    }

    public SDKInputEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInputView();
    }

    public SDKInputEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInputView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SDKInputEditTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInputView();
    }

    private void initInputView(){

        inflater = LayoutInflater.from(getContext());
        View contentView =  inflater.inflate(R.layout.sdk_input_item_et, null);
        if (contentView != null) {

            LayoutParams l = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(contentView, l);

            iconImage = contentView.findViewById(R.id.sdk_input_item_icon);
            labTextView = contentView.findViewById(R.id.sdk_input_item_lable);
            eyeImageView = contentView.findViewById(R.id.sdk_input_item_eye);
            inputEditText = contentView.findViewById(R.id.sdk_input_item_et);

            eyeImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eyeImageView.isSelected()) {
                        eyeImageView.setSelected(false);
                        // 显示为普通文本
                        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        eyeImageView.setSelected(true);
                        // 显示为密码
                        inputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    // 使光标始终在最后位置
                    Editable etable = inputEditText.getText();
                    Selection.setSelection(etable, etable.length());
                }
            });
        }

    }


    //设置输入框的类型
    public void setInputType(SDKInputType inputType){

        int iconId = 0;
        String labName = "";
        int editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        String hint = "";
        switch (inputType){

            case SDKInputType_Account:
                iconId = R.drawable.fl_sdk_ren;
                labName = getResources().getString(R.string.py_account);
                eyeImageView.setVisibility(View.GONE);
                break;

            case SDKInputType_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_password);
                eyeImageView.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case SDKInputType_Old_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_old_pwd);
                eyeImageView.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case SDKInputType_New_Password:
                iconId = R.drawable.fl_sdk_suo;
                labName = getResources().getString(R.string.py_new_pwd);
                eyeImageView.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;

            case SDKInputType_Vf_Code:
                iconId = R.drawable.fl_sdk_dx;
                editTextInputType = InputType.TYPE_CLASS_NUMBER;
                eyeImageView.setVisibility(View.GONE);
                break;

            default:
        }

    iconImage.setImageResource(iconId);
    labTextView.setText(labName);
    inputEditText.setInputType(editTextInputType);
    }

}
