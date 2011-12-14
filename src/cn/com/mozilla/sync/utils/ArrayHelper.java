package cn.com.mozilla.sync.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ArrayHelper {
  public static byte[] copyArray(byte[] original, int start, int length) {
    byte[] copy = new byte[length];
    System.arraycopy(original, start, copy, 0,
        Math.min(original.length - start, length));
    return copy;
  }

  public static boolean equals(byte[] a, byte[] b) {
    if (a == b)
      return true;
    if (a == null || b == null)
      return false;
    int length = a.length;
    if (length != b.length)
      return false;
    for (int i = 0; i < length; i++) {
      if (a[i] != b[i])
        return false;
    }
    return true;
  }

  public static byte[] charArrayToByteArray(char[] src) {
    if (src == null)
      return null;
    int length = src.length;
    byte[] result = new byte[length];
    for (int i = 0; i < length; i++) {
      result[i] = (byte) src[i];
    }
    return result;
  }

  public static byte[] toAsciiBytes(String data) {
    byte[] result = null;
    if (data != null) {
      Charset ascii = Charset.forName("US-ASCII");
      result = ascii.encode(data).array();
    }
    return result;
  }

  public static String toAsciiString(byte[] data) {
    String result = null;
    if (data != null) {
      try {
        result = new String(data, "US-ASCII");
      } catch (UnsupportedEncodingException e) {
      }
    }
    return result;
  }
}
