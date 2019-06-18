package com.gamamobi.onestore.api;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.gamamobi.onestore.api.BundleProtocol.ProductDetailResponse;
import com.gamamobi.onestore.api.BundleProtocol.PurchaseResult;
import com.gamamobi.onestore.api.BundleProtocol.PurchasedItemListResponse;
import com.onestore.extern.iap.IInAppPurchaseService;
import com.onestore.extern.iap.IInAppPurchaseService.Stub;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PurchaseClient {
    private static final String SERVICE_PACKAGE_NAME = "com.skt.skaf.OA00018282";
    private static final String SERVICE_CLASS_NAME = "com.onestore.extern.iap.InAppPurchaseService";
    private static final String SERVICE_ACTION_NAME = "com.onestore.extern.iap.InAppBillingService.ACTION";
    private Context mContext;
    private String mBase64PublicKey = "";
    private IInAppPurchaseService mInAppPurchaseService;
    private ServiceConnection mServiceConnection;
    private PurchaseClient.LoginFlowListener mLoginFlowListener;
    private PurchaseClient.PurchaseFlowListener mPurchaseFlowListener;

    public PurchaseClient(Context context) {
        this.mContext = context.getApplicationContext();
//        this.mBase64PublicKey = base64PublicKey;
    }

    public static void launchUpdateOrInstallFlow(Activity activity, UpdateFlowListener listener) {
        AppInstaller.updateOrInstall(activity, listener);
    }

    public void connect(final PurchaseClient.ServiceConnectionListener listener) {
        this.mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                PurchaseClient.this.mInAppPurchaseService = Stub.asInterface(service);
                listener.onConnected();
            }

            public void onServiceDisconnected(ComponentName name) {
                PurchaseClient.this.mInAppPurchaseService = null;
                listener.onDisconnected();
            }
        };

        try {
            this.mContext.bindService(this.buildIapServiceIntent(), this.mServiceConnection, 1);
        } catch (ClassNotFoundException var3) {
            listener.onErrorNeedUpdateException();
        }

    }

    public void terminate() {
        if (this.mContext != null && this.mServiceConnection != null) {
            try {
                this.mContext.unbindService(this.mServiceConnection);
            } catch (Exception var2) {
                Log.d("PurchaseClient", var2.toString());
            }

            this.mContext = null;
            this.mServiceConnection = null;
            this.mInAppPurchaseService = null;
        }

    }

    private Intent buildIapServiceIntent() throws ClassNotFoundException {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.skt.skaf.OA00018282", "com.onestore.extern.iap.InAppPurchaseService"));
        intent.setAction("com.onestore.extern.iap.InAppBillingService.ACTION");
        if (this.mContext.getPackageManager().resolveService(intent, 0) == null) {
            throw new ClassNotFoundException();
        } else {
            return intent;
        }
    }

    ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor();
    }

    public void isBillingSupportedAsync(final int apiVersion, final PurchaseClient.BillingSupportedListener listener) {
        final Handler handler = new Handler();
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    PurchaseClient.this.isBillingSupportedSync(apiVersion);
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess();
                        }
                    });
                } catch (Throwable var2) {
                    PurchaseClient.this.handleException(handler, listener, var2);
                }

            }
        });
    }

    void isBillingSupportedSync(int apiVersion) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else {
                int response = this.mInAppPurchaseService.isBillingSupported(apiVersion, this.mContext.getPackageName());
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(response)) {
                    throw new PurchaseClient.SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(response)) {
                    throw new PurchaseClient.NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(response)) {
                    throw new PurchaseClient.IapException(response);
                }
            }
        } else {
            throw new RemoteException();
        }
    }

    public void queryPurchasesAsync(final int apiVersion, final String productType, final PurchaseClient.QueryPurchaseListener listener) {
        final Handler handler = new Handler();
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    final List<PurchaseData> purchaseList = PurchaseClient.this.queryPurchasesSync(apiVersion, productType);
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess(purchaseList, productType);
                        }
                    });
                } catch (Throwable var2) {
                    PurchaseClient.this.handleException(handler, listener, var2);
                }

            }
        });
    }

    List<PurchaseData> queryPurchasesSync(int apiVersion, String productType) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else if (TextUtils.isEmpty(productType)) {
                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
            } else {
                String continuationKey = null;
                ArrayList purchaseList = new ArrayList();

                do {
                    Bundle bundle = this.mInAppPurchaseService.getPurchases(apiVersion, this.mContext.getPackageName(), productType, continuationKey);
                    PurchasedItemListResponse ownedItems = new PurchasedItemListResponse(bundle, this.mBase64PublicKey);

                    for(int i = 0; i < ownedItems.size(); ++i) {
                        purchaseList.add(ownedItems.getSinglePurchaseData(i));
                    }

                    continuationKey = ownedItems.getContinuationKey();
                } while(!TextUtils.isEmpty(continuationKey));

                return purchaseList;
            }
        } else {
            throw new RemoteException();
        }
    }

    public void queryProductsAsync(final int apiVersion, final ArrayList<String> productIdList, final String productType, final PurchaseClient.QueryProductsListener listener) {
        final Handler handler = new Handler();
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    final List<ProductDetail> productDetails = PurchaseClient.this.queryProductsSync(apiVersion, productIdList, productType);
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess(productDetails);
                        }
                    });
                } catch (Throwable var2) {
                    PurchaseClient.this.handleException(handler, listener, var2);
                }

            }
        });
    }

    List<ProductDetail> queryProductsSync(int apiVersion, ArrayList<String> productIdList, String productType) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else if (TextUtils.isEmpty(productType)) {
                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
            } else {
                Bundle products = new Bundle();
                products.putStringArrayList("productDetailList", productIdList);
                Bundle bundle = this.mInAppPurchaseService.getProductDetails(apiVersion, this.mContext.getPackageName(), productType, products);
                ProductDetailResponse res = new ProductDetailResponse(bundle);
                return res.getProductData();
            }
        } else {
            throw new RemoteException();
        }
    }

    public void consumeAsync(final int apiVersion, final PurchaseData purchaseData, final PurchaseClient.ConsumeListener listener) {
        final Handler handler = new Handler();
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    PurchaseClient.this.consumeSync(apiVersion, purchaseData);
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess(purchaseData);
                        }
                    });
                } catch (Throwable var2) {
                    PurchaseClient.this.handleException(handler, listener, var2);
                }

            }
        });
    }

    void consumeSync(int apiVersion, PurchaseData purchaseData) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else if (purchaseData != null && !TextUtils.isEmpty(purchaseData.getPurchaseId())) {
                Bundle bundle = this.mInAppPurchaseService.consumePurchase(apiVersion, this.mContext.getPackageName(), purchaseData.getPurchaseId());
                int response = bundle.getInt("responseCode");
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(response)) {
                    throw new PurchaseClient.SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(response)) {
                    throw new PurchaseClient.NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(response)) {
                    throw new PurchaseClient.IapException(response);
                }
            } else {
                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
            }
        } else {
            throw new RemoteException();
        }
    }

    public void manageRecurringProductAsync(final int apiVersion, final PurchaseData purchaseData, final String action, final PurchaseClient.ManageRecurringProductListener listener) {
        final Handler handler = new Handler();
        this.getExecutorService().execute(new Runnable() {
            public void run() {
                try {
                    PurchaseClient.this.manageRecurringProductSync(apiVersion, purchaseData, action);
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onSuccess(purchaseData, action);
                        }
                    });
                } catch (Throwable var2) {
                    PurchaseClient.this.handleException(handler, listener, var2);
                }

            }
        });
    }

    void manageRecurringProductSync(int apiVersion, PurchaseData purchaseData, String action) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else if (purchaseData != null && !TextUtils.isEmpty(purchaseData.getPurchaseId()) && !TextUtils.isEmpty(action)) {
                Bundle bundle = this.mInAppPurchaseService.manageRecurringProduct(apiVersion, this.mContext.getPackageName(), action, purchaseData.getPurchaseId());
                int response = bundle.getInt("responseCode");
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(response)) {
                    throw new PurchaseClient.SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(response)) {
                    throw new PurchaseClient.NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(response)) {
                    throw new PurchaseClient.IapException(response);
                }
            } else {
                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
            }
        } else {
            throw new RemoteException();
        }
    }

    public boolean launchPurchaseFlowAsync(final int apiVersion, final Activity activity, final int requestCode, final String productId, final String productName, final String productType, final String payload, final String gameUserId, final boolean promotionApplicable, final PurchaseClient.PurchaseFlowListener listener) {
        if (listener == null) {
            return false;
        } else {
            final Handler handler = new Handler();
            this.getExecutorService().execute(new Runnable() {
                public void run() {
                    try {
                        PurchaseClient.this.launchPurchaseFlowSync(apiVersion, activity, requestCode, productId, productName, productType, payload, gameUserId, promotionApplicable, listener);
                    } catch (Throwable var2) {
                        PurchaseClient.this.handleException(handler, listener, var2);
                    }

                }
            });
            return true;
        }
    }

    void launchPurchaseFlowSync(int apiVersion, Activity activity, int requestCode, String productId, String productName, String productType, String payload, String gameUserId, boolean promotionApplicable, PurchaseClient.PurchaseFlowListener listener) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else {
                byte[] payloadSize = payload.getBytes();
                if (activity != null && !TextUtils.isEmpty(productId) && !TextUtils.isEmpty(productType) && payloadSize.length <= 100) {
                    Bundle bundle;
                    if (TextUtils.isEmpty(gameUserId)) {
                        bundle = this.mInAppPurchaseService.getPurchaseIntent(apiVersion, this.mContext.getPackageName(), productId, productName, productType, payload);
                    } else {
                        Bundle extraParams = new Bundle();
                        extraParams.putString("gameUserId", gameUserId);
                        extraParams.putBoolean("promotionApplicable", promotionApplicable);
                        bundle = this.mInAppPurchaseService.getPurchaseIntentExtraParams(apiVersion, this.mContext.getPackageName(), productId, productName, productType, payload, extraParams);
                    }

                    if (bundle != null) {
                        int responseCode = bundle.getInt("responseCode");
                        if (IapResult.RESULT_OK.equalCode(responseCode)) {
                            Intent intent = (Intent)bundle.getParcelable("purchaseIntent");
                            if (intent != null) {
                                this.mPurchaseFlowListener = listener;
                                activity.startActivityForResult(intent, requestCode);
                            } else {
                                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_DATA_PARSING);
                            }
                        } else {
                            throw new PurchaseClient.IapException(IapResult.getResult(responseCode));
                        }
                    } else {
                        throw new PurchaseClient.IapException(IapResult.IAP_ERROR_DATA_PARSING);
                    }
                } else {
                    throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
                }
            }
        } else {
            throw new RemoteException();
        }
    }

    public boolean launchLoginFlowAsync(final int apiVersion, final Activity activity, final int requestCode, final PurchaseClient.LoginFlowListener listener) {
        if (listener == null) {
            return false;
        } else {
            final Handler handler = new Handler();
            this.getExecutorService().execute(new Runnable() {
                public void run() {
                    try {
                        PurchaseClient.this.launchLoginFlowSync(apiVersion, activity, requestCode, listener);
                    } catch (Throwable var2) {
                        PurchaseClient.this.handleException(handler, listener, var2);
                    }

                }
            });
            return true;
        }
    }

    void launchLoginFlowSync(int apiVersion, Activity activity, int requestCode, PurchaseClient.LoginFlowListener listener) throws RemoteException, PurchaseClient.SecurityException, PurchaseClient.NeedUpdateException, PurchaseClient.IapException {
        if (this.mInAppPurchaseService != null && this.mServiceConnection != null && this.mContext != null) {
            if (!AppInstaller.isInstalledOneStoreService(this.mContext)) {
                throw new PurchaseClient.NeedUpdateException();
            } else if (activity == null) {
                throw new PurchaseClient.IapException(IapResult.IAP_ERROR_ILLEGAL_ARGUMENT);
            } else {
                Bundle bundle = this.mInAppPurchaseService.getLoginIntent(apiVersion, this.mContext.getPackageName());
                if (bundle != null) {
                    int responseCode = bundle.getInt("responseCode");
                    if (IapResult.RESULT_OK.equalCode(responseCode)) {
                        Intent intent = (Intent)bundle.getParcelable("loginIntent");
                        if (intent != null) {
                            this.mLoginFlowListener = listener;
                            activity.startActivityForResult(intent, requestCode);
                        } else {
                            throw new PurchaseClient.IapException(IapResult.IAP_ERROR_DATA_PARSING);
                        }
                    } else {
                        throw new PurchaseClient.IapException(IapResult.getResult(responseCode));
                    }
                } else {
                    throw new PurchaseClient.IapException(IapResult.IAP_ERROR_DATA_PARSING);
                }
            }
        } else {
            throw new RemoteException();
        }
    }

    void handleException(Handler handler, final PurchaseClient.ErrorListener listener, final Throwable throwable) {
        if (handler != null && listener != null && throwable != null) {
            handler.post(new Runnable() {
                public void run() {
                    if (throwable instanceof RemoteException) {
                        listener.onErrorRemoteException();
                    } else if (throwable instanceof PurchaseClient.NeedUpdateException) {
                        listener.onErrorNeedUpdateException();
                    } else if (throwable instanceof PurchaseClient.SecurityException) {
                        listener.onErrorSecurityException();
                    } else if (throwable instanceof PurchaseClient.IapException) {
                        listener.onError(((PurchaseClient.IapException)throwable).getResult());
                    }

                }
            });
        }
    }

    public boolean handlePurchaseData(Intent intent) {
        if (this.mPurchaseFlowListener != null && intent != null) {
            PurchaseResult purchaseResult;
            try {
                purchaseResult = new PurchaseResult(intent);
            } catch (PurchaseClient.SecurityException var6) {
                this.mPurchaseFlowListener.onErrorSecurityException();
                return true;
            } catch (PurchaseClient.NeedUpdateException var7) {
                this.mPurchaseFlowListener.onErrorNeedUpdateException();
                return true;
            } catch (PurchaseClient.IapException var8) {
                this.mPurchaseFlowListener.onError(var8.getResult());
                return true;
            }

            try {
                if (Security.verifyPurchase(this.mBase64PublicKey, purchaseResult.getPurchaseData(), purchaseResult.getSignature())) {
                    this.mPurchaseFlowListener.onSuccess(purchaseResult.toPurchaseData());
                } else {
                    this.mPurchaseFlowListener.onError(IapResult.IAP_ERROR_SIGNATURE_VERIFICATION);
                }
            } catch (JSONException var4) {
                this.mPurchaseFlowListener.onError(IapResult.IAP_ERROR_DATA_PARSING);
            } catch (PurchaseClient.IapException var5) {
                this.mPurchaseFlowListener.onError(var5.getResult());
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean handleLoginData(Intent intent) {
        if (this.mLoginFlowListener != null && intent != null) {
            int responseCode = intent.getIntExtra("responseCode", -1);
            if (IapResult.RESULT_OK.equalCode(responseCode)) {
                this.mLoginFlowListener.onSuccess();
            } else if (IapResult.RESULT_SECURITY_ERROR.equalCode(responseCode)) {
                this.mLoginFlowListener.onErrorSecurityException();
            } else if (IapResult.RESULT_NEED_UPDATE.equalCode(responseCode)) {
                this.mLoginFlowListener.onErrorNeedUpdateException();
            } else {
                this.mLoginFlowListener.onError(IapResult.getResult(responseCode));
            }

            return true;
        } else {
            return false;
        }
    }

    public static class IapException extends Exception {
        private final IapResult mIapResult;

        public IapException(IapResult result) {
            this.mIapResult = result;
        }

        public IapException(int code) {
            this(IapResult.getResult(code));
        }

        public IapResult getResult() {
            return this.mIapResult;
        }
    }

    public static class SecurityException extends Exception {
        public SecurityException() {
        }
    }

    public static class NeedUpdateException extends Exception {
        public NeedUpdateException() {
        }
    }

    public interface ManageRecurringProductListener extends PurchaseClient.ErrorListener {
        void onSuccess(PurchaseData var1, String var2);
    }

    public interface ConsumeListener extends PurchaseClient.ErrorListener {
        void onSuccess(PurchaseData var1);
    }

    public interface QueryProductsListener extends PurchaseClient.ErrorListener {
        void onSuccess(List<ProductDetail> var1);
    }

    public interface QueryPurchaseListener extends PurchaseClient.ErrorListener {
        void onSuccess(List<PurchaseData> var1, String var2);
    }

    public interface BillingSupportedListener extends PurchaseClient.ErrorListener {
        void onSuccess();
    }

    public interface LoginFlowListener extends PurchaseClient.ErrorListener {
        void onSuccess();
    }

    public interface PurchaseFlowListener extends PurchaseClient.ErrorListener {
        void onSuccess(PurchaseData var1);
    }

    public interface UpdateFlowListener {
        void onSelect();
    }

    public interface ServiceConnectionListener {
        void onConnected();

        void onDisconnected();

        void onErrorNeedUpdateException();
    }

    public interface ErrorListener {
        void onError(IapResult var1);

        void onErrorRemoteException();

        void onErrorSecurityException();

        void onErrorNeedUpdateException();
    }
}

