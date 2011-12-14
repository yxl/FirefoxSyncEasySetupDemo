package cn.com.mozilla.sync.utils;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.com.mozilla.sync.easysetup.JPAKEException;
import cn.com.mozilla.sync.easysetup.JPAKEException.ExceptionType;

public class AES {

  public static byte[] encrypt(byte[] data, byte[] key, byte[] iv,
      boolean padding) throws JPAKEException {
    String transformation = padding ? "AES/CBC/PKCS5Padding"
        : "AES/CBC/NoPadding";
    try {
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
      return cipher.doFinal(data);
    } catch (GeneralSecurityException e) {
      throw new JPAKEException(ExceptionType.AES_ENCRYPT_ERROR, e.toString());

    }
  }

  public static byte[] decrypt(byte[] data, byte[] key, byte[] iv,
      boolean padding) throws JPAKEException {
    String transformation = padding ? "AES/CBC/PKCS5Padding"
        : "AES/CBC/NoPadding";
    try {
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
      return cipher.doFinal(data);
    } catch (GeneralSecurityException e) {
      throw new JPAKEException(ExceptionType.AES_DECRYPT_ERROR, e.toString());

    }
  }

}
