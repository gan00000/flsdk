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
import com.core.base.utils.PL
import com.mw.sdk.R
import com.mw.sdk.adapter.ActExpoAdapter
import com.mw.sdk.data.ActData
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout
import com.mw.sdk.widget.Vp2IndicatorView
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class ActExpoView : SLoginBaseRelativeLayout {

    var actDatas: ArrayList<ActData>? = null

    var currentPageIndex = 0
    var mActExpoAdapter:ActExpoAdapter? = null
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

    override fun createView(context: Context?, layoutInflater: LayoutInflater?): View {
        val actView = layoutInflater!!.inflate(R.layout.activity_act, null)
        val titleBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_title_bg)
        val backBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_back_bg)
        val closeBgIV = actView.findViewById<ImageView>(R.id.mw_act_iv_close_bg)
        val titleTv = actView.findViewById<TextView>(R.id.mw_act_tv_title)
        val contentPageView = actView.findViewById<ViewPager2>(R.id.mw_act_pv2_content)

        closeBgIV.setOnClickListener {
            this.sBaseDialog?.dismiss()
        }

        actDatas = arrayListOf<ActData>()
        for (i in 0..10) {
            val actData = ActData()
            actData.contentImgUrl = "https://www.baidu.com/"
            actData.title = "title=$i"
            if (i == 0){
                actData.isClick = true
            }
            actDatas!!.add(actData)
        }

        if (contentPageView !== null){//contentPageView为空即为横版
            //处理竖屏
            handle_V_view(context, actView, titleBgIV, backBgIV, contentPageView, titleTv)
        }else{

            hand_H_view(context, actView, titleBgIV, backBgIV, titleTv)

        }

        return actView
    }

    private fun hand_H_view(context: Context?,
                            actView: View,
                            titleBgIV: ImageView,
                            backBgIV: ImageView,
                            titleTv: TextView) {


        //横版
        val menuRecyclerView = actView.findViewById<RecyclerView>(R.id.mw_act_rv_menu)
        val contentWebView = actView.findViewById<WebView>(R.id.mw_act_wv_item)
        menuRecyclerView.setLayoutManager(LinearLayoutManager(getContext()))

        actDatas?.let {
            contentWebView.loadUrl(it[0].contentImgUrl)
        }
        val menuCommonAdapter = object : CommonAdapter<ActData>(
            this.context,
            R.layout.activity_act_menu_item,
            actDatas
        ) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

            }

            override fun convert(
                holder: ViewHolder,
                mActData: ActData,
                position: Int
            ) {
                actDatas?.let { datas->

                    val mData = datas[position]

                    val menuIv = holder.getView<ImageView>(R.id.mw_act_iv_menu)
                    if (mData.isClick){
                        menuIv.setImageResource(R.mipmap.img_act_menu_select)
                    }else{
                        menuIv.setImageResource(R.mipmap.img_act_menu_unselect)
                    }

                    menuIv.setOnClickListener {

                        if (mData.isClick){
                            return@setOnClickListener
                        }
                        titleTv.text = mData.title
                        contentWebView.loadUrl(mData.contentImgUrl)
                        for (dataTemp:ActData in datas){
                            dataTemp.isClick = dataTemp == mData
                        }
                        menuRecyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        menuRecyclerView.adapter = menuCommonAdapter
    }

    private fun handle_V_view(  //竖屏
        context: Context?,
        actView: View,
        titleBgIV: ImageView,
        backBgIV: ImageView,
        contentPageView: ViewPager2,
        titleTv: TextView
    ) {
        //        contentPageView.setPageTransformer(mAnimator)
        val indicatorView = actView.findViewById<Vp2IndicatorView>(R.id.mw_act_indicatorView)

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

        mActExpoAdapter = ActExpoAdapter(context, actDatas!!)

        contentPageView.adapter = mActExpoAdapter

        contentPageView.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, offset: Float, offsetPx: Int) {

                PL.d("OnPageChangeCallback onPageScrolled position = $position")

            }

            override fun onPageSelected(position: Int) {
                PL.d("OnPageChangeCallback onPageSelected position = $position")
                mActExpoAdapter?.let {
                    val mData = it.actDatas[position]
                    titleTv.text = mData.title
                    currentPageIndex = position

                    indicatorView?.pageSelected(contentPageView, position)
                }
            }
        })

        indicatorView.attachToViewPager2(contentPageView)
    }


}