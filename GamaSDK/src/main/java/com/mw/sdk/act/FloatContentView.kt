package com.mw.sdk.act

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.core.base.utils.ApkInfoUtil
import com.core.base.utils.PL
import com.core.base.utils.SStringUtil
import com.core.base.utils.ScreenHelper
import com.google.gson.Gson
import com.mw.sdk.R
import com.mw.sdk.bean.SGameBaseRequestBean
import com.mw.sdk.bean.res.FloatMenuResData
import com.mw.sdk.bean.res.MenuData
import com.mw.sdk.callback.FloatCallback
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

//    private lateinit var floatConfigData: FloatConfigData
    private lateinit var floatMenuResData: FloatMenuResData

    var xFloatCallback:FloatCallback? = null

    constructor(
        context: Context?,
        datas: List<MenuData>,
        sxDialog: SBaseDialog,
        mFloatCallback:FloatCallback
    ) : super(context) {
        this.sBaseDialog = sxDialog;
        this.xFloatCallback = mFloatCallback
        menuDatas = arrayListOf()

//        val floatCfgData = SdkUtil.getFloatCfgData(context)
        val menuResData = SdkUtil.getFloatMenuResData(context)

        if (SStringUtil.isEmpty(menuResData)){
            return
        }

//        if (SStringUtil.isNotEmpty(floatCfgData)) {
//            floatConfigData = Gson().fromJson(floatCfgData, FloatConfigData::class.java)
//        }

        if (SStringUtil.isNotEmpty(menuResData)) {
            floatMenuResData = Gson().fromJson(menuResData, FloatMenuResData::class.java)
        }

        if (floatMenuResData != null && floatMenuResData.data != null && floatMenuResData.data.menuList != null){
            /*for (cfgMenu in floatConfigData.menuList){//以配置文件对象字段为准
                for (resMenu in floatMenuResData.data.menuList){
                    if (resMenu.code.equals(cfgMenu.code) && resMenu.isDisplay){
                        menuDatas?.add(cfgMenu)
                        break
                    }
                }
            }*/

            menuDatas?.addAll(floatMenuResData.data.menuList)
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

        menuDatas?.let {
            if (it.isNotEmpty()){
                it.first().isClick = true
            }
        }
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
        mFloatPersionCenterView.setFloatCallback {

            xFloatCallback?.switchAccount("")
            this.sBaseDialog?.dismiss()
            FloatingManager.getInstance().windowManagerFinish()
        }

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

//    fun refreshData(datas: List<MenuData>): Unit {
//
//        menuDatas?.let {
//            it.clear()
//            it.addAll(datas)
//            menuDatas?.first()?.isClick = true
//            menuRecyclerView?.adapter?.notifyDataSetChanged()
//        }
//
//    }

    override fun createView(context: Context?, layoutInflater: LayoutInflater?): View {
        PL.d("createView")
        contentLayout = layoutInflater!!.inflate(R.layout.float_content_layout, null)
        return contentLayout
    }

    private fun hand_H_view(context: Context?) {

        val layoutManager = LinearLayoutManager(getContext())
        menuRecyclerView.layoutManager = layoutManager
        if (ScreenHelper.isPortrait(context)){
            //配置布局，默认为vertical（垂直布局），下边这句将布局改为水平布局
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL;
        }


//        menuDatas?.let {
//            if (it.isNotEmpty()) {
//                val firstData = it[0]
//
//                val sr = SGameBaseRequestBean(context)
//                sr.completeUrl = firstData.url
//                contentWebView.loadUrl(sr.createPreRequestUrl())
//            }
//        }
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

                    if (FloatMenuType.MENU_TYPE_CS == mData.code){

                        Glide.with(this@FloatContentView)
                            .load(mData.icon + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(R.mipmap.icon_float_customer)
                            .into(menuIconIv)

                    }else if (FloatMenuType.MENU_TYPE_MY == mData.code){

                        Glide.with(this@FloatContentView)
                            .load(mData.icon + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(R.mipmap.icon_float_person)
                            .into(menuIconIv)

                    }else{
                        Glide.with(this@FloatContentView)
                            .load(mData.icon + "?" + loginTimestamp)
                            .centerCrop()
                            .placeholder(ApkInfoUtil.getAppIcon(activity))
                            .into(menuIconIv)
                    }

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

                        val sGameBaseRequestBean = SGameBaseRequestBean(activity)
                        sGameBaseRequestBean.completeUrl = aMenuData.url
                        contentWebView.loadUrl(sGameBaseRequestBean.createPreRequestUrl())
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

}