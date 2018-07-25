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
import java.util.List;
import java.util.Set;

import tech.vee.veecoldwallet.Util.HashUtil;
import tech.vee.veecoldwallet.Util.JsonUtil;

public class VEEWallet {
    private String seed;
    private Set<String> accountSeeds;
    private long nonce;
    private String agent;

    private static final String TAG = "Winston";
    private static final String WALLET_VERSION = "0.0.1";
    private static final String AGENT_VERSION = "0.0.1";
    private static final String AGENT_NAME = "VEE cold wallet";

    public VEEWallet(){
        seed = "";
        accountSeeds = new HashSet<>();
        nonce = 0;
        agent = "VEE wallet:" + WALLET_VERSION + "/" + AGENT_NAME + ":" + AGENT_VERSION;
    }

    public VEEWallet(String seed, Set<String> accountSeeds, long nonce){
        this.seed = seed;
        this.accountSeeds = accountSeeds;
        this.nonce = nonce;
        agent = "VEE wallet:" + WALLET_VERSION + "/" + AGENT_NAME + ":" + AGENT_VERSION;
    }
    public VEEWallet(String json){
        HashMap<String,Object> jsonMap = JsonUtil.getJsonAsMap(json);
        String[] keys = {"seed", "accountSeeds", "nonce", "agent"};

        if (JsonUtil.containsKeys(jsonMap, keys)) {
            seed = (String)jsonMap.get("seed");
            accountSeeds = (HashSet<String>)jsonMap.get("accountSeeds");
            nonce = (int)jsonMap.get("nonce");
            agent = (String)jsonMap.get("agent");
        }
    }

    public String getSeed() { return seed; }
    public Set<String> getAccountSeeds() { return accountSeeds; }
    public long getNonce() { return nonce;}
    public String getAgent() { return agent; }

    public static VEEWallet generate() {
        String newSeed = PrivateKeyAccount.generateSeed();
        return recover(newSeed, 1);
    }

    public static VEEWallet generate(int nonce) {
        String newSeed = PrivateKeyAccount.generateSeed();
        return recover(newSeed, nonce);
    }

    public static VEEWallet recover(String seed, int num){
        String newAccountSeed;
        Set<String> newAccountSeeds = new HashSet<>();

        if (seed != null && num > 0) {
            for(int i = 0; i < num; i++) {
                newAccountSeed = generateAccountSeed(seed, i);
                newAccountSeeds.add(newAccountSeed);
            }
            return new VEEWallet(seed, newAccountSeeds, num);
        }
        Log.d(TAG, "Invalid recover");
        return null;
    }

    public static VEEWallet append(VEEWallet wallet, long num){
        String seed;
        String newAccountSeed;
        Set<String> newAccountSeeds;
        long nonce;

        if (wallet != null && num > 0) {
            seed = wallet.getSeed();
            newAccountSeeds = wallet.getAccountSeeds();
            nonce = wallet.getNonce();

            for (long i = nonce; i < nonce + num; i++) {
                newAccountSeed = generateAccountSeed(seed, i);
                newAccountSeeds.add(newAccountSeed);
            }
            return new VEEWallet(seed, newAccountSeeds, nonce + num);
        }
        else {
            Log.d(TAG,"Invalid append");
            return  null;
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

    private static String generateAccountSeed(String seed, long nonce) {
        // account seed from seed & nonce
        ByteBuffer buf = ByteBuffer.allocate(seed.getBytes().length + 8);
        buf.putLong(nonce).put(seed.getBytes());
        byte[] accountSeed = HashUtil.secureHash(buf.array(), 0, buf.array().length);
        return Base58.encode(accountSeed);
    }
}