package com.gama.sdk.plat.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.gama.base.bean.SGameBaseRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.sdk.R;
import com.gama.sdk.SSdkBaseFragment;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.GiftCenterLayoutManager;
import com.gama.sdk.plat.PlatMainActivity;
import com.gama.sdk.plat.adapter.PlatGiftCenterAdapter;
import com.gama.sdk.plat.data.bean.reqeust.ReceiveGiftBean;
import com.gama.sdk.plat.data.bean.response.GiftGetSuccessModel;
import com.gama.sdk.plat.data.bean.response.PlatArrayObjBaseModel;
import com.gama.sdk.plat.data.bean.response.PlatGiftCenterModel;
import com.gama.sdk.utils.DialogUtil;
import com.gama.sdk.widget.EEESwipeRefreshLayout;
import com.gama.sdk.widget.SpaceItemDecoration;

import java.util.List;


public class GiftCenterFragment extends SSdkBaseFragment {

    protected EEESwipeRefreshLayout eeeSwipeRefreshLayout;

    private PlatGiftCenterAdapter platGiftCenterAdapter;

    private List<PlatGiftCenterModel> dataModelList;

    private Dialog mDialog;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    View mySerialLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PL.d("onCreateView");
        View contentView = inflater.inflate(R.layout.plat_menu_content_layout, container, false);
        eeeSwipeRefreshLayout = (EEESwipeRefreshLayout) contentView.findViewById(R.id.plat_info_recy_view);
        TextView titleTextView = (TextView) contentView.findViewById(R.id.plat_title_tv);
        titleTextView.setText(title);
        contentView.findViewById(R.id.plat_title_close_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });

        mDialog = DialogUtil.createLoadingDialog(getActivity(),"Loading...");

        mySerialLayout = contentView.findViewById(R.id.plat_my_serial_number_layout);
        mySerialLayout.setVisibility(View.VISIBLE);
        mySerialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySerialCenterFragment mySerialCenterFragment = new MySerialCenterFragment();
                ((PlatMainActivity)getActivity()).changeFragment(mySerialCenterFragment);
            }
        });
        initData();
        return contentView;

    }

    private void showGiftGetSuccessDialog(GiftGetSuccessModel getSuccessModel, final PlatGiftCenterModel platGiftCenterModel) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View successView = inflater.inflate(R.layout.plat_gift_center_get_gift_success, null);
        View giftSuccessBtn = successView.findViewById(R.id.gift_get_success_confirm);
        TextView giftSuccessNum = (TextView) successView.findViewById(R.id.gift_get_success_mun);
        giftSuccessNum.setText(getSuccessModel.getData());
        final Dialog successDialog = DialogUtil.createDialog(getActivity(),successView);
        successDialog.show();
        giftSuccessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
                if (platGiftCenterModel != null) {
//                    platGiftCenterModel.setReceive(true);
//                    platGiftCenterAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initData() {

        if (eeeSwipeRefreshLayout !=null){

            eeeSwipeRefreshLayout.setLayoutManager(new GiftCenterLayoutManager(getActivity(),4));
            eeeSwipeRefreshLayout.setSwipeEnable(true);//open swipe
            eeeSwipeRefreshLayout.setLoadMoreListener(new PullToRefreshRecyclerView.LoadMoreListener() {
                @Override
                public void onLoadMoreItems() {
                    PL.i("setLoadMoreListener");
//                    onLoadMoreData(InformantionFragment.this,eeeSwipeRefreshLayout,recylerViewAdapter);
                    loadMoreFinish();
                }
            });
            eeeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    PL.i("setOnRefreshListener");
//                    onRecylerRefresh(InformantionFragment.this,eeeSwipeRefreshLayout,recylerViewAdapter);
                    refreshData();
                }
            });

            eeeSwipeRefreshLayout.addItemDecoration(new SpaceItemDecoration(15,15));

            platGiftCenterAdapter = new PlatGiftCenterAdapter(getActivity());


            eeeSwipeRefreshLayout.setAdapter(platGiftCenterAdapter);
            eeeSwipeRefreshLayout.onFinishLoading(false, false);
            PL.d("setAdapter");

            eeeSwipeRefreshLayout.setRefreshing(true);
            //eeeSwipeRefreshLayout.setLoadMoreCount();
            refreshData();

            platGiftCenterAdapter.setRecylerViewItemClickListener(new RecylerViewItemClickListener() {
                @Override
                public void onItemClick(RecyclerView.Adapter adapter, int position, View itemView) {
                    PL.d("onItemClick:" + position);//position从0开始
                    if (dataModelList.size() > 0){
                        PlatGiftCenterModel platGiftCenterModel = dataModelList.get(position);
                        if (platGiftCenterModel != null) {
                            receiveGift(platGiftCenterModel);
                        }
                    }
                }
            });

        }
    }

    private void refreshData() {
        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {
                SGameBaseRequestBean gameBaseRequestBean = new SGameBaseRequestBean(getActivity());
                gameBaseRequestBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/giftbag/api/giftbagCentreGameList");
                return gameBaseRequestBean;
            }
        };
        absHttpRequest.setLoadDialog(mDialog);
        absHttpRequest.setReqCallBack(new ISReqCallBack<PlatArrayObjBaseModel<PlatGiftCenterModel>>() {
            @Override
            public void success(PlatArrayObjBaseModel<PlatGiftCenterModel> baseModel, String rawResult) {
                PL.i(baseModel.getMessage());
                dataModelList = baseModel.getData();
                platGiftCenterAdapter.setDataModelList(dataModelList);

                platGiftCenterAdapter.notifyDataSetChanged();

                refreshFinish();

            }

            @Override
            public void timeout(String code) {
                refreshFinish();
            }

            @Override
            public void noData() {
                refreshFinish();
            }
        });
        absHttpRequest.excute(new TypeToken<PlatArrayObjBaseModel<PlatGiftCenterModel>>(){}.getType());
    }

    private void receiveGift(final PlatGiftCenterModel platGiftCenterModel){

        final ReceiveGiftBean receiveGiftBean = new ReceiveGiftBean(getActivity());
        receiveGiftBean.setActivityCode(platGiftCenterModel.getActivityCode());
        receiveGiftBean.setGiftbagGameCode(platGiftCenterModel.getGiftbagGameCode());
        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {
                receiveGiftBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/giftbag/api/receiveFromGiftbagCentre");
                return receiveGiftBean;
            }
        };

        absHttpRequest.setReqCallBack(new ISReqCallBack<GiftGetSuccessModel>() {
            @Override
            public void success(GiftGetSuccessModel getSuccessModel, String rawResult) {
                PL.i(getSuccessModel.getMessage());
                if (getSuccessModel != null){
                    ToastUtils.toast(getActivity(),getSuccessModel.getMessage());
                    if (getSuccessModel.isRequestSuccess()) {
//                        refreshData();
                        platGiftCenterModel.setIsReceive("1");
                        platGiftCenterAdapter.notifyDataSetChanged();
                        showGiftGetSuccessDialog(getSuccessModel,platGiftCenterModel);

                    }
                }

            }

            @Override
            public void timeout(String code) {
                refreshFinish();
            }

            @Override
            public void noData() {
                refreshFinish();
            }
        });
        absHttpRequest.excute(GiftGetSuccessModel.class);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.d("onViewCreated" + this.getTag());

    }


