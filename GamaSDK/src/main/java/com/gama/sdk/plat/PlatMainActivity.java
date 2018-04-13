package com.gama.sdk.plat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.core.base.SBaseFragment;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.callback.ISReqCallBack;
import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;
import com.gama.base.bean.SGameBaseRequestBean;
import com.gama.base.bean.SSdkBaseRequestBean;
import com.gama.base.cfg.ResConfig;
import com.gama.base.utils.Localization;
import com.gama.base.utils.GamaUtil;
import com.gama.data.login.execute.BaseLoginRequestTask;
import com.gama.data.login.request.AccountInjectionRequestBean;
import com.gama.pay.gp.bean.req.WebPayReqBean;
import com.gama.pay.gp.util.PayHelper;
import com.gama.sdk.R;
import com.gama.sdk.SBaseSdkActivity;
import com.gama.sdk.plat.adapter.PlatMenuGridViewAdapter;
import com.gama.sdk.plat.data.PlatContract;
import com.gama.sdk.plat.data.bean.response.PhoneAreaCodeModel;
import com.gama.sdk.plat.data.bean.response.PlatArrayObjBaseModel;
import com.gama.sdk.plat.data.bean.response.PlatMenuAllModel;
import com.gama.sdk.plat.data.bean.response.PlatMenuModel;
import com.gama.sdk.plat.data.bean.response.PlatObjBaseModel;
import com.gama.sdk.plat.data.bean.response.UserBindInfoModel;
import com.gama.sdk.plat.data.bean.response.UserHasGetGiftModel;
import com.gama.sdk.plat.data.presenter.PlatPresenterImpl;
import com.gama.sdk.plat.fragment.BindPhoneGiftFragment;
import com.gama.sdk.plat.fragment.GiftCenterFragment;
import com.gama.sdk.plat.fragment.InformantionFragment;
import com.gama.sdk.plat.fragment.MessageBoxFragment;
import com.gama.sdk.plat.fragment.NotStarpyAccountManagerFragment;
import com.gama.sdk.plat.fragment.PlatCommonWebViewFragment;
import com.gama.sdk.plat.fragment.StarpyAccountManagerFragment;

import java.util.ArrayList;
import java.util.List;

public class PlatMainActivity extends SBaseSdkActivity implements PlatContract.IPlatView {

    private static final String TAG = PlatMainActivity.class.getCanonicalName();
    private DrawerLayout mDrawerLayout;

    private GridView gridView;

    private View leftSwitchvViewiew;
    private View rightSwitchvViewiew;

    private FrameLayout menuDrawerFrameLayout;
    private FrameLayout contentFrameLayout;

    private FragmentManager fragmentManager;

    private PlatPresenterImpl platPresenter;


