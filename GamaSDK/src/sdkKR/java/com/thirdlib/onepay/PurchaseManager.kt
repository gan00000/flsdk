package com.thirdlib.onepay

import android.app.Activity
import android.content.Context
import android.util.Log
import com.core.base.utils.PL
import com.gaa.sdk.iap.*
import com.gaa.sdk.iap.PurchaseClient.ProductType

/**
 * PurchaseManager는 InApp Purchase SDK의 [PurchaseClient]을 쉽게 사용할 수 있도록 만들어진 Helper Class입니다.
 * 해당 class를 이용하여 IAP SDK를 적용 할 경우 내부 주석을 참고하여 개발해주세요.
 *
 * [PurchaseManager.Callback] 은 InApp Purchase SDK의 응답 결과를 App으로 전달하기 위한 callback interface입니다.
 *
 */
class PurchaseManager(private val context: Context, private val callback: Callback) : PurchasesUpdatedListener {
//    private val TAG = PurchaseManager::class.java.simpleName

    private var mPurchaseClient: PurchaseClient? = PurchaseClient.newBuilder(context)
//        .setBase64PublicKey(AppSecurity.getPublicKey())
        .setListener(this)
        .build()

    private var mTokenToBe: MutableSet<String>? = null
    private var isServiceConnected = false

    interface Callback {
        /**
         * [PurchaseManager.startConnection]를 통해 InApp Purchase SDK와 원스토어 간 연결이 완료될 경우 호출됩니다.
         */
        fun onPurchaseClientSetupFinished()

        /**
         * [PurchaseManager.consumeAsync] 가 성공했을 경우 호출됩니다.
         */
        fun onConsumeFinished(purchaseData: PurchaseData, iapResult: IapResult)

        /**
         * [PurchaseManager.acknowledgeAsync] 가 성공했을 경우 호출됩니다.
         */
        fun onAcknowledgeFinished(purchaseData: PurchaseData, iapResult: IapResult)

        /**
         * [PurchaseManager.launchPurchaseFlow], [PurchaseManager.queryPurchasesAsync]가 성공했을 경우 호출됩니다.
         */
        fun onPurchaseSucceed(purchases: List<PurchaseData>)

        fun onQueryPurchaseSucceed(purchases: List<PurchaseData>)
        fun onPayingQueryPurchaseSucceed(purchases: List<PurchaseData>)

        /**
         * [PurchaseManager.launchPurchaseFlow] 응답이 취소 또는 실패 시 호출됩니다.
         */
        fun onPurchaseFailed(iapResult: IapResult)

        /**
         * API 호출 시 원스토어 로그인이 필요할 경우 호출됩니다. [PurchaseManager.launchLoginFlow]를 통해 원스토어 로그인을 시도 할 수 있습니다.
         */
        fun onNeedLogin()

        /**
         * 원스토어의 버전이 SDK지원보전 보다 낮은 경우 호출됩니다. [PurchaseManager.launchUpdateOrInstall]를 통해 업데이트 할 수 있습니다.
         */
        fun onNeedUpdate()

        /**
         * [PurchaseManager.launchPurchaseFlow] 를 제외한 모든 API의 응답이 실패했을 경우 호출됩니다.
         */
        fun onError(message: String)
    }

    init {
        startConnection {
            callback.onPurchaseClientSetupFinished()
            PL.i("Setup successful. Querying inventory.")
        }
    }

    /**
     * 원스토어 서비스와 연결시도 합니다.
     * @param executeOnSuccess 원스토어 서비스 연결 성공 이후 람다 형식으로 로직 수행
     */
    fun startConnection(executeOnSuccess: () -> Unit) {
        mPurchaseClient?.startConnection(object : PurchaseClientStateListener {
            override fun onSetupFinished(iapResult: IapResult) {
                if (iapResult.isSuccess) {
                    isServiceConnected = true
                    executeOnSuccess()
                    return
                }

                handleIapResultError(iapResult)
            }

            override fun onServiceDisconnected() {
                isServiceConnected = false
            }
        })
    }

