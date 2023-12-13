package com.mw.sdk.bean.res

import com.core.base.bean.BaseResponseModel
import java.io.Serializable

data class ActDataModel(var data: List<ActData>?): BaseResponseModel() {

    data class ActData(var menuSelectImgUrl:String = "",
                       var menuUnSelectImgUrl:String = "",
                       var titleImgUrl:String = "",
                       var backImgUrl:String = "",
                       var closeImgUrl:String = "",
                       var contentImgUrl:String = "",
                       var title:String = "",
                       var contentUrl:String = "",
                       var isContentLoad:Boolean = false,
                       var isClick:Boolean = false): Serializable
}