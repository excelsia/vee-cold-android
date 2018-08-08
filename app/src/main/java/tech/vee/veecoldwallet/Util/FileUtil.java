package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class FileUtil {
    private final static String TAG = "Winston";

    private final static Charset ENCODING = Charset.forName("UTF-8");

    public final static String ERROR = "INVALID";

    private final static String VEE_BACKUP_SDCARD_DIR = "VEEBackup";
    private final static String KEYSALT = "0ba950e1-828b-4bae-9e06-faf078eb33ec";
    private final static String AES = "AES";
    private final static String ALGORITHM = AES + "/ECB/PKCS5Padding";
    private final static int HASHINGITERATIONS = 999999;
    private final static int KEYLENGTH = 256;

    //Save with password
    public static void save(String json, String password, String path){
        File folder;
        PrintWriter file = null;

        try {
            folder = new File(path).getParentFile();

            if (folder != null && !folder.exists()) { folder.mkdirs(); }

            file = new PrintWriter(path);
            file.write(encrypt(prepareKey(password), json));
            //file.write(json);
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

    //Save and backup
    public static void save(Activity activity, String json, String password, String path, String walletFileName){
        File folder;
        PrintWriter file = null;
        String message = "";

        try {
            folder = new File(path).getParentFile();

            if (folder != null && !folder.exists()) { folder.mkdirs(); }

            file = new PrintWriter(path);
            message = encrypt(prepareKey(password), json);
            file.write(message);
            //file.write(json);
            Log.d(TAG, "File saved");

            String backupWalletFilePath = FileUtil.getBackupSdCardDir().getPath()
                    + "/" + walletFileName;
            FileUtil.save(message, backupWalletFilePath);
            Toast.makeText(activity, "Backup successful", Toast.LENGTH_LONG).show();

        }
        catch(IOException e) {
            Log.d(TAG, "Error writing json file: " + e.getMessage());
        }
        finally
        {
            if (file != null) { file.close(); }
        }
    }

    // Save without password
    public static void save(String json, String path){
        File folder;
        PrintWriter file = null;

        try {
            folder = new File(path).getParentFile();

            if (folder != null && !folder.exists()) { folder.mkdirs(); }

            file = new PrintWriter(path);
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

    public static void backup(Activity activity, VEEWallet wallet, String password, String walletFileName) {
        String backupWalletFilePath;

        if(sdCardMountedExists()){
            backupWalletFilePath = FileUtil.getBackupSdCardDir().getPath()
                    + "/" + walletFileName;
            FileUtil.save(wallet.getJson(), password, backupWalletFilePath);
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
            json = IOUtils.toString(inputStream, ENCODING);
            inputStream.close();
            //Toast.makeText(activity, "Load backup file successful", Toast.LENGTH_LONG).show();
            Log.d(TAG, "File loaded");

            save(json, savePath);
            Intent intent = new Intent(activity, ColdWalletActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        }
        catch(IOException e) {
            Toast.makeText(activity, "Load backup file unsuccessful", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error loading json file: " + e.getMessage());
        }
    }

    public static String load(String password, String path) {
        FileInputStream inputStream;
        String json;
        try {
            inputStream = new FileInputStream(path);
            json = decrypt(prepareKey(password), IOUtils.toString(inputStream, ENCODING));
            //json = IOUtils.toString(inputStream, ENCODING);
            inputStream.close();
            Log.d(TAG, "Loaded string: " + json);
            Log.d(TAG, "File loaded");
            if (JsonUtil.isJsonString(json)) { return json; }
            else { return ERROR; }
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

    public static SecretKeySpec prepareKey(String key) {
        return new SecretKeySpec(hashPassword(key.getBytes(ENCODING), KEYSALT.getBytes(ENCODING),
                HASHINGITERATIONS, KEYLENGTH), AES);
    }

    private static byte[] hashPassword(byte[] password, byte[] salt, int iterations, int keyLength) {
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(password, salt, iterations);
        byte[] derivedKey = ((KeyParameter) gen.generateDerivedParameters(keyLength)).getKey();

        return derivedKey;
    }

    private static String encrypt(SecretKeySpec key, String value) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encode(cipher.doFinal(value.getBytes(ENCODING)));
        }
        catch(Exception e){
            Log.d(TAG, "Failed encryption");
            return "";

        }
    }

    private static String decrypt(SecretKeySpec key, String encryptedValue) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decode(encryptedValue)));
        }
        catch(Exception e){
            Log.d(TAG, "Failed decryption: " + e.getMessage());
            return "";
        }
    }
}
