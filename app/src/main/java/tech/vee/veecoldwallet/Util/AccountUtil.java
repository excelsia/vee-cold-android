package tech.vee.veecoldwallet.Util;

public class AccountUtil {
    public static String generatePubKeyAddrMsg (String domain, String address, String publicKey) {
        return domain + "/#cold/export?address=" + address + "&publicKey=" + publicKey;
    }
}
