package cn.com.mozilla.sync.utils;

import java.math.BigInteger;

public class BigIntegerHelper {
  public static byte[] BigIntegerToByteArrayWithoutSign(BigInteger value) {
    byte[] bytes = value.toByteArray();
    if (bytes[0] == (byte) 0) {
      bytes = ArrayHelper.copyArray(bytes, 1, bytes.length - 1);
    }
    return bytes;
  }

  /**
   * Convert an array of bytes to a non-negative big integer.
   */
  public static BigInteger ByteArrayToBigIntegerWithoutSign(byte[] array) {
    return new BigInteger(1, array);
  }

  /**
   * Convert a big integer into hex string. If the length is not even, add an
   * '0' character in the beginning to make it even.
   */
  public static String toEvenLengthHex(BigInteger value) {
    String result = value.toString(16);
    if (result.length() % 2 != 0) {
      result = "0" + result;
    }
    return result;
  }
}
