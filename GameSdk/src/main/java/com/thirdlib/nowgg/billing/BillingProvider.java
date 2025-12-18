package com.thirdlib.nowgg.billing;

/**
 * An interface that provides an access to BillingLibrary methods
 */
public interface BillingProvider {
    BillingManager getBillingManager();

    boolean isSwordPurchased();

    boolean isPackagePurchased();

    boolean isDiamondsPurchased();

    boolean isVIPMonthlySubscribed();
}