    // =================================================================================================================
    // implements PurchasesUpdatedListener
    // =================================================================================================================
    /**
     * 상품 구매에 대한 결과가 전송됩니다.
     * @param iapResult 구매 업데이트 결과
     * @param purchaseData 구매 데이터
     * 将发送产品购买结果。
     * @param iapResult 购买更新结果
     * @param buyData 购买数据
     */
    override fun onPurchasesUpdated(iapResult: IapResult, purchaseData: List<PurchaseData>?) {
        if (iapResult.isSuccess) {
            if (purchaseData != null) {
                callback.onPurchaseSucceed(purchaseData)
            }
            return
        }
        callback.onPurchaseFailed(iapResult)
    }

    /**
     * 원스토어 서비스와의 연결을 해제합니다.
     */
    fun destroy() {
        mPurchaseClient?.endConnection()
        mPurchaseClient = null
    }

    fun getStoreInfo(success: (String) -> Unit) {
        executeServiceRequest {
            mPurchaseClient?.getStoreInfoAsync { iapResult, code ->
                if (iapResult.isSuccess) {
                    success(code)
                }
            }
        }
    }

    /**
     * 원스토어 서비스 모듈이 없거나 버전이 낮을 경우, 실행합니다.
     * 성공 결과 전달시 서비스 연결을 시도 합니다.
     * @param next 업데이트 / 설치 성공 이후 로직 수행
     */
    fun launchUpdateOrInstall(mActivity: Activity, next: () -> Unit) {
        mPurchaseClient?.launchUpdateOrInstallFlow(mActivity) { iapResult ->
            if (iapResult.isSuccess) {
                startConnection {
                    callback.onPurchaseClientSetupFinished()
                    next()
                }
            } else {
                handleIapResultError(iapResult)
            }
        }
    }

    /**
     * 상품 구매
     * @param purchaseParam 구매데이터
     */
    fun launchPurchaseFlow(mActivity: Activity, purchaseParam: PurchaseFlowParams) {
        executeServiceRequest {
            // receive result from onPurchaseUpdated
            PL.i("==================================================")
            PL.i( "productId token : ${purchaseParam.productId}")
            PL.i( "productName : ${purchaseParam.productName}")
            PL.i( "developerPayload : ${purchaseParam.developerPayload}")
            PL.i( "oldPurchaseToken: ${purchaseParam.oldPurchaseToken}")
            PL.i( "productType: ${purchaseParam.productType}")
            PL.i( "prorationMode: ${purchaseParam.prorationMode}")
            PL.i( "isPromotionApplicable: ${purchaseParam.isPromotionApplicable}")
            PL.i( "==================================================")
            mPurchaseClient?.launchPurchaseFlow(mActivity, purchaseParam)
        }
    }

    /**
     * 등록된 상품을 조회합니다.
     * 개발자 센터에서 앱등록시 등록한 상품ID 값의 배열과 상품타입, 구매 상품에 대한 인터페이스를 인자로 받습니다.
     * @param productIdList 상품 ID 배열
     * @param productType 상품 타입
     * @param listener 구매 상품 인터페이스
     */
    fun queryProductDetailAsync(productIdList: List<String>, @ProductType productType: String?, listener: ProductDetailsListener?) {
        executeServiceRequest {
            val params = ProductDetailsParams.newBuilder()
                .setProductIdList(productIdList)
                .setProductType(productType)
                .build()
            mPurchaseClient?.queryProductDetailsAsync(params, listener!!)
        }
    }

