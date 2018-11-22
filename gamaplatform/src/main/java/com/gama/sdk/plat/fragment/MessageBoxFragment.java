package com.gama.sdk.plat.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.gama.base.cfg.ResConfig;
import com.gama.sdk.R;
import com.gama.sdk.SSdkBaseFragment;
import com.gama.sdk.callback.RecylerViewItemClickListener;
import com.gama.sdk.plat.PlatMainActivity;
import com.gama.sdk.plat.adapter.PlatMessageBoxAdapter;
import com.gama.sdk.plat.data.bean.reqeust.MessageReadBean;
import com.gama.sdk.plat.data.bean.reqeust.PagingLoadBean;
import com.gama.sdk.plat.data.bean.response.PlatArrayObjBaseModel;
import com.gama.sdk.plat.data.bean.response.PlatMessageBoxModel;
import com.gama.sdk.utils.DialogUtil;
import com.gama.sdk.widget.EEESwipeRefreshLayout;
import com.gama.sdk.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class MessageBoxFragment extends SSdkBaseFragment {

    protected EEESwipeRefreshLayout eeeSwipeRefreshLayout;

    private PlatMessageBoxAdapter messageBoxAdapter;

    private List<PlatMessageBoxModel> dataModelList;

    private Dialog mDialog;
    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PL.i("delete mail...");
            reqeustDeteleMail();
        }
    };


    PlatMessageBoxModel clickPlatMessageBoxModel;
    PlatCommonWebViewFragment sWebViewFragment;


    //刪除信件
    private void reqeustDeteleMail() {
        if (clickPlatMessageBoxModel == null){
            return;
        }
        final MessageReadBean messageReadBean = new MessageReadBean(getActivity());
        messageReadBean.setMessageId(clickPlatMessageBoxModel.getMessageId());
        messageReadBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/float/api/message/delete");

        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {
                return messageReadBean;
            }
        };

        absHttpRequest.setLoadDialog(mDialog);
        absHttpRequest.setReqCallBack(new ISReqCallBack<BaseResponseModel>() {
            @Override
            public void success(BaseResponseModel responseModel, String rawResult) {
                PL.i(responseModel.getMessage());
                if (responseModel.isRequestSuccess()){
                    if (dataModelList != null && dataModelList.contains(clickPlatMessageBoxModel)){
                        dataModelList.remove(clickPlatMessageBoxModel);
                    }
                    if (sWebViewFragment != null){
                        sWebViewFragment.getFragmentManager().popBackStack();
                    }
                    messageBoxAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        absHttpRequest.excute(BaseResponseModel.class);

    }


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

        initData();
        return contentView;

    }


    private void initData() {

        dataModelList = new ArrayList<>();

        if (eeeSwipeRefreshLayout !=null){

            eeeSwipeRefreshLayout.setLayoutManager(new LinearLayoutManager(getActivity()));
            eeeSwipeRefreshLayout.setSwipeEnable(true);//open swipe

            eeeSwipeRefreshLayout.setLoadMoreListener(new PullToRefreshRecyclerView.LoadMoreListener() {
                @Override
                public void onLoadMoreItems() {
                    PL.i("setLoadMoreListener");
//                    onLoadMoreData(InformantionFragment.this,eeeSwipeRefreshLayout,recylerViewAdapter);
                    refreshData(true);
                }
            });
            eeeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    PL.i("setOnRefreshListener");
//                    onRecylerRefresh(InformantionFragment.this,eeeSwipeRefreshLayout,recylerViewAdapter);

                    refreshData(false);
                }
            });

            eeeSwipeRefreshLayout.addItemDecoration(new SpaceItemDecoration(15,15));


            messageBoxAdapter = new PlatMessageBoxAdapter(getActivity());


            eeeSwipeRefreshLayout.setAdapter(messageBoxAdapter);
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
            PL.d("setAdapter");

            eeeSwipeRefreshLayout.setRefreshing(true);
            //eeeSwipeRefreshLayout.setLoadMoreCount();
            pagingLoadBean = new PagingLoadBean(getActivity());
            refreshData(false);

            messageBoxAdapter.setRecylerViewItemClickListener(new RecylerViewItemClickListener() {
                @Override
                public void onItemClick(RecyclerView.Adapter adapter, int position, View itemView) {
                    PL.d("onItemClick:" + position);//position从0开始
                    if (dataModelList.size() > 0){
                        clickPlatMessageBoxModel = dataModelList.get(position);

                        MessageReadBean messageReadBean = new MessageReadBean(getActivity());
                        messageReadBean.setCompleteUrl(clickPlatMessageBoxModel.getUrl());
                        if (clickPlatMessageBoxModel.isReadStatus()) {
                            messageReadBean.setReadStatus("true");
                        }else{
                            messageReadBean.setReadStatus("false");
                        }

                        messageReadBean.setMessageId(clickPlatMessageBoxModel.getMessageId());
                        sWebViewFragment = new PlatCommonWebViewFragment();
                        sWebViewFragment.setDeleteViewOnClickListener(l);
                        sWebViewFragment.setWebUrl(messageReadBean.createPreRequestUrl());
                        sWebViewFragment.setWebTitle(clickPlatMessageBoxModel.getTitle());
                        sWebViewFragment.setShowBackView(true);
                        ((PlatMainActivity)getActivity()).changeFragment(sWebViewFragment);

                        clickPlatMessageBoxModel.setReadStatus(true);

                        messageBoxAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }

    PagingLoadBean pagingLoadBean;

    private void refreshData(final boolean isLoadMore) {

        if (!isLoadMore && pagingLoadBean != null){
            pagingLoadBean.resetPage();
        }
        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {

                pagingLoadBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/float/api/message");
                return pagingLoadBean;
            }
        };

        absHttpRequest.setReqCallBack(new ISReqCallBack<PlatArrayObjBaseModel<PlatMessageBoxModel>>() {
            @Override
            public void success(PlatArrayObjBaseModel<PlatMessageBoxModel> baseModel, String rawResult) {
                PL.i(baseModel.getMessage());

                if (isLoadMore){

                    List<PlatMessageBoxModel> tempData = baseModel.getData();
                    if (tempData != null && tempData.size() > 0){
                        for (PlatMessageBoxModel platInfoModel: tempData) {
                            dataModelList.add(platInfoModel);
                        }
                        loadMoreFinish();//更多加载完毕
                        pagingLoadBean.increasePage();//页数增加

                    }else {
                        noMoreLoad();//没有更多以数据
                    }

                }else {

                    if (dataModelList != null){//先清除数据
                        dataModelList.clear();
                    }
                    List<PlatMessageBoxModel> tempData = baseModel.getData();
                    if (tempData != null && tempData.size() > 0){
                        pagingLoadBean.increasePage();//页数增加
                        eeeSwipeRefreshLayout.setLoadMoreCount(tempData.size());//设置多少个出现loadmore
                        dataModelList = tempData;
                    }

                    refreshFinish();

                }
//                Collections.sort(dataModelList,comparator);
                messageBoxAdapter.setDataModelList(dataModelList);

                messageBoxAdapter.notifyDataSetChanged();

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
        absHttpRequest.excute(new TypeToken<PlatArrayObjBaseModel<PlatMessageBoxModel>>(){}.getType());
    }

    Comparator<PlatMessageBoxModel> comparator = new Comparator<PlatMessageBoxModel>(){
        public int compare(PlatMessageBoxModel s1, PlatMessageBoxModel s2) {
            //先排年龄
            int m = 0;
            int n = 0;
            if (s1.isReadStatus()){
                m = 1;
            }else{
                m = 2;
            }
            if (s2.isReadStatus()){
                n = 1;
            }else {
                n = 2;
            }
            return n - m;

        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PL.d("onViewCreated" + this.getTag());

    }



    public void refreshFinish(){

        if (messageBoxAdapter != null && eeeSwipeRefreshLayout !=null){

            this.messageBoxAdapter.notifyDataSetChanged();
            eeeSwipeRefreshLayout.setOnRefreshComplete();
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
        }
    }
    public void loadMoreFinish(){
        if (messageBoxAdapter != null && eeeSwipeRefreshLayout !=null){
            this.messageBoxAdapter.notifyDataSetChanged();
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
