package com.lhh.ptrrv.library;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;
import com.lhh.ptrrv.library.footer.loadmore.DefaultLoadMoreView;
import com.lhh.ptrrv.library.header.Header;
import com.lhh.ptrrv.library.impl.PrvInterface;
import com.lhh.ptrrv.library.util.PullToRefreshRecyclerViewUtil;
import com.gama.sdk.R;

/**
 * Created by linhonghong on 2015/11/11.
 */
public class PullToRefreshRecyclerView extends SwipeRefreshLayout implements PrvInterface {

    private static final String TAG = PullToRefreshRecyclerView.class.getCanonicalName();

    private RecyclerView mRecyclerView;

    //root header
    private Header mRootHeader;

    //main view,contain footer，header etc.
    private RelativeLayout mRootRelativeLayout;

    //header
    private View mHeader;

    private View mEmptyView;

    //default = 10
    private int mLoadMoreCount = 8;

    private int mCurScroll;

    private boolean mIsSwipeEnable = false;

    private BaseLoadMoreView mLoadMoreFooter;

    private LoadMoreListener mLoadMoreListener;

    private AdapterObserver mAdapterObserver;

    private boolean isLoading = false;
    private boolean hasMoreItems = false;

    private PullToRefreshRecyclerView.OnScrollListener mOnScrollLinstener;

    private InterOnScrollListener mInterOnScrollListener;

    private PullToRefreshRecyclerViewUtil mPtrrvUtil;

    public interface LoadMoreListener {
        void onLoadMoreItems();
    }

    public interface OnScrollListener{
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        //old-method, like listview 's onScroll ,but it's no use ,right ? by linhonghong 2015.10.29
        void onScroll(RecyclerView recyclerView, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    public PullToRefreshRecyclerView(Context context) {
        this(context,null);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup();
    }

    /**
     * main
     */
    private void setup(){
        setupExtra();
        initView();
        setLinster();
    }

    public void setRefreshing(boolean refreshing){
        super.setRefreshing(refreshing);
    }

    /**
     * initView
     */
    private void initView(){
        mRootRelativeLayout = (RelativeLayout)LayoutInflater.from(getContext()).inflate(R.layout.plat_ptrrv_root_view, null);

        this.addView(mRootRelativeLayout);
//
        this.setColorSchemeResources(R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright,
                R.color.swap_holo_green_bright, R.color.swap_holo_bule_bright);

//        this.setColorSchemeColors(-12011190,-11954459,-12011190,-11954459);
        mRecyclerView = (RecyclerView)mRootRelativeLayout.findViewById(R.id.ptrrv_recycler_view_id);

        mRecyclerView.setHasFixedSize(true);

        if(!mIsSwipeEnable) {
            this.setEnabled(false);
        }

    }


    /**
     * Init
     */
    private void setupExtra(){
        isLoading = false;
        hasMoreItems = false;
        mPtrrvUtil = new PullToRefreshRecyclerViewUtil();
    }

    private void setLinster(){
        mInterOnScrollListener = new InterOnScrollListener();
        mRecyclerView.addOnScrollListener(mInterOnScrollListener);
    }

    @Override
    public void setOnRefreshComplete() {
        this.setRefreshing(false);
    }

    @Override
    public void setOnLoadMoreComplete() {
        setHasMoreItems(false);
        removeLoadMoreView();
    }

    @Override
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    @Override
    public void setEmptyView(View emptyView) {
        if(mEmptyView != null) {
            mRootRelativeLayout.removeView(mEmptyView);
        }
        mEmptyView = emptyView;
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
        if(mAdapterObserver == null){
            mAdapterObserver = new AdapterObserver();
        }
        if(adapter != null){
            adapter.registerAdapterDataObserver(mAdapterObserver);
            mAdapterObserver.onChanged();
        }
    }

    @Override
    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void addHeaderView(View view) {
        //2015.11.17 finish method
        if(mHeader != null){
            mRootRelativeLayout.removeView(mHeader);
        }

        mHeader = view;

        if(mHeader == null){
            return;
        }

        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                if (getRecyclerView() == null || mHeader == null) {
                    return;
                }
                if (mRootHeader == null) {
                    mRootHeader = new Header();
                }
                mRootHeader.setHeight(mHeader.getMeasuredHeight());
                getRecyclerView().removeItemDecoration(mRootHeader);
                getRecyclerView().addItemDecoration(mRootHeader);
                getRecyclerView().getAdapter().notifyDataSetChanged();
            }
        });

