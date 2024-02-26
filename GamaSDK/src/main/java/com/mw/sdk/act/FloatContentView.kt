package com.mw.sdk.act

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.core.base.utils.ApkInfoUtil
import com.core.base.utils.PL
import com.core.base.utils.SStringUtil
import com.google.gson.Gson
import com.mw.sdk.R
import com.mw.sdk.bean.SGameBaseRequestBean
import com.mw.sdk.bean.res.ActDataModel
import com.mw.sdk.bean.res.FloatSwitchRes
import com.mw.sdk.bean.res.FloatConfigData
import com.mw.sdk.bean.res.MenuData
import com.mw.sdk.constant.FloatMenuType
import com.mw.sdk.login.widget.SLoginBaseRelativeLayout
import com.mw.sdk.utils.SdkUtil
import com.mw.sdk.widget.SBaseDialog
import com.mw.sdk.widget.SWebView
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter.OnItemClickListener
import com.zhy.adapter.recyclerview.base.ViewHolder

class FloatContentView : SLoginBaseRelativeLayout {

    var menuDatas: ArrayList<MenuData>? = null

    private lateinit var contentLayout: View

//    private lateinit var mActExpoAdapter:FloatMenuAdapter

    private lateinit var rightContentView: View
    private lateinit var menuRecyclerView: RecyclerView

    private lateinit var contentWebView: SWebView

    private lateinit var mFloatPersionCenterView: FloatPersionCenterView

    private lateinit var floatConfigData: FloatConfigData
    private lateinit var floatSwitchRes: FloatSwitchRes

    constructor(
        context: Context?,
        datas: List<MenuData>,
        sxDialog: SBaseDialog
    ) : super(context) {
        this.sBaseDialog = sxDialog;

        menuDatas = arrayListOf()

        val floatCfgData = SdkUtil.getFloatCfgData(context)
        val menuResData = SdkUtil.getFloatSwitchData(context)

        if (SStringUtil.isEmpty(floatCfgData) || SStringUtil.isEmpty(menuResData)){
            return
        }

        if (SStringUtil.isNotEmpty(floatCfgData)) {
            floatConfigData = Gson().fromJson(floatCfgData, FloatConfigData::class.java)
        }

        if (SStringUtil.isNotEmpty(menuResData)) {
            floatSwitchRes = Gson().fromJson(menuResData, FloatSwitchRes::class.java)
        }

        for (cfgMenu in floatConfigData.menuList){
            for (resMenu in floatSwitchRes.data.menuList){
                if (resMenu.code.equals(cfgMenu.code) && resMenu.isDisplay){
                    menuDatas?.add(cfgMenu)
                }
            }
        }

//        menuDatas?.addAll(datas)
//        for (i in 1..10)
//        {
//           val aMenuData = FloatMenuDataModel.MenuData()
//            aMenuData.menuImgUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgiWJdCY8rMjzHhNUaOoGpAk-Eh1x0Xzfbs-TwhFo34g&s"
//            aMenuData.title = "title_$i"
//            aMenuData.contentUrl = "https://www.baidu.com/"
//            menuDatas?.add(aMenuData)
//        }
        menuDatas?.first()?.isClick = true
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
        PL.d("FloatContentView init")
    }

    fun initView(): Unit {
        PL.d("FloatContentView initView")

        rightContentView = contentLayout.findViewById(R.id.id_ll_float_right_content)
        mFloatPersionCenterView = contentLayout.findViewById(R.id.id_FloatPersionCenterView)

        menuRecyclerView = contentLayout.findViewById(R.id.id_rv_float_left)
        val closeBgIV = contentLayout.findViewById<ImageView>(R.id.id_iv_float_back)

        contentWebView = contentLayout.findViewById(R.id.id_swv_content)
        contentWebView.addMWSDKJavascriptInterface(getsBaseDialog())

        closeBgIV.setOnClickListener {
            this.sBaseDialog?.dismiss()
        }

//        if (contentPageView != null){//contentPageView为空即为横版
//            //处理竖屏
//        }else{
        hand_H_view(context)
//        }

    }

    fun refreshData(datas: List<MenuData>): Unit {

        menuDatas?.let {
            it.clear()
            it.addAll(datas)
            menuDatas?.first()?.isClick = true
            menuRecyclerView?.adapter?.notifyDataSetChanged()
        }

    }

    override fun createView(context: Context?, layoutInflater: LayoutInflater?): View {
        PL.d("createView")
        contentLayout = layoutInflater!!.inflate(R.layout.float_content_layout, null)
        return contentLayout
    }

