package com.flyfun.sdk.login;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyfun.base.utils.GamaUtil;
import com.flyfun.sdk.login.model.AccountModel;
import com.gama.sdk.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AccountPopupWindow extends PopupWindow {

    private Context context;
    private View contentView;
    private RecyclerView historyAccountRv;

    private List<AccountModel> accountModels = new ArrayList<>();;
    private CommonAdapter  historyAccountCommonAdapter;

    private PopWindowListener popWindowListener;

    public void setPopWindowListener(PopWindowListener popWindowListener) {
        this.popWindowListener = popWindowListener;
    }

    public AccountPopupWindow(Context context) {
        super(context);
        this.context = context;

        initView();
        initHistoryRv();
    }

    private Context getContext() {
        return this.context;
    }

    public AccountPopupWindow() {
        super();
    }

    private void initView(){

//        PopupWindow设置的宽和高属性，是给contentView中最外层的View设置的，等于用WRAP_CONTENT覆盖了200dp，
//        如果这个view没有子View，或者子view没有合适的宽高属性，就会导致宽高为0
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.sdk_account_rv, null);
        setContentView(contentView);

        this.setOutsideTouchable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }else{
            this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        this.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                for (LimitedItemBean limitedItemBean : limitedItemBeanArrayList) {
//                    limitedItemBean.setIsSelect(0);
//                }
            }
        });

        historyAccountRv = contentView.findViewById(R.id.sdk_history_account_rv);
    }

    public void showOnView(View anchorView){
        //                accountPopupWindow.showAtLocation(accountSdkInputEditTextView, Gravity.BOTTOM,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.showAsDropDown(anchorView,0,4, Gravity.BOTTOM | Gravity.LEFT);
        }else{
            this.showAsDropDown(anchorView,0,4);
        }
    }

    private void initHistoryRv() {

        List<AccountModel> ams = GamaUtil.getAccountModels(getContext());
        accountModels.addAll(ams);

        if (accountModels == null){
            return;
        }

        historyAccountRv.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAccountCommonAdapter = new CommonAdapter<AccountModel>(this.getContext(), R.layout.sdk_login_history_account_item, accountModels)
        {

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }

            @Override
            protected void convert(final ViewHolder holder, final AccountModel accountModel, final int position) {
                holder.setText(R.id.history_account_item_text, accountModel.getAccount());
                holder.setOnClickListener(R.id.history_account_item_text, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        loginAccountEditText.setText(accountModel.getAccount());
//                        loginPasswordEditText.setText(accountModel.getPassword());
//                        historyAccountRv.setVisibility(View.GONE);
                        if (popWindowListener != null){
                            popWindowListener.onUse(accountModel);
                        }
                        AccountPopupWindow.this.dismiss();
                    }
                });
                holder.setOnClickListener(R.id.history_account_item_delete_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        removeAccountMode(position);
                    }
                });
                holder.setOnClickListener(R.id.history_account_item_delete_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        removeAccountMode(position);
                    }
                });

            }

        };
        historyAccountRv.setAdapter(historyAccountCommonAdapter);
    }

    private void removeAccountMode(int position) {
        AccountModel removeModel = accountModels.remove(position);
        if (removeModel != null) {

            if (popWindowListener != null){
                popWindowListener.onRemove(removeModel);
            }

            if (accountModels.isEmpty()) {
//                historyAccountRv.setVisibility(GONE);
//                historyAccountListBtn.setVisibility(GONE);//删除就保存重新刷新数据
//                loginAccountEditText.setText("");
//                loginPasswordEditText.setText("");
                if (popWindowListener != null){
                    popWindowListener.onEmpty();
                }
            }
            GamaUtil.saveAccountModels(getContext(), accountModels);
            List<AccountModel> ams = GamaUtil.getAccountModels(getContext());
            accountModels.clear();
            accountModels.addAll(ams);
            historyAccountCommonAdapter.notifyDataSetChanged();
        }
    }


   public interface PopWindowListener{

        void onRemove(AccountModel accountModel);
        void onUse(AccountModel accountModel);
        void onEmpty();
    }

}
