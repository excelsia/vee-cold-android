package tech.vee.veecoldwallet.Account;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.wavesj.Base58;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.PublicKeyAccount;

import org.whispersystems.curve25519.Curve25519;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonDeserialize(using = VEETransaction.Deserializer.class)
public class VEETransaction {
    public static final String WAVES = "WAVES";

    private final static Charset UTF8 = Charset.forName("UTF-8");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> TX_INFO = new TypeReference<Map<String, Object>>() {};

    private static final Curve25519 cipher = Curve25519.getInstance(Curve25519.BEST);

    private static final int KBYTE = 1024;
    private static final byte TRANSFER = 4;
    private static final byte V2 = 2;
    private static ByteBuffer buffer;
    private static byte chainId;
    private static String recipient;

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

    private VEETransaction(PublicKeyAccount signer, ByteBuffer buffer, String endpoint, Object... items) {
        this.bytes = toBytes(buffer);
        this.id = hash(bytes);
        this.endpoint = endpoint;

        if (signer instanceof PrivateKeyAccount) {
            this.proofs = Collections.singletonList(sign((PrivateKeyAccount)signer, bytes));
        } else {
            this.proofs = Collections.emptyList();
        }

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
    public static VEETransaction makeTransferTx(PublicKeyAccount sender, String recipient, long amount, String assetId,
                                                long fee, String feeAssetId, String attachment, BigInteger timestamp)
    {
        byte[] attachmentBytes = (attachment == null ? "" : attachment).getBytes();
        ByteBuffer buf = ByteBuffer.allocate(KBYTE);
        buf.put(TRANSFER).put(sender.getPublicKey());
        putAsset(buf, assetId);
        putAsset(buf, feeAssetId);
        putBigInteger(buf, timestamp);
        buf.putLong(amount).putLong(fee);
        recipient = putRecipient(buf, sender.getChainId(), recipient);
        putString(buf, attachment);

        return new VEETransaction(sender, buf,"/transactions/broadcast",
                "type", TRANSFER,
                "version", V2,
                "senderPublicKey", Base58.encode(sender.getPublicKey()),
                "recipient", recipient,
                "amount", amount,
                "assetId", toJsonObject(assetId),
                "fee", fee,
                "feeAssetId", toJsonObject(feeAssetId),
                "timestamp", timestamp,
                "attachment", Base58.encode(attachmentBytes));
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
     * Returns JSON-encoded transaction data.
     * @return a JSON string
     */
    public String getJson() {
        HashMap<String, Object> toJson = new HashMap<>();

        if (data.containsKey("timestamp")){
            toJson.put("timestamp", data.get("timestamp"));
        }

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
    private String sign(PrivateKeyAccount account, byte[] bytes){
        return Base58.encode(cipher.calculateSignature(account.getPrivateKey(), bytes));
    }
    private static byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.position()];
        buffer.position(0);
        buffer.get(bytes);
        return bytes;
    }

    @NonNull
    private static String hash(byte[] bytes) {
        return Base58.encode(Hash.hash(bytes, 0, bytes.length, Hash.BLAKE2B256));
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private static String normalize(String assetId) {
        return assetId == null || assetId.isEmpty() ? Asset.WAVES : assetId;
    }

    private static boolean isWaves(String assetId) {
        return WAVES.equals(normalize(assetId));
    }

    @Nullable
    private static String toJsonObject(String assetId) {
        return isWaves(assetId) ? null : assetId;
    }

    private static void putAsset(ByteBuffer buffer, String assetId) {
        if (isWaves(assetId)) {
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
        } else {
            buffer.put(Base58.decode(recipient));
            return recipient;
        }
    }
}
