package com.mw.sdk.act

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.core.base.utils.PL
import com.mw.sdk.R
import com.mw.sdk.adapter.ActExpoAdapter
import com.mw.sdk.bean.SGameBaseRequestBean
import com.mw.sdk.bean.res.ActDataModel
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout
import com.mw.sdk.utils.SdkUtil
import com.mw.sdk.widget.SBaseDialog
import com.mw.sdk.widget.SWebView
import com.mw.sdk.widget.Vp2IndicatorView
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class ActExpoView : SLoginBaseRelativeLayout {

    var actDatas: ArrayList<ActDataModel.ActData>? = null

    private lateinit var actView:View

    private var currentPageIndex = 0
    private var mActExpoAdapter:ActExpoAdapter? = null
    private var indicatorView:Vp2IndicatorView? = null
    private var contentPageView:ViewPager2? = null

    private var menuRecyclerView:RecyclerView? = null

    constructor(context: Context?, datas: List<ActDataModel.ActData>, sxDialog: SBaseDialog) : super(context)
    {
        this.sBaseDialog = sxDialog;
        actDatas = arrayListOf()
        actDatas?.addAll(datas)
        actDatas?.first()?.isClick = true
        initView()
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        PL.d("ActExpoView init")
    }

    fun initView(): Unit {
        PL.d("ActExpoView initView")

        val titleBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_title_bg)
        val backBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_back_bg)
        val closeBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_close_bg)
        val titleTv = actView.findViewById<TextView>(R.id.mw_act_tv_title)
        contentPageView = actView.findViewById<ViewPager2>(R.id.mw_act_pv2_content)

        closeBgIV.setOnClickListener {
            this.sBaseDialog?.dismiss()
        }

        if (contentPageView != null){//contentPageView为空即为横版
            //处理竖屏
            handle_V_view(context, actView, titleBgIV, backBgIV, closeBgIV,
                contentPageView!!, titleTv)
        }else{
            hand_H_view(context, actView, titleBgIV, backBgIV, closeBgIV, titleTv)
        }

    }
    fun refreshData(datas: List<ActDataModel.ActData>): Unit {

        actDatas?.let {
            it.clear()
            it.addAll(datas)
            actDatas?.first()?.isClick = true
            mActExpoAdapter?.notifyDataSetChanged()
            menuRecyclerView?.adapter?.notifyDataSetChanged()
            indicatorView?.attachToViewPager2(contentPageView)
        }

    }
    override fun createView(context: Context?, layoutInflater: LayoutInflater?): View {
        PL.d("createView")
        actView = layoutInflater!!.inflate(R.layout.activity_act, null)
        return actView
    }

    private fun hand_H_view(context: Context?,
                            actView: View,
                            titleBgIV: ImageView,
                            backBgIV: ImageView,
                            closeBgIV: ImageView,
                            titleTv: TextView) {


        //横版
        menuRecyclerView = actView.findViewById<RecyclerView>(R.id.mw_act_rv_menu)
        val contentWebView = actView.findViewById<SWebView>(R.id.mw_act_wv_item)
        menuRecyclerView?.setLayoutManager(LinearLayoutManager(getContext()))
        contentWebView.addMWSDKJavascriptInterface(getsBaseDialog())

        actDatas?.let {
            if (it.isNotEmpty()) {
                val firstData = it[0]
                titleTv.text = firstData.title
                loadViewImage(firstData, backBgIV, closeBgIV, titleBgIV)

                val sr = SGameBaseRequestBean(context)
                sr.completeUrl = firstData.contentUrl
                contentWebView.loadUrl(sr.createPreRequestUrl())
            }
        }
        val menuCommonAdapter = object : CommonAdapter<ActDataModel.ActData>(
            this.context,
            R.layout.activity_act_menu_item,
            actDatas
        ) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

            }

            override fun convert(
                holder: ViewHolder,
                mActData: ActDataModel.ActData,
                position: Int
            ) {
                actDatas?.let { datas->

                    val mData = datas[position]

                    val menuIv = holder.getView<ImageView>(R.id.mw_act_iv_menu)
                    val loginTimestamp = SdkUtil.getSdkTimestamp(activity)

                    if (mData.isClick){
//                        menuIv.setImageResource(R.mipmap.img_act_menu_select)
                        Glide.with(this@ActExpoView)
                            .load(mData.menuSelectImgUrl + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(R.mipmap.img_act_menu_select)
                            .into(menuIv)

                    }else{
//                        menuIv.setImageResource(R.mipmap.img_act_menu_unselect)

                        Glide.with(this@ActExpoView)
                            .load(mData.menuUnSelectImgUrl + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(R.mipmap.img_act_menu_unselect)
                            .into(menuIv)
                    }

                    menuIv.setOnClickListener {

                        if (mData.isClick){
                            return@setOnClickListener
                        }

                        for (dataTemp: ActDataModel.ActData in datas){
                           // == 对应于Java中的 equals()
                           // ===对应于 Java中的 ==
                            dataTemp.isClick = dataTemp === mData

                        }
                        menuRecyclerView?.adapter?.notifyDataSetChanged()

                        titleTv.text = mData.title
                        loadViewImage(mData, backBgIV, closeBgIV, titleBgIV)

                        val sr = SGameBaseRequestBean(context)
                        sr.completeUrl = mData.contentUrl
                        contentWebView.loadUrl(sr.createPreRequestUrl())

                    }
                }
            }
        }
        menuRecyclerView?.adapter = menuCommonAdapter
    }

    private fun handle_V_view(  //竖屏
        context: Context?,
        actView: View,
        titleBgIV: ImageView,
        backBgIV: ImageView,
        closeBgIV: ImageView,
        contentPageView: ViewPager2,
        titleTv: TextView
    ) {
        //        contentPageView.setPageTransformer(mAnimator)
        indicatorView = actView.findViewById<Vp2IndicatorView>(R.id.mw_act_indicatorView)

        backBgIV.setOnClickListener {

            mActExpoAdapter?.let {

                if (it.webViewMap.size > currentPageIndex) {
                    val webView = it.webViewMap[currentPageIndex]
                    if (webView != null) {
                        if (webView.canGoBack()) {
                            webView.goBack()
                        }
                    }
                }
            }

        }

        mActExpoAdapter = ActExpoAdapter(context, actDatas!!, getsBaseDialog())

        contentPageView.adapter = mActExpoAdapter

        contentPageView.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, offset: Float, offsetPx: Int) {

                PL.d("OnPageChangeCallback onPageScrolled position = $position")

            }

            override fun onPageSelected(position: Int) {
                PL.d("OnPageChangeCallback onPageSelected position = $position")
                mActExpoAdapter?.let {
                    currentPageIndex = position
                    indicatorView?.pageSelected(contentPageView, position)

                    val mData = it.actDatas[position]
                    titleTv.text = mData.title

                    loadViewImage(mData, backBgIV, closeBgIV, titleBgIV)

                }
            }
        })

        indicatorView?.attachToViewPager2(contentPageView)
    }

    private fun loadViewImage(
        mData: ActDataModel.ActData,
        backBgIV: ImageView,
        closeBgIV: ImageView,
        titleBgIV: ImageView
    ) {
        if (mData.titleImgUrl.isNotEmpty()) {//背景图片有的话，才一起加载

            val loginTimestamp = SdkUtil.getSdkTimestamp(activity)
            Glide.with(this@ActExpoView)
                .load(mData.backImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_back_bg)
                .into(backBgIV)
            Glide.with(this@ActExpoView)
                .load(mData.closeImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_close_bg)
                .into(closeBgIV)

            Glide.with(this@ActExpoView)
                .load(mData.titleImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_bg)
                .into(titleBgIV)

        }
    }


}