//    protected abstract void onFragmentCreateView(InformantionFragment baseSwipeRefreshFragment, EEESwipeRefreshLayout eeeSwipeRefreshLayout, RecyclerView.Adapter recylerViewAdapter);
//    protected abstract void onRecylerRefresh(InformantionFragment baseSwipeRefreshFragment, EEESwipeRefreshLayout eeeSwipeRefreshLayout, RecyclerView.Adapter recylerViewAdapter);
//
//    protected abstract void onLoadMoreData(InformantionFragment baseSwipeRefreshFragment, EEESwipeRefreshLayout eeeSwipeRefreshLayout, RecyclerView.Adapter recylerViewAdapter);


    public void refreshFinish(){

        if (platGiftCenterAdapter != null && eeeSwipeRefreshLayout !=null){

            this.platGiftCenterAdapter.notifyDataSetChanged();
            eeeSwipeRefreshLayout.setOnRefreshComplete();
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
        }
    }
    public void loadMoreFinish(){
        if (platGiftCenterAdapter != null && eeeSwipeRefreshLayout !=null){
            this.platGiftCenterAdapter.notifyDataSetChanged();
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
        }
    }

    public void noMoreLoad(){
        if (eeeSwipeRefreshLayout != null) {
            ToastUtils.toast(getContext(),"no data any more");
            eeeSwipeRefreshLayout.onFinishLoading(false, false);
        }
    }

}
