package tech.vee.veecoldwallet.Util;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wavesplatform.wavesj.Base64;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class JsonUtil {
    public final static String ERROR = "INVALID";

    private final static String TAG = "Winston";
    private final static Charset ENCODING = Charset.forName("UTF-8");
    private final static String KEYSALT = "0495c728-1614-41f6-8ac3-966c22b4a62d";
    private final static String AES = "AES";
    private final static String ALGORITHM = AES + "/ECB/PKCS5Padding";
    private final static String HASHING = "PBKDF2WithHmacSHA512";
    private final static int HASHINGITERATIONS = 999999;
    private final static int KEYLENGTH = 128;

    public static HashMap<String,Object> getJsonAsMap(String str){
        if (isJsonString(str)){
            try{
                HashMap<String,Object> gson = new Gson().fromJson(str, new TypeToken<HashMap<String, Object>>(){}.getType());
                Log.d(TAG, gson.toString());
                return gson;
            }
            catch(Exception e){
                return null;
            }
        }
        return null;
    }

    public static boolean containsKeys(HashMap<String,Object> jsonMap, String[] keys){
        for (String key:keys){
            if (!jsonMap.containsKey(key)) return false;
        }
        return true;
    }

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
            Log.d(TAG, "Error reading json file: " + e.getMessage());
        }
        finally
        {
            if (file != null) { file.close(); }
        }
    }

    public static String load(String path) {
        FileInputStream inputStream;
        String json;
        try {
            inputStream = new FileInputStream(path);
            //json = decrypt(key, IOUtils.toString(inputStream, ENCODING));
            json = IOUtils.toString(inputStream, ENCODING);
            inputStream.close();
            Log.d(TAG, "File loaded");
            if (isJsonString(json)) { return json; }
            else { return ERROR; }
        }
        catch(IOException e) {
            Log.d(TAG, "Error loading json file: " + e.getMessage());
            return "";
        }
    }

    private static boolean isJsonString(String str){
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static SecretKeySpec prepareKey(String key) {
        return new SecretKeySpec(hashPassword(key.toCharArray(), KEYSALT.getBytes(ENCODING),
                HASHINGITERATIONS, KEYLENGTH), AES);
    }

    private static byte[] hashPassword(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            SecretKeyFactory skf  = SecretKeyFactory.getInstance(HASHING);
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key  = skf.generateSecret(spec);
            return key.getEncoded();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            Log.d(TAG, "No such algorithm: ");
            return null;
        }
        catch (InvalidKeySpecException e){
            Log.d(TAG, "Invalid key specification: " + e.getCause());
            return null;
        }
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
            return cipher.doFinal(Base64.decode(encryptedValue)).toString();
        }
        catch(Exception e){
            Log.d(TAG, "Failed decryption");
            return "";
        }
    }
}
