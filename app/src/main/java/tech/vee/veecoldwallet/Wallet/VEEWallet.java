package tech.vee.veecoldwallet.Wallet;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import tech.vee.veecoldwallet.Util.HashUtil;
import tech.vee.veecoldwallet.Util.JsonUtil;

public class VEEWallet {
    private String seed;
    private List<String> accountSeeds;
    private long nonce;
    private String agent;

    private static final String TAG = "Winston";
    private static final String WALLET_VERSION = "1.0rc1";
    private static final String AGENT_VERSION = "0.0.1";
    private static final String AGENT_NAME = "VEE cold wallet";
    private static final byte CHAIN_ID = 0;

    public VEEWallet(){
        seed = "";
        accountSeeds = new ArrayList<>();
        nonce = 0;
        agent = "VEE wallet:" + WALLET_VERSION + "/" + AGENT_NAME + ":" + AGENT_VERSION;
    }

    public VEEWallet(String seed, List<String> accountSeeds, long nonce){
        this.seed = seed;
        this.accountSeeds = accountSeeds;
        this.nonce = nonce;
        agent = "VEE wallet:" + WALLET_VERSION + "/" + AGENT_NAME + ":" + AGENT_VERSION;
    }
    public VEEWallet(String json){
        HashMap<String,Object> jsonMap = JsonUtil.getJsonAsMap(json);
        String[] keys = {"seed", "accountSeeds", "nonce", "agent"};

        if (JsonUtil.containsKeys(jsonMap, keys)) {
            seed = (String) jsonMap.get("seed");
            accountSeeds = (ArrayList<String>) jsonMap.get("accountSeeds");
            nonce = Double.valueOf((double) jsonMap.get("nonce")).longValue();
            agent = (String) jsonMap.get("agent");
        }
    }

    public String getSeed() { return seed; }
    public List<String> getAccountSeeds() { return accountSeeds; }
    public long getNonce() { return nonce;}
    public String getAgent() { return agent; }

    public static VEEWallet generate() {
        String newSeed = PrivateKeyAccount.generateSeed();
        return recover(newSeed, 1);
    }

    public static VEEWallet generate(long nonce) {
        String newSeed = PrivateKeyAccount.generateSeed();
        return recover(newSeed, nonce);
    }

    public static VEEWallet recover(String seed, long num){
        String accountSeed;
        List<String> newAccountSeeds = new ArrayList<>();

        if (seed != null && num > 0) {
            for(long i = 0; i < num; i++) {
                accountSeed = generateAccountSeed(seed, i);
                //accountSeed = generateAccountSeedOld(seed, i);
                newAccountSeeds.add(accountSeed);
            }
            return new VEEWallet(seed, newAccountSeeds, num);
        }
        Log.d(TAG, "Invalid recover");
        return null;
    }

    public void append(long num){
        String accountSeed;

        if (num > 0) {
            for (long i = nonce; i < nonce + num; i++) {
                accountSeed = generateAccountSeed(seed, i);
                //accountSeed = generateAccountSeedOld(seed, i);
                accountSeeds.add(accountSeed);
            }
        }
        else {
            Log.d(TAG,"Invalid append");
        }
    }

    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<String, Object>();
        toJson.put("seed", seed);
        toJson.put("accountSeeds", accountSeeds);
        toJson.put("nonce", nonce);
        toJson.put("agent", agent);

        try {
            return new ObjectMapper().writeValueAsString(toJson);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    public ArrayList<VEEAccount> generateAccounts() {
        ArrayList<VEEAccount> accounts = new ArrayList<>();
        VEEAccount account;

        for(long i = 0; i < accountSeeds.size(); i++){
            account = new VEEAccount(accountSeeds.get((int) i), i, CHAIN_ID);
            Log.d(TAG, account.toString());
            accounts.add(account);
        }
        return accounts;
    }

    private static String generateAccountSeed(String seed, long nonce) {
        // account seed from seed & nonce
        String noncedSecret = String.valueOf(nonce) + seed;
        ByteBuffer buf = ByteBuffer.allocate(noncedSecret.getBytes().length);
        buf.put(noncedSecret.getBytes());
        byte[] accountSeed = HashUtil.secureHash(buf.array(), 0, buf.array().length);
        return Base58.encode(accountSeed);
    }

    private static String generateAccountSeedOld(String seed, long nonce) {
        // account seed from seed & nonce
        int num = (int) nonce;
        ByteBuffer buf = ByteBuffer.allocate(seed.getBytes().length + 4);
        buf.putInt(num).put(seed.getBytes());
        byte[] accountSeed = HashUtil.secureHash(buf.array(), 0, buf.array().length);
        return Base58.encode(accountSeed);
    }
}