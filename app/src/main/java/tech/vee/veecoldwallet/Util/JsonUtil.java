package tech.vee.veecoldwallet.Util;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import tech.vee.veecoldwallet.Activity.ColdWalletActivity;
import tech.vee.veecoldwallet.Activity.ConfirmTxActivity;
import tech.vee.veecoldwallet.Wallet.VEEAccount;
import tech.vee.veecoldwallet.Wallet.VEEWallet;

public class JsonUtil {
    private final static String TAG = "Winston";

    @NonNull
    public static void checkTransferTx(Activity activity, HashMap<String, Object> jsonMap,
                                       ArrayList<VEEAccount> accounts) {
        String senderPublicKey, recipient, attachment, assetId, feeAssetId;
        long amount, fee, timestamp;
        String[] keys = {"senderPublicKey", "recipient", "attachment",
                "assetId", "feeAssetId", "amount", "fee", "timestamp"};
        VEEAccount senderAcc = null;

        if (JsonUtil.containsKeys(jsonMap, keys)){
            senderPublicKey = (String) jsonMap.get("senderPublicKey");
            recipient = (String) jsonMap.get("recipient");
            attachment = (String) jsonMap.get("attachment");
            assetId = (String) jsonMap.get("assetId");
            feeAssetId = (String) jsonMap.get("feeAssetId");
            amount = Double.valueOf((double)jsonMap.get("amount")).longValue();
            fee = Double.valueOf((double)jsonMap.get("fee")).longValue();
            timestamp = Double.valueOf((double)jsonMap.get("timestamp")).longValue();

            for(VEEAccount account:accounts){
                if(account.isAccount(senderPublicKey)){
                    Log.d(TAG, "Private key: " + account.getPriKey());
                    senderAcc = account;
                }
            }

            if (senderAcc != null) {
                Gson gson = new Gson();
                VEEWallet wallet = ((ColdWalletActivity) activity).getWallet();
                String walletStr = gson.toJson(wallet);
                Intent intent = new Intent(activity, ConfirmTxActivity.class);
                intent.putExtra("ACTION", "TRANSFER");
                intent.putExtra("WALLET", walletStr);
                intent.putExtra("SENDER", gson.toJson(senderAcc));
                intent.putExtra("RECIPIENT", recipient);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("ASSET_ID", assetId);
                intent.putExtra("FEE", fee);
                intent.putExtra("FEE_ASSET_ID", feeAssetId);
                intent.putExtra("ATTACHMENT", attachment);
                intent.putExtra("TIMESTAMP", timestamp);

                activity.startActivity(intent);
        }
            else {
                UIUtil.createUpdateAppDialog(activity);
                //Toast.makeText(activity, "Wallet does not contain sender", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Private key cannot be found");
            }
        }
        else {
            UIUtil.createUpdateAppDialog(activity);
            //Toast.makeText(activity, "Invalid transaction format", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Map does not contain all keys");
        }
    }

    @NonNull
    public static void checkPaymentTx(Activity activity, HashMap<String, Object> jsonMap,
                                       ArrayList<VEEAccount> accounts) {
        String senderPublicKey, recipient, attachment, assetId, feeAssetId;
        long amount, fee, timestamp;
        short feeScale;
        String[] keys = {"senderPublicKey", "recipient", "amount", "fee", "feeScale", "timestamp"};
        VEEAccount senderAcc = null;

        if (JsonUtil.containsKeys(jsonMap, keys)){
            senderPublicKey = (String) jsonMap.get("senderPublicKey");
            recipient = (String) jsonMap.get("recipient");
            amount = Double.valueOf((double)jsonMap.get("amount")).longValue();
            attachment = (String) jsonMap.get("attachment");
            fee = Double.valueOf((double)jsonMap.get("fee")).longValue();
            feeScale = Double.valueOf((double)jsonMap.get("feeScale")).shortValue();
            timestamp = Double.valueOf((double)jsonMap.get("timestamp")).longValue();

            for(VEEAccount account:accounts){
                if(account.isAccount(senderPublicKey)){
                    Log.d(TAG, "Private key: " + account.getPriKey());
                    senderAcc = account;
                }
            }

            if (senderAcc != null) {
                Gson gson = new Gson();
                VEEWallet wallet = ((ColdWalletActivity) activity).getWallet();
                String walletStr = gson.toJson(wallet);
                Intent intent = new Intent(activity, ConfirmTxActivity.class);
                intent.putExtra("ACTION", "PAYMENT");
                intent.putExtra("WALLET", walletStr);
                intent.putExtra("SENDER", gson.toJson(senderAcc));
                intent.putExtra("RECIPIENT", recipient);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("FEE", fee);
                intent.putExtra("FEESCALE", feeScale);
                intent.putExtra("ATTACHMENT", attachment);
                intent.putExtra("TIMESTAMP", timestamp);

                activity.startActivity(intent);
            }
            else {
                UIUtil.createUpdateAppDialog(activity);
                //Toast.makeText(activity, "Wallet does not contain sender", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Private key cannot be found");
            }
        }
        else {
            UIUtil.createUpdateAppDialog(activity);
            //Toast.makeText(activity, "Invalid transaction format", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Map does not contain all keys");
        }
    }

