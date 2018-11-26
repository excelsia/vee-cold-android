package tech.vee.veecoldwallet.Wallet;

public class VEEChain {
    public static final byte TEST_NET = 'T';
    public static final byte MAIN_NET = 'M';

    /**
     * translate chainId from string
     *
     * @param chainIdString "M" or "T"
     */
    public static byte getChainId(String chainIdString) {
        return chainIdString.getBytes()[0];
    }
}
