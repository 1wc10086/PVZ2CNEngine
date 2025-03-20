
import java.io.StringReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.engines.RijndaelEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.paddings.ZeroBytePadding;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.InvalidCipherTextException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import org.spongycastle.util.Arrays;

public class NetCryptoCall{
    private static final String BOUNDARY = "--_{{}}_";
    private static final String LINE_SEP = System.lineSeparator();

    public static String detectDecryptDataType(String input) throws Exception {
        return input.contains("form-data") ? formatTextToJson(HTTPRequestDecryptor(input)) :
            (input.contains("\"i\":") && input.contains("\"r\":") && input.contains("\"e\":") ? formatTextToJson(HTTPResponseDecryptor(input)) :
            input);
    }

    public static String detectEncryptDataType(String input)throws Exception {
        return input.contains("\"i\":") && input.contains("\"r\":") && input.contains("\"t\":") ? HTTPRequestEncryptor(input) :
            (input.contains("\"i\":") && input.contains("\"r\":") && input.contains("\"d\":") ? HTTPResponseEncryptor(input) :
            input);
    }

    // Encryption Methods
    public static String HTTPRequestEncryptor(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String reqId = json.getString("i");
            String data = json.optString("t", "");
            return reqBody2(reqId, HTTPEncryptor(data, reqId));
        } catch (Exception e) {
            return str;
        }
    }

    public static String HTTPResponseEncryptor(String str) throws Exception {
        JSONObject jSONObject = new JSONObject(str);
        String string = jSONObject.getString("i");
        String obj = jSONObject.toString();
        jSONObject.remove("d");
        jSONObject.put("e", HttpEncrypt(obj, string));
        return jSONObject.toString();
    }

    // Decryption Methods
    public static String HTTPRequestDecryptor(String str) throws Exception {
        String reqId = getLineStr(str, 4);
        JSONObject json = new JSONObject();
        json.put("i", reqId);
        String decrypted = HTTPDecryptor(getLineStr(str, 8), reqId);
        json.put("r", 0);
        json.put(isJSON(decrypted) ? "t" : "e", 
                 isJSON(decrypted) ? new JSONObject(decrypted) : decrypted);
        return json.toString();
    }


    public static String HTTPResponseDecryptor(String str) throws Exception {
        JSONObject json = new JSONObject(str);
        return new JSONObject(HttpDecrypt(json.getString("e"), json.getString("i"))).toString();
    }

    // Core Encryption/Decryption
    public static String HTTPEncryptor(String data, String key) throws Exception {
        return Base64Encoder(RijndaelCBCZeroBytePaddingEncrypt(
                                 data.getBytes(), getKey(key).getBytes(), getIv(key).getBytes()));
    }

    public static String HTTPDecryptor(String data, String key) throws Exception {
        return new String(RijndaelCBCZeroBytePaddingDecrypt(
                              Base64Decoder(data), getKey(key).getBytes(), getIv(key).getBytes()));
    }

    public static String HttpEncrypt(String data, String key) throws Exception {
        return Base64Encoder(RijndaelCBCZeroBytePaddingEncrypt(
                                 data.getBytes(), getKey(key).getBytes(), getiv(key).getBytes()));
    }

    public static String HttpDecrypt(String data, String key) throws Exception {
        return new String(RijndaelCBCZeroBytePaddingDecrypt(
                              Base64Decoder(data), getKey(key).getBytes(), getiv(key).getBytes()));
    }

    // Utility Methods
    public static String formatTextToJson(String text) {
        try {
            return new JSONObject(text).toString(1);
        } catch (JSONException e) {
            return "无效";
        }
    }

    public static boolean isJSON(String str) {
        try {
            new JSONObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLineStr(String str, int lineNum) {
        if (lineNum <= 0) return "错误";
        String[] lines = str.split(LINE_SEP);
        return (lines.length >= lineNum) ? lines[lineNum - 1].trim() : "错误";
    }

    public static String reqBody(String reqId, String data) {
        return String.join(LINE_SEP,
                           BOUNDARY,
                           "Content-Disposition:form-data;name=\"req\"",
                           "",
                           reqId,
                           BOUNDARY,
                           "Content-Disposition:form-data;name=\"e\"",
                           "",
                           data,
                           BOUNDARY,
                           "Content-Disposition:form-data;name=\"ev\"",
                           "",
                           "1",
                           BOUNDARY);
    }

    public static String reqBody2(String reqId, String data) {
        return reqBody(reqId, data); // Identical functionality
    }

    // Crypto Helpers
    private static byte[] RijndaelCBCZeroBytePaddingEncrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher cipher = initCipher(true, key, iv);
        return processCipher(cipher, data);
    }

    private static byte[] RijndaelCBCZeroBytePaddingDecrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        PaddedBufferedBlockCipher cipher = initCipher(false, key, iv);
        return processCipher(cipher, data);
    }

    private static PaddedBufferedBlockCipher initCipher(boolean encrypt, byte[] key, byte[] iv) {
        RijndaelEngine engine = new RijndaelEngine(192);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
            new CBCBlockCipher(engine), new ZeroBytePadding());
        cipher.init(encrypt, new ParametersWithIV(new KeyParameter(key), iv));
        return cipher;
    }

    private static byte[] processCipher(PaddedBufferedBlockCipher cipher, byte[] data) throws Exception {
        byte[] output = new byte[cipher.getOutputSize(data.length)];
        int len = cipher.processBytes(data, 0, data.length, output, 0);
        len += cipher.doFinal(output, len);
        return Arrays.copyOf(output, len);
    }

    private static String getKey(String str) throws Exception {
        return md5("`jou*" + str + ")xoj'");
    }

    private static String getIv(String str) throws Exception {
        int n = Integer.parseInt(str.substring(1)) % 7;
        String key = getKey(str);
        return key.substring(n, n + 24);
    }

    private static String getiv(String str) throws Exception { // Note: lowercase 'iv'
        return getIv(str); // Identical functionality
    }

    public static String md5(String str) throws NoSuchAlgorithmException {
        return bytesToHex(MessageDigest.getInstance("MD5").digest(str.getBytes()));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    private static byte[] Base64Decoder(String str) {
        return Base64.getUrlDecoder().decode(str.replace("+", "-").replace("/", "_").replace(",", "="));
    }

    private static String Base64Encoder(byte[] bytes) {
        return new String(Base64.getUrlEncoder().encode(bytes));
    }
}