        mRootRelativeLayout.addView(mHeader);
    }

    @Override
    public void removeHeader() {
        if (mRootHeader != null) {
            getRecyclerView().removeItemDecoration(mRootHeader);
            mRootHeader = null;
        }
        if(mHeader != null){
            mRootRelativeLayout.removeView(mHeader);
            mHeader = null;
        }
    }

    @Override
    public void setFooter(View view) {
        // now is empty, you can do in extra adapter
    }

    @Override
    public void setLoadMoreFooter(BaseLoadMoreView loadMoreFooter) {
        mLoadMoreFooter = loadMoreFooter;
    }

    @Override
    public BaseLoadMoreView getLoadMoreFooter() {
        return mLoadMoreFooter;
    }

    @Override
    public void addOnScrollListener(PullToRefreshRecyclerView.OnScrollListener onScrollLinstener) {
        mOnScrollLinstener = onScrollLinstener;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        if(mRecyclerView != null) {
            return mRecyclerView.getLayoutManager();
        }
        return null;
    }

    @Override
    public void onFinishLoading(boolean hasMoreItems, boolean needSetSelection) {

        if(getLayoutManager() == null){
            return;
        }

        if(!hasMoreItems && mLoadMoreFooter != null){
            //if it's last line, minus the extra height of loadmore
            mCurScroll = mCurScroll - mLoadMoreFooter.getLoadMorePadding();
        }

        // if items is too short, don't show loadingview
       /* if (getLayoutManager().getItemCount() < mLoadMoreCount) {
            hasMoreItems = false;
        }*/

        setHasMoreItems(hasMoreItems);
        removeLoadMoreView();
        isLoading = false;

        if (needSetSelection) {
            int first = findFirstVisibleItemPosition();
            mRecyclerView.scrollToPosition(--first);
        }
    }

    public int findFirstVisibleItemPosition(){
        return mPtrrvUtil.findFirstVisibleItemPosition(getLayoutManager());
    }

    public int findLastVisibleItemPosition(){
        return mPtrrvUtil.findLastVisibleItemPosition(getLayoutManager());
    }

    public int findFirstCompletelyVisibleItemPosition(){
        return mPtrrvUtil.findFirstCompletelyVisibleItemPosition(getLayoutManager());
    }

    @Override
    public void setSwipeEnable(boolean enable) {
        //just like extra setEnable(boolean).but it's more easy to use, like super.setEnable
        mIsSwipeEnable = enable;
        this.setEnabled(mIsSwipeEnable);
    }

    @Override
    public boolean isSwipeEnable() {
        return mIsSwipeEnable;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if(mRecyclerView != null){
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    /**
     * 设置首次多少个item才会出现加载更多（超过这个数量出现加载更多）
     * @param count
     */
    @Override
    public void setLoadMoreCount(int count) {
        mLoadMoreCount = count;
    }

    @Override
    public void release() {

    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration){
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    public void setLoadmoreString(String str){
        if(mLoadMoreFooter != null){
            mLoadMoreFooter.setLoadmoreString(str);
        }
    }

    private void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;

    }

    private void addLoadMoreView() {

        if (getLayoutManager().getChildCount() > 0) {
            if (getLayoutManager().getItemCount() > 0 && mLoadMoreFooter == null) {
                mLoadMoreFooter = new DefaultLoadMoreView(getContext(), getRecyclerView());
            }
            //add loadmore
            mRecyclerView.removeItemDecoration(mLoadMoreFooter);
            mRecyclerView.addItemDecoration(mLoadMoreFooter);
        }
    }

    private void removeLoadMoreView() {
        //remove loadmore
        if (mRecyclerView != null && mLoadMoreFooter != null) {
            mRecyclerView.removeItemDecoration(mLoadMoreFooter);
        }
    }


    private class InterOnScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
//            L.d("onScrollStateChanged newState:" + newState);
            if(getLayoutManager() == null){
                //here layoutManager is null
                return;
            }
            //RecycleView 显示的条目数
            int visibleCount = getLayoutManager().getChildCount();

            //显示数据总数
            int totalItemCount = getLayoutManager().getItemCount();


            //do super before callback
           /* if(mOnScrollLinstener != null){
                mOnScrollLinstener.onScrollStateChanged(recyclerView,newState);
            }*/
            if(totalItemCount < mLoadMoreCount){
                setHasMoreItems(false);
                isLoading = false;
            }else if (visibleCount > 0 && newState==RecyclerView.SCROLL_STATE_IDLE && !isLoading && hasMoreItems && ((lastVisibleItem + 1) == totalItemCount)){
                if (mLoadMoreListener != null) {
                    addLoadMoreView();
                    isLoading = true;
                    mLoadMoreListener.onLoadMoreItems();
                }
            }
        }

        int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//            L.d("onScrolled newState:");
            //do super before callback
            if(getLayoutManager() == null){
                //here layoutManager is null
                return;
            }

            mCurScroll = dy + mCurScroll;
            if(mHeader != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                    mHeader.setTranslationY(-mCurScroll);
                }else {
//                    ViewHelper.setTranslationY(mHeader, -mCurScroll);
                }
            }

