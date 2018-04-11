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
import com.gama.sdk.plat.PlatMainActivity;
import com.gama.sdk.plat.adapter.PlatInfoRecyclerViewAdapter;
import com.gama.sdk.plat.data.bean.reqeust.PagingLoadBean;
import com.gama.sdk.plat.data.bean.response.PlatArrayObjBaseModel;
import com.gama.sdk.plat.data.bean.response.PlatInfoModel;
import com.gama.sdk.utils.DialogUtil;
import com.gama.sdk.widget.EEESwipeRefreshLayout;
import com.gama.sdk.widget.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class InformantionFragment extends SSdkBaseFragment {

    protected EEESwipeRefreshLayout eeeSwipeRefreshLayout;

    private PlatInfoRecyclerViewAdapter platInfoRecyclerViewAdapter;

    private List<PlatInfoModel> dataModelList;

    private Dialog mDialog;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;


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
        pagingLoadBean = new PagingLoadBean(getActivity());

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

            eeeSwipeRefreshLayout.addItemDecoration(new SpaceItemDecoration(30,8));


            platInfoRecyclerViewAdapter = new PlatInfoRecyclerViewAdapter(getActivity());


            eeeSwipeRefreshLayout.setAdapter(platInfoRecyclerViewAdapter);
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
            PL.d("setAdapter");

            eeeSwipeRefreshLayout.setRefreshing(true);
            //eeeSwipeRefreshLayout.setLoadMoreCount();
            refreshData(false);

            platInfoRecyclerViewAdapter.setRecylerViewItemClickListener(new RecylerViewItemClickListener() {
                @Override
                public void onItemClick(RecyclerView.Adapter adapter, int position, View itemView) {
                    PL.d("onItemClick:" + position);//position从0开始
                    if (dataModelList.size() > 0){
                        PlatInfoModel platInfoModel = dataModelList.get(position);

                        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(getActivity());
                        sGameBaseRequestBean.setCompleteUrl(platInfoModel.getUrl());

                        PlatCommonWebViewFragment sWebViewFragment = new PlatCommonWebViewFragment();
                        sWebViewFragment.setWebUrl(sGameBaseRequestBean.createPreRequestUrl());

                        sWebViewFragment.setWebTitle(getString(R.string.plat_info_webview_title));
                        sWebViewFragment.setShowBackView(true);
                        ((PlatMainActivity)getActivity()).changeFragment(sWebViewFragment);
                    }
                }
            });

        }
    }


    PagingLoadBean pagingLoadBean;

    private void refreshData(final boolean isLoadMore) {

        if (!isLoadMore){
            pagingLoadBean.resetPage();//刷新重置页数
        }

        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {

                pagingLoadBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(getActivity()) + "app/float/api/information");
                return pagingLoadBean;
            }
        };
        absHttpRequest.setReqCallBack(new ISReqCallBack<PlatArrayObjBaseModel<PlatInfoModel>>() {
            @Override
            public void success(PlatArrayObjBaseModel<PlatInfoModel> baseModel, String rawResult) {
                PL.i(baseModel.getMessage());
                if (isLoadMore){

                    List<PlatInfoModel> tempData = baseModel.getData();
                    if (tempData != null && tempData.size() > 0){
                        for (PlatInfoModel platInfoModel: tempData) {
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

                    List<PlatInfoModel> tempData = baseModel.getData();
                    if (tempData != null && tempData.size() > 0){
                        pagingLoadBean.increasePage();//页数增加
                        eeeSwipeRefreshLayout.setLoadMoreCount(tempData.size());//设置多少个出现loadmore
                        dataModelList = tempData;
                    }

                    refreshFinish();

                }


                platInfoRecyclerViewAdapter.setDataModelList(dataModelList);

                platInfoRecyclerViewAdapter.notifyDataSetChanged();

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
//        absHttpRequest.setGetMethod(true,false);
        absHttpRequest.excute(new TypeToken<PlatArrayObjBaseModel<PlatInfoModel>>(){}.getType());
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

        if (platInfoRecyclerViewAdapter != null && eeeSwipeRefreshLayout !=null){

            this.platInfoRecyclerViewAdapter.notifyDataSetChanged();
            eeeSwipeRefreshLayout.setOnRefreshComplete();
            eeeSwipeRefreshLayout.onFinishLoading(true, false);
        }
    }
    public void loadMoreFinish(){
        if (platInfoRecyclerViewAdapter != null && eeeSwipeRefreshLayout !=null){
            this.platInfoRecyclerViewAdapter.notifyDataSetChanged();
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
