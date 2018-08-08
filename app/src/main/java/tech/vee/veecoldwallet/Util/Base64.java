package tech.vee.veecoldwallet.Util;

public class Base64 {

    public static String encode(byte[] input) {
        return new String(android.util.Base64.encode(input, android.util.Base64.NO_WRAP));
    }

    public static byte[] decode(String input) {
        return android.util.Base64.decode(input, android.util.Base64.NO_WRAP);
    }
}