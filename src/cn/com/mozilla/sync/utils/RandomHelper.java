package cn.com.mozilla.sync.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomHelper {
  /**
   * Get a random BigInteger in [0, n)
   * 
   * @param max
   * @return
   */
  public static BigInteger getRandomBigInteger(BigInteger n) {
    BigInteger r = null;
    SecureRandom rnd = new SecureRandom();
    do {
      r = new BigInteger(n.bitLength(), rnd);
    } while (r.compareTo(n) >= 0);
    return r;
  }

  /**
   * Get a random byte array with specified length
   */
  public static byte[] getRandomByteArray(int length) {
    byte[] result = new byte[length];
    SecureRandom rand = new SecureRandom();
    rand.nextBytes(result);
    return result;
  }

  /**
   * GUIDs are 9 random bytes encoded with base64url (RFC 4648). That makes them
   * 12 characters long with 72 bits of entropy.
   */
  public static String makeGUID() {
    return encodeBase64url(getRandomByteArray(9));
  }

  /**
   * Encode byte string as base64url (RFC 4648).
   */
  public static String encodeBase64url(byte[] bytes) {
    return Base64.encodeToString(bytes, Base64.DEFAULT).replace("+", "-")
        .replace("/", "_");
  }
}
