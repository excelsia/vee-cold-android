package tech.vee.veecoldwallet.Wallet;

public class VEEChain {
    public static final byte TEST_NET = 'T';
    public static final byte MAIN_NET = 'M';

    public static byte getChainId(String chainIdString) {
        return "M".equals(chainIdString) ? VEEChain.MAIN_NET : VEEChain.TEST_NET;
    }
}
