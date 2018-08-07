package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class FileUtil {
    private final static String TAG = "Winston";
    private final static String VEE_BACKUP_SDCARD_DIR = "VEEBackup";

    public static void save(String json, String path){
        File folder;
        PrintWriter file = null;

        try {
            folder = new File(path).getParentFile();

            if (folder != null && !folder.exists()) { folder.mkdirs(); }

            file = new PrintWriter(path);
            //file.write(encrypt(key, json));
            file.write(json);
            Log.d(TAG, "File saved");
        }
        catch(IOException e) {
            Log.d(TAG, "Error writing json file: " + e.getMessage());
        }
        finally
        {
            if (file != null) { file.close(); }
        }
    }

    public static boolean backupExists(Activity activity, String walletFileName) {
        String backupWalletFilePath = FileUtil.getBackupSdCardDir().getPath()
                + "/" + walletFileName;
        File backupWalletFile = new File(backupWalletFilePath);
        if (backupWalletFile.exists()) { return true; }
        return false;
    }

    public static void backup(Activity activity, VEEWallet wallet, String walletFileName) {
        String backupWalletFilePath;

        if(sdCardMountedExists()){
            backupWalletFilePath = FileUtil.getBackupSdCardDir().getPath()
                    + "/" + walletFileName;
            FileUtil.save(wallet.getJson(), backupWalletFilePath);
            Toast.makeText(activity, "Backup successful", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(activity, "No SD card mounted", Toast.LENGTH_LONG).show();
        }
    }

    public static void loadBackup(Activity activity, String savePath, String walletFileName) {
        FileInputStream inputStream;
        String json;
        String backupWalletFilePath = FileUtil.getBackupSdCardDir().getPath()
                + "/" + walletFileName;
        try {
            inputStream = new FileInputStream(backupWalletFilePath);
            //json = decrypt(key, IOUtils.toString(inputStream, ENCODING));
            json = IOUtils.toString(inputStream, JsonUtil.ENCODING);
            inputStream.close();
            Log.d(TAG, "File loaded");

            if (JsonUtil.isJsonString(json)) {
                Toast.makeText(activity, "Load backup file successful", Toast.LENGTH_LONG).show();
                save(json, savePath);
            }
            else {
                Toast.makeText(activity, "Load backup file unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
        catch(IOException e) {
            Log.d(TAG, "Error loading json file: " + e.getMessage());
        }
    }

    public static String load(String path) {
        FileInputStream inputStream;
        String json;
        try {
            inputStream = new FileInputStream(path);
            //json = decrypt(key, IOUtils.toString(inputStream, ENCODING));
            json = IOUtils.toString(inputStream, JsonUtil.ENCODING);
            inputStream.close();
            Log.d(TAG, "File loaded");
            if (JsonUtil.isJsonString(json)) { return json; }
            else { return JsonUtil.ERROR; }
        }
        catch(IOException e) {
            Log.d(TAG, "Error loading json file: " + e.getMessage());
            return "";
        }
    }

    public static boolean sdCardMountedExists() {
        String storageState = android.os.Environment.getExternalStorageState();

        if (isEmpty(storageState)) {
            return false;
        }
        return compareString(storageState,
                android.os.Environment.MEDIA_MOUNTED);
    }

    private static File getSDPath() {
        File sdDir = Environment.getExternalStorageDirectory();
        return sdDir;
    }

    private static File getBackupSdCardDir() {
        File backupDir = new File(getSDPath(), VEE_BACKUP_SDCARD_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        return backupDir;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    private static boolean compareString(String str, String other) {
        if (str == null) {
            return other == null;
        } else {
            return other != null && str.equals(other);
        }
    }
}
