public class BitCryptoCall {

    public static long encrypt(long j) {
        if (j < 0) {
            j += 4294967296L;
        }
        StringBuilder sb = new StringBuilder();
        String longTobinary = longTobinary(j);
        int length = longTobinary.length();
        for (long j2 = 32; j2 > length; j2--) {
            sb.append("0");
        }
        sb.append(longTobinary);
        long binaryTolong = binaryTolong(new StringBuffer().append(new StringBuffer().append(sb.toString().substring(13, 28)).append(binaryPart3Inverse(sb.toString().substring(28, 32))).toString()).append(sb.toString().substring(0, 13)).toString());
        if (binaryTolong >= 2147483648L) {
            return binaryTolong - 4294967296L;
        }
        return binaryTolong;
    }   
    public static long Decrypt(long j) {
        long j2;
        int[] iArr = {13, 12, 15, 14, 9, 8, 11, 10, 5, 4, 7, 6, 1, 0, 3, 2};
        if (j >= 0) {
            j2 = (j / 8192) % 16;
        } else {
            j += 4294967296L;
            j2 = (j / 8192) % 16;
        }
        long j3 = 8192;
        long j4 = 16;
        long j5 = (((j / j3) / j4) * j4) + iArr[(int) j2] + (524288 * (j % j3));
        if (j5 > Integer.MAX_VALUE) {
            return j5 - 4294967296L;
        }
        return j5;
    }

    public static String longTobinary(long j) {
        return Long.toBinaryString(j);
    }

    public static long binaryTolong(String str) {
        return Long.parseLong(str, 2);
    }

    public static String binaryPart3Inverse(String str) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        int i = 0;
        for (int i2 = 0; i2 < charArray.length; i2++) {
            char c = charArray[i2];
            if (i == 2) {
                sb.append(c);
            } else {
                sb.append(c == '0' ? '1' : '0');
            }
            i++;
        }
        return sb.toString();
    }
}
