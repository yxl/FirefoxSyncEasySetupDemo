package cn.com.mozilla.sync.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.com.mozilla.sync.easysetup.JPAKEException;
import cn.com.mozilla.sync.easysetup.JPAKEException.ExceptionType;

public class SHA {
  private static final int SHA256_DIGEST_LENGTH = 32;

  public static byte[] HDKFSHA256Expand(byte[] data, byte[] info, int length)
      throws JPAKEException {
    int iterations = (length + SHA256_DIGEST_LENGTH - 1) / SHA256_DIGEST_LENGTH;
    ByteArrayOutputStream tr = new ByteArrayOutputStream(iterations
        * SHA256_DIGEST_LENGTH);
    ByteArrayOutputStream tn = new ByteArrayOutputStream();
    byte[] tmp;
    for (int i = 0; i < iterations; i++) {
      try {
        tn.write(info);
        tn.write(i + 1);
        tmp = HMACSHA256(tn.toByteArray(), data);
        tr.write(tmp);
        tn = new ByteArrayOutputStream();
        tn.write(tmp);
      } catch (IOException e) {
        throw new JPAKEException(ExceptionType.SHA_ERROR, e.toString());
      }
    }
    return ArrayHelper.copyArray(tr.toByteArray(), 0, length);
  }

  public static byte[] HMACSHA256(byte[] data, byte[] key)
      throws JPAKEException {
    byte[] result = null;
    try {
      Mac hmacSha256;
      hmacSha256 = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(key,
          "HmacSHA256");
      hmacSha256.init(secret_key);
      result = hmacSha256.doFinal(data);
    } catch (GeneralSecurityException e) {
      throw new JPAKEException(ExceptionType.SHA_ERROR, e.toString());
    }
    return result;
  }

  public static byte[] sha1(String message) {
    byte[] result = null;
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA1");
      digest.update(ArrayHelper.toAsciiBytes(message));
      result = digest.digest();
    } catch (GeneralSecurityException e) {
    }
    return result;
  }
}
