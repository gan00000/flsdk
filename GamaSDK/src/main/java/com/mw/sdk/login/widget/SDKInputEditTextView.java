package com.mw.sdk.login.widget;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.core.base.utils.ToastUtils;
import com.mw.base.utils.GamaUtil;
import com.mw.sdk.R;


//封裝SDK輸入框
public class SDKInputEditTextView extends RelativeLayout {


    private LayoutInflater inflater;

    private ImageView iconImageView;

    public ImageView getIconImageView() {
        return iconImageView;
    }

    private ImageView eyeImageView;
    private TextView labTextView;
    private EditText inputEditText;
    private View eyeImageViewLayout;

    private View line1View;
    private View bottomLine;

    public EditText getInputEditText() {
        return inputEditText;
    }

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

            iconImageView = contentView.findViewById(R.id.sdk_input_item_icon);
            labTextView = contentView.findViewById(R.id.sdk_input_item_lable);
            eyeImageView = contentView.findViewById(R.id.sdk_input_item_eye);
            eyeImageViewLayout = contentView.findViewById(R.id.sdk_input_item_eye_layout);
            inputEditText = contentView.findViewById(R.id.sdk_input_item_et);
            line1View = contentView.findViewById(R.id.sdk_input_item_line1);
            bottomLine = contentView.findViewById(R.id.sdk_input_item_bottom_line);


            eyeImageViewLayout.setOnClickListener(new OnClickListener() {
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
        int editTextInputType = InputType.TYPE_CLASS_TEXT;
        String hint = "";
        switch (inputType){

            case SDKInputType_Account:
                iconId = R.mipmap.mw_account_icon;
                labName = getResources().getString(R.string.py_account);
                hint = getResources().getString(R.string.py_register_account_hit);
                eyeImageViewLayout.setVisibility(View.GONE);
                adjustTvTextSize();
                break;

            case SDKInputType_Password:
                iconId = R.mipmap.mw_passowrd_icon;
                labName = getResources().getString(R.string.py_password);
                hint = getResources().getString(R.string.py_register_password_hit);
                eyeImageViewLayout.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                adjustTvTextSize();
                break;

            case SDKInputType_Password_Again:
                iconId = R.mipmap.mw_passowrd_icon;
                labName = getResources().getString(R.string.py_password);
                hint = getResources().getString(R.string.text_input_new_pwd_confire);
                eyeImageViewLayout.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                adjustTvTextSize();
                break;

            case SDKInputType_Old_Password:
                iconId = R.mipmap.mw_passowrd_icon;
                labName = getResources().getString(R.string.py_old_pwd);
                hint = getResources().getString(R.string.py_input_old_password);
                eyeImageViewLayout.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                adjustTvTextSize();
                break;
            case SDKInputType_New_Password:
                iconId = R.mipmap.mw_passowrd_icon;
                labName = getResources().getString(R.string.py_new_pwd);
                hint = getResources().getString(R.string.text_input_new_pwd);
                eyeImageViewLayout.setVisibility(View.VISIBLE);
                editTextInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                adjustTvTextSize();
                break;

            case SDKInputType_Vf_Code:
                iconId = R.mipmap.mw_account_icon;
                editTextInputType = InputType.TYPE_CLASS_NUMBER;
                eyeImageViewLayout.setVisibility(View.GONE);
                iconImageView.setVisibility(View.GONE);
                line1View.setVisibility(View.GONE);
                bottomLine.setVisibility(View.GONE);
                labName = getResources().getString(R.string.py_vfcode);
                hint = getResources().getString(R.string.py_msg_vfcode_hint);
                adjustTvTextSize();
                break;

            default:
        }

        iconImageView.setImageResource(iconId);
        labTextView.setText(labName);
        inputEditText.setInputType(editTextInputType);
        inputEditText.setHint(hint);

    }

    /** 字体大小适配**/
    private  void adjustTvTextSize()
    {
        final EditText inputEditText = getInputEditText();
//        inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

//                if (SStringUtil.isEmpty(s.toString())){
//                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//                }else {
//                    inputEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                }

            }
        });
    }

    public boolean checkAccount(){

        String account = this.getInputEditText().getEditableText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.toast(getContext(), R.string.py_account_empty);
            return false;
        }
        if (!GamaUtil.checkAccount(account)) {
            ToastUtils.toast(getContext(), R.string.text_account_format, Toast.LENGTH_LONG);
            return false;
        }
        return  true;
    }

   /* public boolean checkPassword(){
        String password = this.getInputEditText().getEditableText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(getContext(), R.string.py_password_empty);
            return false;
        }
        if (!GamaUtil.checkPassword(password)) {
            String passwordError1 = getContext().getResources().getString(R.string.py_password_error) + ":";
            String passwordError2 = getContext().getResources().getString(R.string.py_register_password_hit);
            String errorStrPassword = passwordError1 + passwordError2;
            ToastUtils.toast(getContext(), errorStrPassword, Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }*/


    /*public boolean checkVfCode(){

        String vf = this.getInputEditText().getEditableText().toString().trim();
        if (TextUtils.isEmpty(vf)) {
            ToastUtils.toast(getContext(), R.string.py_vfcode_empty);
            return false;
        }
        return  true;
    }*/
}
