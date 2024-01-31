package com.mw.sdk.bean.res

import com.core.base.bean.BaseResponseModel
import java.io.Serializable

data class FloatMenuDataModel(var data: List<MenuData>?): BaseResponseModel() {

    data class MenuData(var menuImgUrl:String = "",
                       var title:String = "",
                       var contentUrl:String = "",
                       var isContentLoad:Boolean = false,
                       var isClick:Boolean = false): Serializable
}
