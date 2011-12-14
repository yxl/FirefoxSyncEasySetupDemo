package cn.com.mozilla.sync.easysetup;

import cn.com.mozilla.sync.utils.ArrayHelper;
import cn.com.mozilla.sync.utils.SHA;

public class JPAKEKeys {
  private byte[] mCryptoKey;
  private byte[] mHmacKey;

  public JPAKEKeys(byte[] key) throws JPAKEException {
    byte[] data = SHA.HDKFSHA256Expand(key,
        ArrayHelper.toAsciiBytes("Sync-AES_256_CBC-HMAC256"), 32 * 2);
    mCryptoKey = ArrayHelper.copyArray(data, 0, 32);
    mHmacKey = ArrayHelper.copyArray(data, 32, 32);
  }

  public byte[] getCryptoKey() {
    return mCryptoKey;
  }

  public byte[] getHmacKey() {
    return mHmacKey;
  }
}
