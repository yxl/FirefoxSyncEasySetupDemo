package cn.com.mozilla.sync;

import java.math.BigInteger;

import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;
import cn.com.mozilla.sync.easysetup.JPAKEException;
import cn.com.mozilla.sync.easysetup.JPAKEParty;
import cn.com.mozilla.sync.utils.ArrayHelper;

public class JPAKEPartyTest extends AndroidTestCase {

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Suppress
  public void testPasswordHashing() {
    JPAKEParty party = new JPAKEParty("Cheese", 3072, "Alice", "Bob");
    assertEquals(party.getDecimalHashedPassword("Cheese"), "74115656807269");
  }

  public void testPasswordChange1024() {
    JPAKEParty a = new JPAKEParty("abcd1234", 1024, "Alice", "Bob");
    JPAKEParty b = new JPAKEParty("abcd1234", 1024, "Bob", "Alice");
    runPasswordExchange(a, b, true);
  }

  public void testPasswordChange1024WithWrongPassword() {
    JPAKEParty a = new JPAKEParty("all you need is cheese", 1024, "Alice",
        "Bob");
    JPAKEParty b = new JPAKEParty("all you need is bacon", 1024, "Bob", "Alice");
    runPasswordExchange(a, b, false);
  }

  public void testPasswordChange2048() {
    JPAKEParty a = new JPAKEParty("abcd1234", 2048, "Alice", "Bob");
    JPAKEParty b = new JPAKEParty("abcd1234", 2048, "Bob", "Alice");
    runPasswordExchange(a, b, true);
  }

  public void testPasswordChange2048WithWrongPassword() {
    JPAKEParty a = new JPAKEParty("all you need is cheese", 2048, "Alice",
        "Bob");
    JPAKEParty b = new JPAKEParty("all you need is bacon", 2048, "Bob", "Alice");
    runPasswordExchange(a, b, false);
  }

  public void testPasswordChange3072() {
    JPAKEParty a = new JPAKEParty("abcd1234", 3072, "Alice", "Bob");
    JPAKEParty b = new JPAKEParty("abcd1234", 3072, "Bob", "Alice");
    runPasswordExchange(a, b, true);
  }

  public void testPasswordChange3072WithWrongPassword() {
    JPAKEParty a = new JPAKEParty("all you need is cheese", 3072, "Alice",
        "Bob");
    JPAKEParty b = new JPAKEParty("all you need is bacon", 3072, "Bob", "Alice");
    runPasswordExchange(a, b, false);
  }

  private void runPasswordExchange(JPAKEParty a, JPAKEParty b,
      boolean isPasswordEqual) {
    try {
      JSONObject a1 = a.generateMessageOne();
      JSONObject b1 = b.generateMessageOne();
      JSONObject a2 = a.generateMessageTwoFromMessageOne(b1);
      JSONObject b2 = b.generateMessageTwoFromMessageOne(a1);
      byte[] ka = a.generateKeyFromMessageTwo(b2).toByteArray();
      byte[] kb = b.generateKeyFromMessageTwo(a2).toByteArray();
      assertEquals(ArrayHelper.equals(ka, kb), isPasswordEqual);
    } catch (JPAKEException e) {
      assertTrue(e.toString(), false);
    }
  }

  public void test() {
    BigInteger three = new BigInteger("3");
    BigInteger o = new BigInteger("0");
    BigInteger one = new BigInteger("1");
    BigInteger tmp = o.subtract(one).mod(three);
    assertEquals(tmp.intValue(), 2);
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
