package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

import tech.vee.veecoldwallet.Activity.ScannerActivity;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class QRCodeUtil {
    private static final String TAG = "Winston";
    //private static final String DOMAIN = "https://vee.tech";
    private static final String DOMAIN = "http://localhost:8080";

    public static final String OP_CODE = "transaction";

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

        String OP_CODE = "account";
        HashMap<String, Object> accountJson = new HashMap<>();

        accountJson.put("protocol", VEEWallet.PROTOCOL);
        accountJson.put("api", VEEWallet.API_VERSION);
        accountJson.put("opc", OP_CODE);
        accountJson.put("address",account.getAddress());
        accountJson.put("publicKey",account.getPubKey());

        try {
            return new ObjectMapper().writeValueAsString(accountJson);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    public static String generateSeedStr(VEEWallet wallet) {
        String OP_CODE = "seed";
        HashMap<String,Object>seedJson = new HashMap<>();

        seedJson.put("protocol",VEEWallet.PROTOCOL);
        seedJson.put("api",VEEWallet.API_VERSION);
        seedJson.put("opc",OP_CODE);
        seedJson.put("seed",wallet.getSeed());

        try {
            return new ObjectMapper().writeValueAsString(seedJson);
        } catch (JsonProcessingException e) {
            //not expected to ever happen
            return null;
        }

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
            return "";
        }
    }

    public static int processQrContents(String qrContents) {
        HashMap<String,Object> map = new HashMap<>();
        String priKey;

        if (qrContents == null) return 0;

        map = JsonUtil.getJsonAsMap(qrContents);
        if (map != null) {
            if (map.containsKey("transactionType"))
            {
                return 1;
            }
            else return 9;
        }

        /* priKey = QRCodeUtil.parseSeed(qrContents);
        if (priKey != "") return 2;
        */
        else return 3;
    }

    public static void scan(Activity activity){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setCaptureActivity(ScannerActivity.class);
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }
}