    @NonNull
    public static void checkLeaseTx(Activity activity, HashMap<String, Object> jsonMap,
                                       ArrayList<VEEAccount> accounts) {
        String senderPublicKey, recipient;
        long amount, fee, timestamp;
        short feeScale;
        String[] keys = {"senderPublicKey", "recipient", "amount", "fee", "feeScale", "timestamp"};
        VEEAccount senderAcc = null;

        if (JsonUtil.containsKeys(jsonMap, keys)){
            senderPublicKey = (String) jsonMap.get("senderPublicKey");
            recipient = (String) jsonMap.get("recipient");
            amount = Double.valueOf((double)jsonMap.get("amount")).longValue();
            fee = Double.valueOf((double)jsonMap.get("fee")).longValue();
            feeScale = Double.valueOf((double)jsonMap.get("feeScale")).shortValue();
            timestamp = Double.valueOf((double)jsonMap.get("timestamp")).longValue();

            for(VEEAccount account:accounts){
                if(account.isAccount(senderPublicKey)){
                    Log.d(TAG, "Private key: " + account.getPriKey());
                    senderAcc = account;
                }
            }

            if (senderAcc != null) {
                Gson gson = new Gson();
                VEEWallet wallet = ((ColdWalletActivity) activity).getWallet();
                String walletStr = gson.toJson(wallet);
                Intent intent = new Intent(activity, ConfirmTxActivity.class);
                intent.putExtra("ACTION", "LEASE");
                intent.putExtra("WALLET", walletStr);
                intent.putExtra("SENDER", gson.toJson(senderAcc));
                intent.putExtra("RECIPIENT", recipient);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("FEE", fee);
                intent.putExtra("FEESCALE", feeScale);
                intent.putExtra("TIMESTAMP", timestamp);

                activity.startActivity(intent);
            }
            else {
                UIUtil.createUpdateAppDialog(activity);
                //Toast.makeText(activity, "Wallet does not contain sender", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Private key cannot be found");
            }
        }
        else {
            UIUtil.createUpdateAppDialog(activity);
            //Toast.makeText(activity, "Invalid transaction format", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Map does not contain all keys");
        }
    }

    @NonNull
    public static void checkCancelLeaseTx(Activity activity, HashMap<String, Object> jsonMap,
                                       ArrayList<VEEAccount> accounts) {
        String senderPublicKey, txId;
        long fee, timestamp;
        short feeScale;
        String[] keys = {"senderPublicKey", "txId", "fee", "feeScale", "timestamp"};
        VEEAccount senderAcc = null;

        if (JsonUtil.containsKeys(jsonMap, keys)){
            senderPublicKey = (String) jsonMap.get("senderPublicKey");
            txId = (String) jsonMap.get("txId");
            fee = Double.valueOf((double)jsonMap.get("fee")).longValue();
            feeScale = Double.valueOf((double)jsonMap.get("feeScale")).shortValue();
            timestamp = Double.valueOf((double)jsonMap.get("timestamp")).longValue();

            for(VEEAccount account:accounts){
                if(account.isAccount(senderPublicKey)){
                    Log.d(TAG, "Private key: " + account.getPriKey());
                    senderAcc = account;
                }
            }

            if (senderAcc != null) {
                Gson gson = new Gson();
                VEEWallet wallet = ((ColdWalletActivity) activity).getWallet();
                String walletStr = gson.toJson(wallet);
                Intent intent = new Intent(activity, ConfirmTxActivity.class);
                intent.putExtra("ACTION", "CANCEL_LEASE");
                intent.putExtra("WALLET", walletStr);
                intent.putExtra("SENDER", gson.toJson(senderAcc));
                intent.putExtra("TX_ID", txId);
                intent.putExtra("FEE", fee);
                intent.putExtra("FEESCALE", feeScale);
                intent.putExtra("TIMESTAMP", timestamp);

                activity.startActivity(intent);
            }
            else {
                UIUtil.createUpdateAppDialog(activity);
                //Toast.makeText(activity, "Wallet does not contain sender", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Private key cannot be found");
            }
        }
        else {
            UIUtil.createUpdateAppDialog(activity);
            //Toast.makeText(activity, "Invalid transaction format", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Map does not contain all keys");
        }
    }

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

    public static boolean isJsonString(String str){
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