//            int firstVisibleItem, visibleItemCount, totalItemCount;
//            visibleItemCount = getLayoutManager().getChildCount();
//            totalItemCount = getLayoutManager().getItemCount();
//            firstVisibleItem = findFirstVisibleItemPosition();
            //sometimes ,the last item is too big so as that the screen cannot show the item fully
            lastVisibleItem = findLastVisibleItemPosition();
//            lastVisibleItem = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if(mIsSwipeEnable) {
                if (findFirstCompletelyVisibleItemPosition() != 0) {
                    //here has a bug, if the item is too big , use findFirstCompletelyVisibleItemPosition will cannot swipe
                    PullToRefreshRecyclerView.this.setEnabled(false);
                } else {
                    PullToRefreshRecyclerView.this.setEnabled(true);
                }
            }

     /*       if(totalItemCount < mLoadMoreCount){
                setHasMoreItems(false);
                isLoading = false;
            }else if (!isLoading && hasMoreItems && ((lastVisibleItem + 1) == totalItemCount)) {
                Log.d(TAG,"!isLoading && hasMoreItems && ((lastVisibleItem + 1) == totalItemCount)");
                if (mLoadMoreListener != null) {
                    isLoading = true;
                    mLoadMoreListener.onLoadMoreItems();
                }

            }*/

//            if(mOnScrollLinstener != null){
//                mOnScrollLinstener.onScrolled(recyclerView, dx, dy);
//                mOnScrollLinstener.onScroll(recyclerView, firstVisibleItem, visibleItemCount, totalItemCount);
//            }
        }

    }


    private class AdapterObserver extends RecyclerView.AdapterDataObserver{
        @Override
        public void onChanged() {
            super.onChanged();
            //adapter has change
            if(mRecyclerView == null){
                //here must be wrong ,recyclerView is null????
                return;
            }

            RecyclerView.Adapter<?> adapter =  mRecyclerView.getAdapter();
            if(adapter != null && mEmptyView != null) {
                if(adapter.getItemCount() == 0) {
                    if(mIsSwipeEnable) {
                        PullToRefreshRecyclerView.this.setEnabled(false);
                    }
                    if(mEmptyView.getParent() != mRootRelativeLayout) {
                        mRootRelativeLayout.addView(mEmptyView);
                    }
                    mEmptyView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                else {
                    if(mIsSwipeEnable) {
                        PullToRefreshRecyclerView.this.setEnabled(true);
                    }
                    mEmptyView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
