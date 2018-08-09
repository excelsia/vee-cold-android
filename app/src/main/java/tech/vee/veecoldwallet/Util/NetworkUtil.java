package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private NetworkUtil() {

    }

    public enum NetworkType {
        Wifi, Mobile, NoConnect,
    }

    public static boolean bluetoothIsConnected() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            int state = adapter.getState();
            return state == BluetoothAdapter.STATE_ON;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isConnected(Activity activity) {
        try {
            ConnectivityManager ConnectivityManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = ConnectivityManager.getActiveNetworkInfo();

            if (netInfo == null) {
                return false;
            } else {
                return netInfo.isAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * judge whether the network connection
     */
    public static NetworkType isConnectedType(Activity activity) {
        try {
            ConnectivityManager mConnectivity = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            for (NetworkInfo networkInfo : mConnectivity.getAllNetworkInfo()) {
                if (networkInfo.isConnectedOrConnecting()) {
                    return getNetworkType(networkInfo);
                }
            }
            return NetworkType.NoConnect;
        } catch (Exception e) {
            return NetworkType.NoConnect;
        }

    }

    private static NetworkType getNetworkType(NetworkInfo info) {
        if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() ==
                ConnectivityManager.TYPE_ETHERNET) {
            return NetworkType.Wifi;
        } else {
            return NetworkType.Mobile;
        }
    }
}
