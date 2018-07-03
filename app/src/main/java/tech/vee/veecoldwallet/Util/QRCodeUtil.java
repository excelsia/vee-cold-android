package tech.vee.veecoldwallet.Util;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import tech.vee.veecoldwallet.Account.VEEAccount;

public class QRCodeUtil {
    private static final String TAG = "Winston";
    //private static final String DOMAIN = "https://vee.tech";
    private static final String DOMAIN = "http://localhost:8080";
    public static Bitmap generateQRCode(String message, int width) {
        Bitmap qrCode;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrCode = barcodeEncoder.encodeBitmap(message, BarcodeFormat.QR_CODE, width, width);
        }
        catch(Exception e){
            qrCode = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        }
        return qrCode;
    }

    public static String generatePubKeyAddrStr(VEEAccount account) {
        return DOMAIN + "/#cold/export?address=" + account.getAddress() + "&publicKey=" + account.getPubKey();
    }

    public static String generatePriKeyStr(VEEAccount account) {
        return DOMAIN + "/#cold/export?privateKey=" + account.getPriKey();
    }

    public static Bitmap exportPubKeyAddr(VEEAccount account, int width){
        String message;
        message = generatePubKeyAddrStr(account);
        return generateQRCode(message, width);
    }

    public static Bitmap exportPriKey(VEEAccount account, int width){
        String message;
        message = generatePriKeyStr(account);
        return generateQRCode(message, width);
    }

    public static String parsePriKey(String message) {
        if(message.contains("/#cold/export?privateKey=")) {
            String[] tokens = message.split("=");
            return tokens[1];
        }
        else {
            Log.d(TAG, "Format incorrect!");
            return "";
        }

    }
}
