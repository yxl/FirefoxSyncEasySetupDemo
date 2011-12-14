package cn.com.mozilla.sync;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;
import cn.com.mozilla.sync.easysetup.EasySetupClient;

public class EasySetupClientTest extends AndroidTestCase {
  private EasySetupClient mClient;

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mClient = new EasySetupClient("https://setup.services.mozilla.com/");
  }

  @Suppress
  public void testRequestChannel() {
    String pin = "";
    try {
      pin = mClient.requestPIN();
    } catch (Exception e) {
      assertTrue("requestPIN throws exception: " + e.toString(), false);
    }
    assertTrue("Invalid PIN length", pin.length() == 12);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    mClient.shutdown();
    super.tearDown();
  }
}
