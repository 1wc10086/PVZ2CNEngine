
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class XMLCryptoCall {


    public static String XMLDecrypt(CharSequence cs) throws Exception {
        StringBuilder sb=new StringBuilder();
        String[] split=cs.toString().split("&");
        for (int i=0;i < split.length;i++) {
            String[] split2=split[i].split("=");
            if (i != 0)sb.append("&");
            sb.append(split2[0] + "=");
            if (split2[0].equals("md5")) {
                sb.append(split2[1]);
            } else {
                sb.append(TwPayDecrypt(split2[1]));
            }
        }
        return sb.toString();
    }

    public static String XMLEncrypt(CharSequence cs) throws Exception {
        StringBuilder sb=new StringBuilder();
        String[] split=cs.toString().split("&");
        for (int i=0;i < split.length;i++) {
            String[] split2=split[i].split("=");
            if (i != 0)sb.append("&");
            sb.append(split2[0] + "=");
            if (split2[0].equals("md5")) {
                sb.append(split2[1]);
            } else {
                sb.append(TwPayEncrypt(split2[1]));
            }
        }
        return sb.toString();
    }

    private static final String TwPayKey = "TwPay001";
    private static final String encoding = "utf-8"; 
    private static final byte[] TwPayIv = {1, 2, 3, 4, 5, 6, 7, 8};
    private static final String algorithm = "DES/CBC/PKCS5Padding";
    private static final String baseAlgorithm = "DES";


    public static String TwPayDecrypt(String str) throws Exception {
        byte[] StringToBytes = hexStringToByteArray(str);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(TwPayIv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TwPayKey.getBytes(), baseAlgorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(2, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(StringToBytes), encoding);
    }

    public static String TwPayEncrypt(String str) throws Exception {
        byte[] bytes = str.getBytes(encoding);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(TwPayIv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(TwPayKey.getBytes(), baseAlgorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(1, secretKeySpec, ivParameterSpec);
        return bytesToHexString(cipher.doFinal(bytes));
    }

    private static byte[] hexStringToByteArray(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
