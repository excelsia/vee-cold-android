package tech.vee.veecoldwallet.Util;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

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

    public static String generateSeedStr(VEEWallet wallet) {
        return DOMAIN + "/#cold/export?seed=" + wallet.getSeed();
    }

    public static Bitmap exportPubKeyAddr(VEEAccount account, int width){
        String message;
        message = generatePubKeyAddrStr(account);
        return generateQRCode(message, width);
    }

    public static Bitmap exportSeed(VEEWallet wallet, int width){
        String message;
        message = generateSeedStr(wallet);
        return generateQRCode(message, width);
    }

    public static String parseSeed(String message) {
        if(message.contains("/#cold/export?seed=")) {
            String[] tokens = message.split("=");
            return tokens[1];
        }
        else {
            Log.d(TAG, "Format incorrect!");
            return "";
        }
    }

    public static int processQrContents(String qrContents) {
        HashMap<String,Object> map = new HashMap<>();
        String priKey;

        if (qrContents == null) return 0;

        map = JsonUtil.getJsonAsMap(qrContents);
        if (map != null) return 1;

        priKey = QRCodeUtil.parseSeed(qrContents);
        if (priKey != "") return 2;

        return 3;
    }
}
