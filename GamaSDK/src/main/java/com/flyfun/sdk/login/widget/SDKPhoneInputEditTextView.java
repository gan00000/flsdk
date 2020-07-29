package com.flyfun.sdk.login.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyfun.base.bean.GamaAreaInfoBean;
import com.gama.sdk.R;


//封裝SDK電話號碼輸入框
public class SDKPhoneInputEditTextView extends RelativeLayout {


    private LayoutInflater inflater;

    private ImageView iconImage;
    private TextView labTextView, phoneAreaTextView;
    private EditText inputEditText;

    //选中的区域信息
    private GamaAreaInfoBean selectedBean;

    public ImageView getIconImage() {
        return iconImage;
    }

    public TextView getLabTextView() {
        return labTextView;
    }

    public TextView getPhoneAreaTextView() {
        return phoneAreaTextView;
    }

    public EditText getInputEditText() {
        return inputEditText;
    }

    public SDKPhoneInputEditTextView(Context context) {
        super(context);
        initInputView();
    }

    public SDKPhoneInputEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInputView();
    }

    public SDKPhoneInputEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInputView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SDKPhoneInputEditTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInputView();
    }

    private void initInputView(){

        inflater = LayoutInflater.from(getContext());
        View contentView =  inflater.inflate(R.layout.sdk_phone_input_item_et, null);
        if (contentView != null) {

            LayoutParams l = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            l.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(contentView, l);

            iconImage = contentView.findViewById(R.id.sdk_input_item_icon);
            labTextView = contentView.findViewById(R.id.sdk_input_item_lable);
            inputEditText = contentView.findViewById(R.id.sdk_input_item_et);
            phoneAreaTextView = contentView.findViewById(R.id.sdk_input_item_phone_area);

        }

    }

    public String getPhoneAreaCode(){
        return phoneAreaTextView.getText().toString().trim();
    }

    public String getPhoneNumber(){
        return getInputEditText().getEditableText().toString().trim();
    }


   /* public boolean checkPhoneOk(){
        String areaCode = phoneAreaTextView.getText().toString();
        if(TextUtils.isEmpty(areaCode)) {
            ToastUtils.toast(getContext(), R.string.py_area_code_empty);
            return false;
        }
        if (selectedBean == null){
            return false;
        }
        String phone = getInputEditText().getEditableText().toString().trim();
        if(!phone.matches(selectedBean.getPattern())) {
            ToastUtils.toast(getContext(), R.string.py_phone_error);
            return false;
        }

        return true;
    }*/

}
