package tech.vee.veecoldwallet.Wallet;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.whispersystems.curve25519.Curve25519;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.vee.veecoldwallet.Util.Base58;
import tech.vee.veecoldwallet.Util.HashUtil;

@JsonDeserialize(using = VEETransaction.Deserializer.class)
public class VEETransaction {
    public static final String WAVES = "WAVES";
    public static final String TAG = "Winston";

    private final static Charset UTF8 = Charset.forName("UTF-8");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};

    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private static final int KBYTE = 1024;
    private static final byte V2 = 2;

    private static final byte PAYMENT = 2;
    private static final byte TRANSFER = 12;
    private static final byte LEASE = 3;
    private static final byte LEASE_CANCEL = 4;

    /** VEETransaction ID. */
    public final String id;
    /** VEETransaction data. */
    public final Map<String, Object> data;
    /**
     * List of proofs. Each proof is a Base58-encoded byte array of at most 64 bytes.
     * There's currently a limit of 8 proofs per transaction.
     */
    public final List<String> proofs;
    final String endpoint;
    final byte[] bytes;

    private VEETransaction(VEEAccount signer, ByteBuffer buffer, String endpoint, Object... items) {
        this.bytes = toBytes(buffer);
        this.id = hash(bytes);
        this.endpoint = endpoint;
        this.proofs = Collections.singletonList(sign(signer, bytes));

        HashMap<String, Object> map = new HashMap<String, Object>();
        for (int i=0; i<items.length; i+=2) {
            Object value = items[i+1];
            if (value != null) {
                map.put((String) items[i], value);
            }
        }
        this.data = Collections.unmodifiableMap(map);
    }

    private VEETransaction(Map<String, Object> data) {
        this.data = Collections.unmodifiableMap(data);
        this.id = (String) data.get("id");
        this.proofs = (List<String>) data.get("proofs");
        this.endpoint = null;
        this.bytes = null;
    }

    @NonNull
    public static VEETransaction makePaymentTx(VEEAccount sender, String recipient, long amount,
                                                long fee, short feeScale, String attachment, BigInteger timestamp)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(PAYMENT).put(Base58.decode(sender.getPubKey()));
        putBigInteger(buf, timestamp);
        buf.putLong(amount).putLong(fee);
        buf.putShort(feeScale);
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment);

        return new VEETransaction(sender, buf,"/transactions/broadcast",
                "type", PAYMENT,
                "version", V2,
                "senderPublicKey", sender.getPubKey(),
                "recipient", recipient,
                "amount", amount,
                "fee", fee,
                "feeScale", feeScale,
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    @NonNull
    public static VEETransaction makeTransferTx(VEEAccount sender, String recipient, long amount, String assetId,
                                                long fee, String feeAssetId, String attachment, BigInteger timestamp)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(TRANSFER).put(Base58.decode(sender.getPubKey()));
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        putBigInteger(buf, timestamp);
        buf.putLong(amount).putLong(fee);
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment);

        return new VEETransaction(sender, buf,"/transactions/broadcast",
                "type", TRANSFER,
                "version", V2,
                "senderPublicKey", sender.getPubKey(),
                "recipient", recipient,
                "amount", amount,
                "assetId", VEEAsset.toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", VEEAsset.toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
    }

    @NonNull
    public static VEETransaction makeLeaseTx(VEEAccount sender, String recipient, long amount, long fee, short feeScale, BigInteger timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(LEASE).put(Base58.decode(sender.getPubKey()));
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        buf.putLong(amount).putLong(fee);
        buf.putShort(feeScale);
        putBigInteger(buf, timestamp);
        return new VEETransaction(sender, buf,"/transactions/broadcast",
                "type", LEASE,
                "version", V2,
                "senderPublicKey", sender.getPubKey(),
                "recipient", recipient,
                "amount", amount,
                "fee", fee,
                "feeScale", feeScale,
                "timestamp", timestamp);
    }


    public static VEETransaction makeLeaseCancelTx(VEEAccount sender, String txId, long fee, short feeScale, BigInteger timestamp) {
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(LEASE_CANCEL).put(Base58.decode(sender.getPubKey())).putLong(fee);
        buf.putShort(feeScale);
        putBigInteger(buf, timestamp);
        buf.put(Base58.decode(txId));
        return new VEETransaction(sender, buf,"/transactions/broadcast",
                "type", LEASE_CANCEL,
                "version", V2,
                "senderPublicKey", sender.getPubKey(),
                "txId", txId,
                "fee", fee,
                "feeScale", feeScale,
                "timestamp", timestamp);
    }

    static class Deserializer extends JsonDeserializer<VEETransaction> {
        @Override
        public VEETransaction deserialize(JsonParser p, DeserializationContext context) throws IOException {
            Map<String, Object> data = mapper.convertValue(p.getCodec().readTree(p), TX_INFO);
            return new VEETransaction(data);
        }
    }

    public byte[] getBytes() {
        return bytes.clone();
    }

    /**
     * Returns JSON-encoded transaction data containing the timestamp and signature.
     * @return a JSON string
     */
    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<>();

        if (proofs.size() == 1) {
            // assume proof0 is a signature
            toJson.put("signature", proofs.get(0));
        }

        try {
            return new ObjectMapper().writeValueAsString(toJson);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    /**
     * Returns JSON-encoded transaction data.
     * @return a JSON string
     */
    public String getFullJson() {
        HashMap<String, Object> toJson = new HashMap<String, Object>(data);
        toJson.put("id", id);
        toJson.put("proofs", proofs);
        if (proofs.size() == 1) {
            // assume proof0 is a signature
            toJson.put("signature", proofs.get(0));
        }
        try {
            return new ObjectMapper().writeValueAsString(toJson);
        } catch (JsonProcessingException e) {
            // not expected to ever happen
            return null;
        }
    }

    @NonNull
    private String sign(VEEAccount account, byte[] bytes){
        return Base58.encode(cipher.calculateSignature(Base58.decode(account.getPriKey()), bytes));
    }
    private static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    @NonNull
    private static String hash(byte[] bytes) {
        return Base58.encode(HashUtil.hash(bytes, 0, bytes.length, HashUtil.BLAKE2B256));
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (VEEAsset.isVEE(assetId)) {
            buffer.put((byte) 0);
        } else {
            buffer.put((byte) 1).put(Base58.decode(assetId));
        }
    }

    private static void putString(ByteBuffer buffer, String s) {
        if (s == null) s = "";
        putBytes(buffer, s.getBytes(UTF8));
    }

    private static void putBigInteger(ByteBuffer buffer, BigInteger b) {
        if (b == null) b = BigInteger.ZERO;
        buffer.put(b.toByteArray());
    }

    private static void putBytes(ByteBuffer buffer, byte[] bytes) {
        buffer.putShort((short) bytes.length).put(bytes);
    }

    private static String putRecipient(ByteBuffer buffer, byte chainId, String recipient) {

        if (recipient.length() <= 30) {
            // assume an alias
            buffer.put((byte) 0x02).put(chainId).putShort((short) recipient.length()).put(recipient.getBytes(UTF8));
            return String.format("alias:%c:%s", chainId, recipient);
        }
        else {
            buffer.put(Base58.decode(recipient));
            return recipient;
        }
    }
}