    /**
     * 관리형 상품에 대한 소비를 진행합니다.
     * 상품 구매 완료 후 3일 이내 소비 되지 않았을 경우, 사용자에게 상품이 지급되지 않습니다.
     * 구매 완료시 전달받은 구매데이터를 인자로 받습니다.
     * @param purchaseData 구매데이터
     */
    fun consumeAsync(purchaseData: PurchaseData, mConsumeListener: ConsumeListener) {
//        if (mTokenToBe == null) {
//            mTokenToBe = HashSet()
//        }
//        else if (mTokenToBe!!.contains(purchaseData.purchaseToken)) {
//            PL.i( "Token was already scheduled to be consumed - skipping...")
//            return
//        }
//        mTokenToBe?.add(purchaseData.purchaseToken)

        executeServiceRequest {
            val params = ConsumeParams.newBuilder().setPurchaseData(purchaseData).build()
            mPurchaseClient?.consumeAsync(
                params
            ) { iapResult, data ->
                if (iapResult.isSuccess) {
                    if (data != null) {
                        if (purchaseData.purchaseToken == data.purchaseToken) {
//                            mTokenToBe?.remove(data.purchaseToken)
                            PL.i("consumeAsync success")
//                            callback.onConsumeFinished(data, iapResult)
                        } else {
                            PL.i("consumeAsync purchaseToken not equal")
//                            callback.onError("purchaseToken not equal")
                        }
                    } else {
                        PL.i("consumeAsync nothing purchase data")
//                        callback.onError("nothing purchase data")
                    }
                } else {
//                    handleIapResultError(iapResult)
                    PL.i("consumeAsync IapResultErro")
                }
                callback.onConsumeFinished(data, iapResult)
                mConsumeListener.onConsumeResponse(iapResult, data)
            }
        }
    }

    /**
     * 비소비성 상품을 인증합니다.
     * PurchaseData.isAcknowledge 함수를 사용하여 인증 여부를 판단할 수 있습니다.
     * @param purchaseData 구매데이터
     */
    fun acknowledgeAsync(purchaseData: PurchaseData) {
        if (mTokenToBe == null) {
            mTokenToBe = HashSet()
        } else if (mTokenToBe!!.contains(purchaseData.purchaseToken)) {
            PL.i( "Token was already scheduled to be acknowledged - skipping...")
            return
        }
        mTokenToBe?.add(purchaseData.purchaseToken)
        executeServiceRequest {
            val params = AcknowledgeParams.newBuilder().setPurchaseData(purchaseData).build()
            mPurchaseClient?.acknowledgeAsync(params) { iapResult, data ->
                if (iapResult.isSuccess) {
                    if (data != null) {
                        if (purchaseData.purchaseToken == data.purchaseToken) {
                            mTokenToBe?.remove(data.purchaseToken)
                            callback.onAcknowledgeFinished(data, iapResult)
                        } else {
                            callback.onError("purchaseToken not equal")
                        }
                    } else {
                        callback.onError("purchaseToken not equal")
                    }
                } else {
                    handleIapResultError(iapResult)
                }
            }
        }
    }

