package tech.vee.veecoldwallet.Util;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeUtil {
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
}
