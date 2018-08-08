package tech.vee.veecoldwallet.Util;

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

    public static boolean BluetoothIsConnected() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            int state = adapter.getState();
            return state == BluetoothAdapter.STATE_ON;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isConnected() {
        // BluetoothDevice.ACTION_ACL_CONNECTED;
        // The base Context in the ContextWrapper has not been set yet, which is
        // causing the NullPointerException
        try {
            ConnectivityManager ConnectivityManager = (ConnectivityManager) BitherApplication
                    .mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = ConnectivityManager.getActiveNetworkInfo();
            if (netinfo == null) {
                return false;
            } else {
                return netinfo.isAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * judge whether the network connection
     */
    public static NetworkType isConnectedType() {
        try {
            ConnectivityManager mConnectivity = (ConnectivityManager) BitherApplication.mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            for (NetworkInfo networkInfo : mConnectivity.getAllNetworkInfo()) {
                //LogUtil.d("network",networkInfo.getTypeName()+":"+networkInfo.getType()+",
                // "+networkInfo.isConnected());
                if (networkInfo.isConnectedOrConnecting()) {
                    return getNetworkType(networkInfo);
                }
            }
            return NetworkType.NoConnect;
        } catch (Exception e) {
            LogUtil.w("Exception", e.getMessage() + "\n" + e.getStackTrace());
            return NetworkType.NoConnect;
        }

    }

    //TODO Determine the unknown network
    private static NetworkType getNetworkType(NetworkInfo info) {
        if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() ==
                ConnectivityManager.TYPE_ETHERNET) {
            return NetworkType.Wifi;
        } else {
            return NetworkType.Mobile;
        }
    }
}
