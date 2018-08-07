package tech.vee.veecoldwallet.Wallet;

import android.util.Log;

import org.whispersystems.curve25519.java.curve_sigs;

import java.nio.ByteBuffer;
import java.util.Arrays;

import tech.vee.veecoldwallet.Util.HashUtil;
import tech.vee.veecoldwallet.Util.Base58;

public class VEEAccount {
    String accountSeed;
    long nonce;
    byte chainId;
    String priKey;
    String pubKey;
    String address;
    String accountName;

    private static final String TAG = "Winston";
    private static final byte ADDR_VERSION = 5;

    // Create new account using valid seed phrase
    public VEEAccount(String accountSeed, long nonce, byte chainId) {
        this.accountSeed = accountSeed;
        this.nonce = nonce;
        this.chainId = chainId;
        byte[] privateKey = generatePriKey(accountSeed);
        priKey = Base58.encode(privateKey);

        byte[] publicKey;
        publicKey = generatePubKey(privateKey);
        pubKey = Base58.encode(publicKey);

        address = Base58.encode(generateAddress(publicKey, chainId));

        accountName = "Account " + (nonce + 1);
    }

    public String getAccountSeed() { return accountSeed; }
    public long getNonce() { return nonce; }
    public byte getChainId() { return chainId; }
    public String getPriKey() {
        return priKey;
    }
    public String getPubKey() {
        return pubKey;
    }
    public String getAddress() {
        return address;
    }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public boolean isAccount(String pubKey){
        if (getPubKey().equals(pubKey)) return true;
        return false;
    }

    public String getMutatedAddress() {
        String start, middle, end;
        int len = address.length();

        if(len > 6) {
            start = address.substring(0, 6);
            middle = "******";
            end = address.substring(len - 6, len);
            return start + middle + end;
        }
        else{
            Log.d(TAG, "Address is incorrect length");
            return "";
        }
    }

    public static String getMutatedAddress(String address) {
        String start, middle, end;
        int len = address.length();

        if(len > 6) {
            start = address.substring(0, 6);
            middle = "******";
            end = address.substring(len - 6, len);
            return start + middle + end;
        }
        else{
            Log.d(TAG, "Address is incorrect length");
            return "";
        }
    }

    @Override
    public String toString(){
        return "Nonce: " + nonce + "\nAccount seed: " + accountSeed + "\nPrivate Key: "
                + priKey + "\nPublic Key: " + pubKey + "\nAddress: " + address;
    }

    private static byte[] generatePriKey(String accountSeed) {
        // private key from account seed
        byte[] accountSeedByte = Base58.decode(accountSeed);
        byte[] hashedSeed = HashUtil.hash(accountSeedByte, 0, accountSeedByte.length, HashUtil.SHA256);
        byte[] privateKey = Arrays.copyOf(hashedSeed, 32);
        privateKey[0]  &= 248;
        privateKey[31] &= 127;
        privateKey[31] |= 64;

        return privateKey;
    }

    private static byte[] generatePubKey(byte[] privateKey) {
        byte[] publicKey = new byte[32];
        curve_sigs.curve25519_keygen(publicKey, privateKey);
        return publicKey;
    }

    private static byte[] generateAddress(byte[] publicKey, byte chainId) {
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = HashUtil.secureHash(publicKey, 0, publicKey.length);
        buf.put(ADDR_VERSION).put(chainId).put(hash, 0, 20);
        byte[] checksum = HashUtil.secureHash(buf.array(), 0, 22);
        buf.put(checksum, 0, 4);
        return buf.array();
    }
}