    /**
     * TODO: 개발사에서는 소비되지 않는 관리형상품(inapp)에 대해, 애플리케이션의 적절한 life cycle 에 맞춰 구매내역조회를 진행 후 소비를 진행해야합니다.
     *
     * 개발사에서 필요한 상품타입을 인자로 전달하여 사용하여야 합니다.
     *
     * 구매내역조회 API를 이용하여 소비되지 않는 관리형상품(inapp)과 구독형상품(sub) 목록을 받아옵니다.
     * 관리형상품(inapp)의 경우 소비를 하지 않을 경우 재구매요청을 하여도 구매가 되지 않습니다. 꼭, 소비 과정을 통하여 소모성상품 소비를 진행하여야합니다.
     * manageRecurringProduct API를 통해 해지예약요청을 할 경우 recurringState는 0 -> 1로 변경됩니다. 해지예약 취소요청을 할경우 recurringState는 1 -> 0로 변경됩니다.
     *
     * @param types 상품타입
     */
    fun queryPurchasesAsync(isPaying: Boolean, @ProductType vararg types: String) {
        val result: MutableList<PurchaseData> = ArrayList()
        val time = System.currentTimeMillis()

        executeServiceRequest {
            if (types.isNotEmpty()) {
                types.forEachIndexed { index, type ->
                    mPurchaseClient?.queryPurchasesAsync(type) { iapResult, purchaseData ->
                        PL.i( "$type - Querying purchases elapsed time: ${System.currentTimeMillis().minus(time)} ms")
                        if (iapResult.isSuccess) {
                            result.addAll(purchaseData!!)
                        } else {
                            PL.i("$type - queryPurchasesAsync() got an error response code: " + iapResult.responseCode)
                        }

                        if (types.size - 1 == index) {
                            if (isPaying) {
                                callback.onPayingQueryPurchaseSucceed(result)
                            }else{
                                callback.onQueryPurchaseSucceed(result)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 월정액 상품 상태 변경을 합니다.
     * v21에서는 Deprecate 된 기능입니다.
     * @param purchaseData 구매데이터
     * @param recurringAction 월정액 상품 변경할 상태
     * @param listener 상태 변경 결과 인터페이스
     */
    fun manageRecurringProductAsync(purchaseData: PurchaseData, recurringAction: String, listener: RecurringProductListener?) {
        executeServiceRequest {
            val params = RecurringProductParams.newBuilder()
                .setPurchaseData(purchaseData)
                .setRecurringAction(recurringAction)
                .build()
            mPurchaseClient?.manageRecurringProductAsync(params, listener!!)
        }
    }

    /**
     * 구매한 구독 상품 내역을 확인 화면으로 이동합니다.
     * @param purchaseData 구매데이터
     */
    fun launchSubscriptionsMenu(mActivity: Activity, purchaseData: PurchaseData?) {
        var params: SubscriptionParams? = null
        if (purchaseData != null) {
            params = SubscriptionParams.newBuilder()
                .setPurchaseData(purchaseData)
                .build()
        }
        mPurchaseClient?.launchManageSubscription(mActivity, params)
    }

    /**
     * 구독 업그레이드와 다운그레이드
     * 업그레이드는 현재 구독한 상품보다 절대 금액이 높을 경우,
     * 다운그레이드는 현재 구독한 상품보다 절대 금액이 낮은 경우, 수행된다.
     * 현재 구독 상품과 다른 상품을 선택 해야 한다.
     * @param purchase 이전 구매 정보
     * @param product 구독할 상품
     * @param option 비례배분 옵션 (설정 화면에서 변경 가능)
     */
    fun launchUpdateSubscription(mActivity: Activity, purchase: PurchaseData, product: ProductDetail, @PurchaseFlowParams.ProrationMode option: Int) {
        PL.i( "==================================================")
        PL.i( "purchase token : ${purchase.purchaseToken}")
        PL.i( "option : $option")
        PL.i( "product : ${product.productId}")
        PL.i( "acknowledge: ${purchase.isAcknowledged}")
        PL.i( "name : ${product.title}")
        PL.i( "==================================================")

        val subUpdateParam = PurchaseFlowParams.SubscriptionUpdateParams.newBuilder()
            .setOldPurchaseToken(purchase.purchaseToken)
            .setProrationMode(option)
            .build()

        /**
         * [PurchaseClient.launchPurchaseFlow] API를 이용하여 구매 요청을 진행합니다.
         *
         * 기본적으로 개발자 센터에 등록된 상품명이 결제 화면에 노출되지만
         * setProductName() 사용 시 결제 화면의 노출되는 상품명을 변경할 수 있습니다.
         *
         * 업데이트할 구독 상품 정보를 setSubscriptionUpdateParams로 전달합니다.
         * 
         */
        val devPayload = ""//AppSecurity.generatePayload()
        val params = PurchaseFlowParams.newBuilder()
            .setProductId(product.productId)             // mandatory
            .setProductType(product.type)                // mandatory
            .setDeveloperPayload(devPayload)             // optional
//            .setProductName(product.title)               // optional
            .setSubscriptionUpdateParams(subUpdateParam) // mandatory
            .build()

        launchPurchaseFlow(mActivity,params)
    }

    private fun executeServiceRequest(done: () -> Unit) {
        if (isServiceConnected) {
            done()
        } else {
            startConnection { done() }
        }
    }

    public fun handleIapResultError(iapResult: IapResult) {
        when (iapResult.responseCode) {
            PurchaseClient.ResponseCode.RESULT_NEED_LOGIN -> {
                PL.i("handleError() RESULT_NEED_LOGIN")
                callback.onNeedLogin()
            }
            PurchaseClient.ResponseCode.RESULT_NEED_UPDATE -> {
                PL.i( "handleErrorCode() RESULT_NEED_UPDATE")
                callback.onNeedUpdate()
            }
            else -> {
                val message = iapResult.message + "(" + iapResult.responseCode + ")"
                PL.i("handleErrorCode() error: $message")
                callback.onError(message)
            }
        }
    }
}