    private ArrayList<PlatMenuModel> platMenuModels;

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            Log.i(TAG,"onDrawerSlide");
            leftSwitchvViewiew.setVisibility(View.GONE);
            isDrawerOpen = false;
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            Log.i(TAG,"onDrawerOpened");
            leftSwitchvViewiew.setVisibility(View.GONE);
            rightSwitchvViewiew.setBackgroundResource(R.drawable.plat_menu_open);
            isDrawerOpen = true;
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            Log.i(TAG,"onDrawerClosed");
            leftSwitchvViewiew.setVisibility(View.VISIBLE);
            rightSwitchvViewiew.setBackgroundResource(R.drawable.plat_menu_closeleft);
            isDrawerOpen = false;
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            Log.i(TAG,"onDrawerStateChanged");
            if (!isDrawerOpen){
                leftSwitchvViewiew.setVisibility(View.VISIBLE);
                rightSwitchvViewiew.setBackgroundResource(R.drawable.plat_menu_closeleft);
            }
        }
    };
    private PlatMenuGridViewAdapter platMenuGridViewAdapter;

    private InformantionFragment informantionFragment;
    private GiftCenterFragment giftCenterFragment;
    private MessageBoxFragment messageBoxFragment;

    private UserBindInfoModel userBindInfoModel;
    private  UserHasGetGiftModel userHasGetGiftModel;
    private List<PhoneAreaCodeModel> phoneAreaCodeModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Localization.updateSGameLanguage(this);

        setContentView(R.layout.plat_activity_content_page);

        fragmentManager = getSupportFragmentManager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        menuDrawerFrameLayout = (FrameLayout) findViewById(R.id.plat_menu_drawer);//侧滑菜单

        leftSwitchvViewiew = findViewById(R.id.plat_menu_switch_view);
        rightSwitchvViewiew = findViewById(R.id.plat_menu__right_switch_view);

        contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);//fragment页面

        initData();

        mDrawerLayout.addDrawerListener(drawerListener);

        gridView = (GridView) findViewById(R.id.plat_menu_drawer_gridview);

        platMenuGridViewAdapter = new PlatMenuGridViewAdapter(this);

        gridView.setAdapter(platMenuGridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG,"onItemClick i--" + i);
                if (currentClickView != null) {
                    currentClickView.setBackgroundColor(Color.TRANSPARENT);
                }
                view.setBackgroundResource(R.drawable.plat_menu_item_bg);
                currentClickView = view;

                if (platMenuModels != null){
                    PlatMenuModel platMenuModel = platMenuModels.get(i);
                    if (platMenuModel.getItemId().equals("information")){

                        showInfoFragment(platMenuModel);

                    }else if (platMenuModel.getItemId().equals("bindGiftBag")){

                        changeToBindPhoneGiftFragment(platMenuModel);

                    }else if (platMenuModel.getItemId().equals("account")){

                        if (userBindInfoModel != null) {
                            if (userBindInfoModel.isStarpyUser()) {
                                StarpyAccountManagerFragment accountManagerFragment = new StarpyAccountManagerFragment();
                                accountManagerFragment.setTitle(platMenuModel.getName());
                                accountManagerFragment.setUserBindInfoModel(userBindInfoModel);
                                changeFragmentNotBackStack(accountManagerFragment);
                            }else {

                                NotStarpyAccountManagerFragment accountManagerFragment = new NotStarpyAccountManagerFragment();
                                accountManagerFragment.setTitle(platMenuModel.getName());
                                accountManagerFragment.setUserBindInfoModel(userBindInfoModel);
                                changeFragmentNotBackStack(accountManagerFragment);
                            }
                        }else {
                            ToastUtils.toast(getApplicationContext(),R.string.plat_get_data_error);
                        }

                    }else if (platMenuModel.getItemId().equals("letterBox")){

                        if (messageBoxFragment == null) {
                            messageBoxFragment = new MessageBoxFragment();
                        }
                        messageBoxFragment.setTitle(platMenuModel.getName());
                        changeFragment(messageBoxFragment);

                    }else if (platMenuModel.getItemId().equals("giftBagCentre")){
                        if (giftCenterFragment == null) {
                            giftCenterFragment = new GiftCenterFragment();
                            giftCenterFragment.setTitle(platMenuModel.getName());
                        }
                        changeFragment(giftCenterFragment);

                    }else if (platMenuModel.getItemId().equals("storedValue")){

                        WebPayReqBean webPayReqBean = PayHelper.buildWebPayBean(PlatMainActivity.this,"",
                                GamaUtil.getRoleLevel(PlatMainActivity.this),"");

                        String payThirdUrl = null;
                        if (GamaUtil.getSdkCfg(PlatMainActivity.this) != null) {

                            payThirdUrl = GamaUtil.getSdkCfg(PlatMainActivity.this).getS_Third_PayUrl();
                        }
                        if (TextUtils.isEmpty(payThirdUrl)){
                            payThirdUrl = ResConfig.getPayPreferredUrl(PlatMainActivity.this) + ResConfig.getPayThirdMethod(PlatMainActivity.this);
                        }
                        webPayReqBean.setCompleteUrl(payThirdUrl);

                        openPlatWebViewFragmentForPay(webPayReqBean.createPreRequestUrl(),platMenuModel.getName(),platMenuModel.getName());


                    }else {
                        SGameBaseRequestBean sGameBaseRequestBean = new SGameBaseRequestBean(PlatMainActivity.this);
                        sGameBaseRequestBean.setCompleteUrl(platMenuModel.getUrl());
                        openPlatWebViewFragment(sGameBaseRequestBean.createPreRequestUrl(),platMenuModel.getName(),platMenuModel.getName());
                    }
                }


            }
        });

        leftSwitchvViewiew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG," mDrawerLayout onClick");
                mDrawerLayout.openDrawer(menuDrawerFrameLayout);
            }
        });

        rightSwitchvViewiew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(menuDrawerFrameLayout);
            }
        });


    }

    public void changeToBindPhoneGiftFragment(PlatMenuModel platMenuModel) {
        if (platMenuModel == null){
            for (PlatMenuModel model:platMenuModels) {
                if (model.getItemId().equals("bindGiftBag")){
                    platMenuModel = model;
                    break;
                }
            }
        }
        BindPhoneGiftFragment bindPhoneGiftFragment = new BindPhoneGiftFragment();
        bindPhoneGiftFragment.setTitle(platMenuModel.getName());
        bindPhoneGiftFragment.setUserBindInfoModel(userBindInfoModel);
        bindPhoneGiftFragment.setUserHasGetGiftModel(userHasGetGiftModel);
        bindPhoneGiftFragment.setPhoneAreaCodeModels(phoneAreaCodeModels);
        changeFragmentNotBackStack(bindPhoneGiftFragment);
    }

    private void showInfoFragment(PlatMenuModel platMenuModel) {
        if (informantionFragment == null) {
            informantionFragment = new InformantionFragment();
            informantionFragment.setTitle(platMenuModel.getName());
        }
        changeFragmentNotBackStack(informantionFragment);
    }


    private void initData() {

        platPresenter = new PlatPresenterImpl();
        platPresenter.setBaseView(this);
        platPresenter.reqeustPlatMenuData(this);

        requestUserBindInfo();
        //http://testwww.starb168.com/app/user/api/phoneBindIsReceiveGiftbag
        requestPhoneBindIsReceiveGiftbag();

        requestPhoneAreaCode();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PL.i("activity onActivityResult");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDrawerLayout != null && drawerListener != null){
            mDrawerLayout.removeDrawerListener(drawerListener);
        }

    }

    /**
     * 当前被点击的item
     */
    private View currentClickView;

    /**
     * 侧滑菜单是否处于打开状态
     */
    private boolean isDrawerOpen;

    @Override
    public void reqeustPlatMenuDataSuccess(PlatContract.RequestType requestType, PlatMenuAllModel platMenuAllModel) {

        if (platMenuAllModel != null) {
            platMenuModels = platMenuAllModel.getData();
            platMenuGridViewAdapter.setPlatMenuBeans(platMenuModels);
            platMenuGridViewAdapter.notifyDataSetChanged();

            if (platMenuModels != null && platMenuModels.size() > 0) {
                showInfoFragment(platMenuModels.get(0));
                mDrawerLayout.openDrawer(menuDrawerFrameLayout);
            }
        }
    }

    @Override
    public void reqeustDataFail(PlatContract.RequestType requestType) {

    }

    /**
     * 获取当前前台的Fragment
     * @return
     */
    public Fragment getVisibleFragment(){
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null){
            return null;
        }
        for(Fragment fragment : fragments){
            if(fragment!= null && fragment.isVisible())
                return fragment;
        }
        return null;
    }


    public void openPlatWebViewFragment(String url,String title,String tag){
        PlatCommonWebViewFragment sWebViewFragment = new PlatCommonWebViewFragment();
        sWebViewFragment.setWebUrl(url);
        sWebViewFragment.setWebTitle(title);
        sWebViewFragment.setFragmentTag("platMenuFragment_"+ tag);
        changeFragment(sWebViewFragment);
    }

    public void openPlatWebViewFragmentForPay(String url,String title,String tag){
        PlatCommonWebViewFragment sWebViewFragment = new PlatCommonWebViewFragment();
        sWebViewFragment.setWebUrl(url);
        sWebViewFragment.setWebTitle(title);
        sWebViewFragment.setShowTitleView(false);
        sWebViewFragment.setShowWebViewCloseViwe(true);
        sWebViewFragment.setFragmentTag("platMenuFragment_"+ tag);
        changeFragment(sWebViewFragment);
    }

    public void changeFragment(SBaseFragment sBaseFragment) {
        PL.d("添加新的的Fragment: ");
        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment previousFragment = getVisibleFragment();
        if (previousFragment != null) {//如果add一个新的fragment，不是第一次add的话，要先隐藏上一个再add新的才能显示出来
            ft.hide(previousFragment);
        }

        if (!sBaseFragment.isAdded()) {
            ft.add(contentFrameLayout.getId(), sBaseFragment);
            ft.addToBackStack(sBaseFragment.getFragmentTag());
            PL.d("manager.getBackStackEntryCount():" + fragmentManager.getBackStackEntryCount());

        }else{
            ft.show(sBaseFragment);
        }
        ft.commit();
    }

    public void changeFragmentNotBackStack(SBaseFragment sBaseFragment) {
        PL.d("添加新的的Fragment: ");
        FragmentTransaction ft = fragmentManager.beginTransaction();

        Fragment previousFragment = getVisibleFragment();
        if (previousFragment != null) {//如果add一个新的fragment，不是第一次add的话，要先隐藏上一个再add新的才能显示出来
            ft.hide(previousFragment);
        }

        if (!sBaseFragment.isAdded()) {
            ft.add(contentFrameLayout.getId(), sBaseFragment);
//            ft.addToBackStack(sBaseFragment.getFragmentTag());
            PL.d("manager.getBackStackEntryCount():" + fragmentManager.getBackStackEntryCount());

        }else{
            ft.show(sBaseFragment);
        }
        ft.commit();
    }

  /*  public void changeFragment(Fragment newFragment,Fragment oldFrg) {
        PL.d("切换已存在的HotVideoFragment");
        if (newFragment.isAdded() && oldFrg.isAdded()) {
            fragmentManager.beginTransaction().hide(oldFrg).show(newFragment).commit();
        }else {
            changeFragment(newFragment);
        }
    }*/


    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(contentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


    private void requestPhoneBindIsReceiveGiftbag() {

        final SGameBaseRequestBean gameBaseRequestBean = new SGameBaseRequestBean(this);
        gameBaseRequestBean.setCompleteUrl(ResConfig.getPlatPreferredUrl(this) + "app/giftbag/api/phoneBindIsReceiveGiftbag");
        AbsHttpRequest absHttpRequest = new AbsHttpRequest() {
            @Override
            public BaseReqeustBean createRequestBean() {

                return gameBaseRequestBean;
            }
        };
        absHttpRequest.setReqCallBack(new ISReqCallBack<PlatObjBaseModel<UserHasGetGiftModel>>() {
            @Override
            public void success(PlatObjBaseModel<UserHasGetGiftModel> baseModel, String rawResult) {
                PL.i("get requestPhoneBindIsReceiveGiftbag success");
                if (baseModel != null) {
                    PL.i(baseModel.getMessage());
                    userHasGetGiftModel = baseModel.getData();
                }

            }

            @Override
            public void timeout(String code) {
            }

            @Override
            public void noData() {
            }
        });
        absHttpRequest.excute(new TypeToken<PlatObjBaseModel<UserHasGetGiftModel>>(){}.getType());
    }


    private void requestUserBindInfo() {

        final AccountInjectionRequestBean accountInjectionRequest = new AccountInjectionRequestBean(this);
        accountInjectionRequest.setUserId(GamaUtil.getUid(this));
//        accountInjectionRequest.setGameCode(ResConfig.getGameCode(this));

        BaseLoginRequestTask baseRequestTask = new BaseLoginRequestTask(this){
            @Override
            public BaseReqeustBean createRequestBean() {
                super.createRequestBean();
                accountInjectionRequest.setSignature(SStringUtil.toMd5(accountInjectionRequest.getAppKey() + accountInjectionRequest.getGameCode()
                        + accountInjectionRequest.getUserId() + accountInjectionRequest.getTimestamp()));

                accountInjectionRequest.setRequestMethod("users_bind_info");
                return accountInjectionRequest;

            }
        };

        baseRequestTask.setSdkBaseRequestBean(accountInjectionRequest);


//        baseRequestTask.setLoadDialog(DialogUtil.createLoadingDialog(this,"Loading..."));
        baseRequestTask.setReqCallBack(new ISReqCallBack<UserBindInfoModel>() {
            @Override
            public void success(UserBindInfoModel baseModel, String rawResult) {

                PL.i("get user bind info finish");
                if (baseModel.isRequestSuccess()) {
                    PL.i("get user bind info success");
                    userBindInfoModel = baseModel;
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        baseRequestTask.excute(UserBindInfoModel.class);
    }

    private void requestPhoneAreaCode() {

        final SSdkBaseRequestBean sdkBaseRequestBean = new SSdkBaseRequestBean(this);
        sdkBaseRequestBean.setRequestMethod("areaCode");
        BaseLoginRequestTask baseRequestTask = new BaseLoginRequestTask(this){
            @Override
            public BaseReqeustBean createRequestBean() {
                super.createRequestBean();
                return sdkBaseRequestBean;

            }
        };
        baseRequestTask.setSdkBaseRequestBean(sdkBaseRequestBean);
        baseRequestTask.setReqCallBack(new ISReqCallBack<PlatArrayObjBaseModel<PhoneAreaCodeModel>>() {
            @Override
            public void success(PlatArrayObjBaseModel<PhoneAreaCodeModel> baseModel, String rawResult) {

                PL.i("requestPhoneAreaCode success");
                if (baseModel.isRequestSuccess()) {
                    phoneAreaCodeModels = baseModel.getData();
                }

            }

            @Override
            public void timeout(String code) {

            }

            @Override
            public void noData() {

            }
        });
        baseRequestTask.excute(new TypeToken<PlatArrayObjBaseModel<PhoneAreaCodeModel>>(){}.getType());

    }
}
