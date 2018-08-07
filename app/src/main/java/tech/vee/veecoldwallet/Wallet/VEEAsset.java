package tech.vee.veecoldwallet.Wallet;

import com.wavesplatform.wavesj.Asset;

public abstract class VEEAsset {
    /**
     * Constant used to represent VEE token in asset transactions.
     */
    public static final String VEE = "VEE";

    static String normalize(String assetId) {
        return assetId == null || assetId.isEmpty() ? VEEAsset.VEE : assetId;
    }

    static boolean isVEE(String assetId) {
        return VEE.equals(normalize(assetId));
    }

    static String toJsonObject(String assetId) {
        return isVEE(assetId) ? null : assetId;
    }
}