    private fun hand_H_view(context: Context?) {


        menuRecyclerView.layoutManager = LinearLayoutManager(getContext())


        menuDatas?.let {
            if (it.isNotEmpty()) {
                val firstData = it[0]

                val sr = SGameBaseRequestBean(context)
                sr.completeUrl = firstData.url
                contentWebView.loadUrl(sr.createPreRequestUrl())
            }
        }
        val menuCommonAdapter = object : CommonAdapter<MenuData>(
            this.context,
            R.layout.float_left_menu_item,
            menuDatas
        ) {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)

            }

            override fun convert(
                holder: ViewHolder,
                mActData: MenuData,
                position: Int
            ) {
                menuDatas?.let { datas ->

                    val mData = datas[position]
                    val menuIconIv = holder.getView<ImageView>(R.id.id_iv_menu_icon)
                    val menuTitleTv = holder.getView<TextView>(R.id.id_tv_menu_title)
                    val menuReddotView = holder.getView<View>(R.id.id_v_menu_reddot)
                    val loginTimestamp = SdkUtil.getSdkTimestamp(activity)

                    Glide.with(this@FloatContentView)
                            .load(mData.icon + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(ApkInfoUtil.getAppIcon(activity))
                            .into(menuIconIv)

                    menuTitleTv.text = mData.name

                    if (mData.isClick){
                        holder.itemView.setBackgroundColor(getContext().resources.getColor(R.color.white_c))
                        menuReddotView.visibility = View.GONE //被点击即消失红点
                    }else{
                        holder.itemView.setBackgroundColor(getContext().resources.getColor(R.color.c_EDEDED))
                    }

                    if (mData.isWithReddot && FloatingManager.getInstance().redDotRes != null && FloatingManager.getInstance().redDotRes.data != null &&
                        FloatingManager.getInstance().redDotRes.data.isCs){
                        menuReddotView.visibility = View.VISIBLE
                    }

//                    menuIv.setOnClickListener {
//
//                        if (mData.isClick){
//                            return@setOnClickListener
//                        }
//
//                        for (dataTemp: ActDataModel.ActData in datas){
//                           // == 对应于Java中的 equals()
//                           // ===对应于 Java中的 ==
//                            dataTemp.isClick = dataTemp === mData
//
//                        }
//                        menuRecyclerView?.adapter?.notifyDataSetChanged()
//
//                        titleTv.text = mData.title
//                        loadViewImage(mData, backBgIV, closeBgIV, titleBgIV)
//
//                        val sr = SGameBaseRequestBean(context)
//                        sr.completeUrl = mData.contentUrl
//                        contentWebView.loadUrl(sr.createPreRequestUrl())
//
//                    }
                }
            }
        }
        menuCommonAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, viewHolder: RecyclerView.ViewHolder?, i: Int) {

                PL.d("menuCommonAdapter onItemClick $i")
                menuDatas?.let {

                    val aMenuData  = it[i]

                    if (aMenuData.isClick){//已经点了
                        return
                    }

                    for (dataTemp: MenuData in it){
                        // == 对应于Java中的 equals()
                        // ===对应于 Java中的 ==
                        dataTemp.isClick = dataTemp === aMenuData

                    }


                    if (FloatMenuType.MENU_TYPE_MY == aMenuData.code){//我的部分
                        contentWebView.visibility = View.GONE
                        rightContentView.visibility = View.VISIBLE

                    }else{
                        contentWebView.visibility = View.VISIBLE
                        rightContentView.visibility = View.GONE

                        contentWebView.loadUrl(aMenuData.url)
                    }
                    if (FloatMenuType.MENU_TYPE_CS == aMenuData.code && FloatingManager.getInstance().redDotRes != null && FloatingManager.getInstance().redDotRes.data != null){
                        FloatingManager.getInstance().redDotRes.data.isCs = false
                        FloatingManager.getInstance().updateReddot(false)
                    }
                    menuRecyclerView.adapter?.notifyDataSetChanged()

                }

            }

            override fun onItemLongClick(
                view: View?,
                viewHolder: RecyclerView.ViewHolder?,
                i: Int
            ): Boolean {
                return false
            }


        })
        menuRecyclerView.adapter = menuCommonAdapter
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
    }

    private fun loadViewImage(
        mData: ActDataModel.ActData,
        backBgIV: ImageView,
        closeBgIV: ImageView,
        titleBgIV: ImageView
    ) {
        if (mData.titleImgUrl.isNotEmpty()) {//背景图片有的话，才一起加载

            val loginTimestamp = SdkUtil.getSdkTimestamp(activity)
            Glide.with(this@FloatContentView)
                .load(mData.backImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_back_bg)
                .into(backBgIV)
            Glide.with(this@FloatContentView)
                .load(mData.closeImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_close_bg)
                .into(closeBgIV)

            Glide.with(this@FloatContentView)
                .load(mData.titleImgUrl + "?" + loginTimestamp)
                .centerCrop()
                .placeholder(R.mipmap.act_title_bg)
                .into(titleBgIV)

        }
    }


}