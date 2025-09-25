package com.thirdlib.irCafebazaar

import ir.cafebazaar.poolakey.entity.PurchaseInfo

interface PPPayCallback {

    fun succeed(mPurchaseInfo : PurchaseInfo)

    fun fali(msg:String)

    fun cancel(msg:String)

}