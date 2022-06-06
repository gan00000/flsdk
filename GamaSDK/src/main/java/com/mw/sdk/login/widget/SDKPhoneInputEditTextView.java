package com.mw.sdk.login.widget;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.core.base.utils.SStringUtil;
import com.mw.base.bean.GamaAreaInfoBean;
import com.mw.sdk.R;


//封裝SDK電話號碼輸入框
public class SDKPhoneInputEditTextView extends RelativeLayout {


    private LayoutInflater inflater;

    private ImageView iconImage;
    private TextView labTextView, phoneAreaTextView;
    private EditText inputEditText;
    private View araeCodeMoreListView;

    public View getAraeCodeMoreListView() {
        return araeCodeMoreListView;
    }

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

            araeCodeMoreListView = contentView.findViewById(R.id.sdk_input_item_phone_list_icon);

            adjustTvTextSize();
        }

    }

    /** 字体大小适配**/
    private  void adjustTvTextSize()
    {
        final EditText inputEditText = getInputEditText();
        inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (SStringUtil.isEmpty(s.toString())){
                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                }else {
                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }

            }
        });
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
