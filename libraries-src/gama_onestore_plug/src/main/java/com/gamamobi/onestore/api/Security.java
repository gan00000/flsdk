//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

import android.text.TextUtils;
import android.util.Base64;

import com.gamamobi.onestore.api.PurchaseClient.IapException;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA512withRSA";

    public Security() {
    }

    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) throws IapException {
        if (!TextUtils.isEmpty(base64PublicKey) && !TextUtils.isEmpty(signedData) && !TextUtils.isEmpty(signature)) {
            PublicKey key = generatePublicKey(base64PublicKey);
            return verify(key, signedData, signature);
        } else {
            return false;
        }
    }

    private static PublicKey generatePublicKey(String encodedPublicKey) throws IapException {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, 0);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException var3) {
            throw new SecurityException("RSA not available", var3);
        } catch (InvalidKeySpecException var4) {
            throw new IapException(IapResult.IAP_ERROR_SIGNATURE_NOT_VALIDATION);
        }
    }

    private static boolean verify(PublicKey publicKey, String signedData, String signature) {
        try {
            Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            return sig.verify(Base64.decode(signature, 0));
        } catch (InvalidKeyException | SignatureException | IllegalArgumentException | NoSuchAlgorithmException var4) {
            return false;
        }
    }
}
