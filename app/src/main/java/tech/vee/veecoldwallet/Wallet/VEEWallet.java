package tech.vee.veecoldwallet.Wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tech.vee.veecoldwallet.Util.JsonUtil;

public class VEEWallet {
    private String seed;
    private List<String> accountSeeds;
    private int nonce;
    private String agent;

    private static final String WALLET_VERSION = "0.0.1";
    private static final String AGENT_VERSION = "0.0.1";
    private static final String AGENT_NAME = "VEE cold wallet";

    public VEEWallet(){
        seed = "";
        accountSeeds = new ArrayList<>();
        nonce = 0;
        agent = "VEE wallet:" + WALLET_VERSION + "/" + AGENT_NAME + ":" + AGENT_VERSION;
    }

    public VEEWallet(String seed, List<String> accountSeeds, int nonce){
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
            accountSeeds = (ArrayList<String>)jsonMap.get("accountSeeds");
            nonce = (int)jsonMap.get("nonce");
            agent = (String)jsonMap.get("agent");
        }
    }

    public String getSeed() { return seed; }
    public List<String> getAccountSeeds() { return accountSeeds; }
    public int getNonce() { return nonce;}
    public String getAgent() { return agent; }

    public static VEEWallet generate() {
        String newSeed = PrivateKeyAccount.generateSeed();
        return recover(newSeed);
    }

    public static VEEWallet recover(String seed){
        String newAccountSeed;
        List<String> newAccountSeeds = new ArrayList<>();
        int nonce = 0;

        newAccountSeed = nonce + seed;
        newAccountSeeds.add(newAccountSeed);
        nonce++;

        return new VEEWallet(seed, newAccountSeeds, nonce);
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
}