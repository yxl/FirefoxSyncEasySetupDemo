package cn.com.mozilla.sync;

import java.nio.charset.Charset;

import android.test.AndroidTestCase;
import cn.com.mozilla.sync.easysetup.JPAKEException;
import cn.com.mozilla.sync.easysetup.JPAKEKeys;
import cn.com.mozilla.sync.utils.ArrayHelper;

public class JPAKEKeysTest extends AndroidTestCase {
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testKeyDerivation() {
    Charset ascii = Charset.forName("US-ASCII");
    byte[] key = ascii.encode("0123456789abcdef0123456789abcdef").array();
    byte[] expectedCryptoKey = new byte[] { 0x53, 0x00, (byte) 0x84, 0x3c,
        (byte) 0xc2, 0x0d, 0x56, (byte) 0x88, (byte) 0x81, 0x47, 0x15,
        (byte) 0x97, 0x52, 0x4f, 0x12, 0x5c, (byte) 0x8f, (byte) 0xe3,
        (byte) 0xf8, 0x06, (byte) 0xa5, 0x48, (byte) 0xce, (byte) 0xfd, 0x05,
        0x32, 0x26, (byte) 0xdd, (byte) 0xb5, (byte) 0xf4, 0x0f, (byte) 0xe8 };

    byte[] expectedHmacKey = new byte[] { 0x1f, 0x1f, 0x0b, (byte) 0x85,
        (byte) 0xc3, 0x41, (byte) 0x8c, 0x2b, 0x26, (byte) 0xa6, (byte) 0x8a,
        0x0a, 0x40, 0x49, (byte) 0xe2, (byte) 0x92, 0x58, 0x5a, 0x05, 0x68,
        (byte) 0xbd, 0x1c, (byte) 0x8a, (byte) 0xdd, (byte) 0x97, (byte) 0xe6,
        0x6d, (byte) 0xbb, 0x65, (byte) 0xb8, (byte) 0xbe, (byte) 0x99 };

    JPAKEKeys keysObject;
    try {
      keysObject = new JPAKEKeys(key);
      assertTrue(ArrayHelper.equals(keysObject.getCryptoKey(),
          expectedCryptoKey));
      assertTrue(ArrayHelper.equals(keysObject.getHmacKey(), expectedHmacKey));
    } catch (JPAKEException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
