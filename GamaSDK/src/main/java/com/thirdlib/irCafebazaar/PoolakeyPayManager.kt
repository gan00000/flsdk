package com.thirdlib.irCafebazaar

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.core.base.utils.PL
import com.core.base.utils.ResUtil
import com.core.base.utils.SStringUtil
import com.core.base.utils.ToastUtils
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.ConnectionState
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.callback.PurchaseQueryCallback
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.exception.DynamicPriceNotSupportedException
import ir.cafebazaar.poolakey.request.PurchaseRequest

class PoolakeyPayManager(val activity: AppCompatActivity){

    private lateinit var paymentConfiguration : PaymentConfiguration

    private val poolakeyPayment by lazy(LazyThreadSafetyMode.NONE) {
        Payment(context = activity, config = paymentConfiguration)
    }


    private var paymentConnection: Connection? = null

    private var isConnect = false

    internal var ppCallback: PPPayCallback? = null

    public fun startPurchase(activity: Activity, productId: String, payload: String, dynamicPriceToken: String?, callback: PPPayCallback) {

        ppCallback = callback

        var mw_cafebazaar_rsa_public_key = ResUtil.findStringByName(activity, "mw_cafebazaar_rsa_public_key")
        if (SStringUtil.isEmpty(mw_cafebazaar_rsa_public_key)){
            ToastUtils.toast(activity, "mw_cafebazaar_rsa_public_key is null")
            ppCallback?.fali("mw_cafebazaar_rsa_public_key is null")
            return
        }
        if (paymentConfiguration == null){
            paymentConfiguration = PaymentConfiguration(
                localSecurityCheck = SecurityCheck.Enable(mw_cafebazaar_rsa_public_key)
            )
        }

//        if (poolakeyPayment == null){
//            poolakeyPayment = Payment(context = activity, config = paymentConfiguration)
//        }

        startPaymentConnection { result ->
            if (result){
                purchaseProduct(productId, payload, dynamicPriceToken)
            }else{
                ppCallback?.fali("payment connect fail")
            }
        }
    }


    private fun purchaseProduct(productId: String, payload: String, dynamicPriceToken: String?) {

        if (SStringUtil.isEmpty(productId)){
            ppCallback?.fali("productId empty")
            return
        }
        if (isConnect && paymentConnection != null && paymentConnection?.getState() == ConnectionState.Connected) {

            poolakeyPayment.purchaseProduct(
                registry = activity.activityResultRegistry,
                request = PurchaseRequest(
                    productId = productId,
                    payload = payload,
                    dynamicPriceToken = dynamicPriceToken
                )
            ) {
                purchaseFlowBegan {
                    PL.d("purchaseFlowBegan")
                }
                failedToBeginFlow {
                    PL.d("failedToBeginFlow")
                    // bazaar need to update, in this case we only launch purchase without discount
                    if (it is DynamicPriceNotSupportedException) {
                        //ToastUtils.toast(activity, "Dynamic price token not supported in this bazaar version")
                        PL.d("failedToBeginFlow Dynamic price token not supported in this bazaar version")
                        purchaseProduct(productId, payload, null)
                    } else {
                        PL.d("failedToBeginFlow Purchase failed")
                        ppCallback?.fali("failedToBeginFlow Purchase failed")
                    }
                }
                purchaseSucceed { mPurchaseInfo ->
                    PL.d("purchaseSucceed")
                    consumePurchasedItem(mPurchaseInfo.purchaseToken)
                    ppCallback?.succeed(mPurchaseInfo)
                }
                purchaseCanceled {
                    PL.d("purchaseCanceled")
                    ppCallback?.cancel("")
                }
                purchaseFailed {
                    PL.d("purchaseFailed")
                    ppCallback?.fali("Purchase failed")
                }
            }
        }

    }

    private fun startPaymentConnection(block: (success:Boolean) -> Unit) {

        if (isConnect){
            PL.d("already connect")
            block(true)
            return
        }
        paymentConnection = poolakeyPayment.connect {
            connectionSucceed {
                PL.d("connectionSucceed")
                isConnect = true
                block(true)
            }
            connectionFailed {
                PL.d("connectionFailed")
                isConnect = false
                block(false)
            }
            disconnected {
                PL.d("disconnected")
                isConnect = false
            }
        }
    }

    private fun handlePurchaseQueryCallback(): PurchaseQueryCallback.() -> Unit = {
        querySucceed { purchasedItems ->
//            val productId = skuValueInput.text.toString()
//            purchasedItems.find { it.productId == productId }
//                ?.also { toast(R.string.general_user_purchased_item_message) }
//                ?: run { toast(R.string.general_user_did_not_purchased_item_message) }
        }
        queryFailed {
        }
    }

    private fun consumePurchasedItem(purchaseToken: String) {

        if (isConnect && paymentConnection != null && paymentConnection?.getState() == ConnectionState.Connected) {
            poolakeyPayment.consumeProduct(purchaseToken) {
                consumeSucceed {
                    PL.d("consumeSucceed")
                }
                consumeFailed {
                    PL.d("consumeFailed")
                }
            }
        }
    }


    public fun disconnect(activity: Activity?) {
        paymentConnection?.disconnect()
    }

}
