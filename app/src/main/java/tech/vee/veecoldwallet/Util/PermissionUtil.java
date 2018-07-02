package tech.vee.veecoldwallet.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PermissionUtil {
    private static String tag = "Winston";
    public static boolean permissionGranted(Activity activity){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return false;
        else return true;
    }

    public static void checkPermissions(Activity activity){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (permissionGranted(activity)) Log.d(tag, "Permission granted!");
        if (!permissionGranted(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.CAMERA}, 0000);
        }

    }
}
