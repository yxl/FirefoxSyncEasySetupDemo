package cn.com.mozilla.sync.easysetup;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.mozilla.sync.easysetup.JPAKEException.ExceptionType;
import cn.com.mozilla.sync.utils.BigIntegerHelper;

public class JPAKEParty {
  private String mSignerId;
  private JPAKE  mPAKE;

  public JPAKEParty(String secret, int modulusLength, String signerId,
      String peerId) {
    mSignerId = signerId;
    mPAKE = JPAKE.create(getHashedPassword(secret), modulusLength, signerId,
        peerId);
  }

  /**
   * 
   * @return a JSON string of following format: { "gx1":"...", "zkp_x1" : { "gr"
   *         : "...", "b" : "...", "id" : "sender" }, "zkp_x2" : { "gr" : "...",
   *         "b" : "...", "id" : "sender" }, }
   */
  public JSONObject generateMessageOne() throws JPAKEException {
    JPAKE.STEP1 step1 = mPAKE.generateStep1();
    try {
      JSONObject json = new JSONObject();
      json.put("gx1", BigIntegerHelper.toEvenLengthHex(step1.gx1));
      JSONObject json_zkp_x1 = new JSONObject();
      json_zkp_x1.put("gr", BigIntegerHelper.toEvenLengthHex(step1.zkp_x1.gr));
      json_zkp_x1.put("b", BigIntegerHelper.toEvenLengthHex(step1.zkp_x1.b));
      json_zkp_x1.put("id", mSignerId);
      json.put("zkp_x1", json_zkp_x1);
      json.put("gx2", BigIntegerHelper.toEvenLengthHex(step1.gx2));
      JSONObject json_zkp_x2 = new JSONObject();
      json_zkp_x2.put("gr", BigIntegerHelper.toEvenLengthHex(step1.zkp_x2.gr));
      json_zkp_x2.put("b", BigIntegerHelper.toEvenLengthHex(step1.zkp_x2.b));
      json_zkp_x2.put("id", mSignerId);
      json.put("zkp_x2", json_zkp_x2);
      return json;
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_PARTY_GENERATE_MESSAGE_ONE_JSON_ERROR, e);
    }
  }

  /**
   * 
   * @param one
   * @return a JSON string of following format: { "A": "...", "zkp_A" : { "gr":
   *         "...", "b": "...", "id": "receiver"}} }
   */
  public JSONObject generateMessageTwoFromMessageOne(JSONObject one)
      throws JPAKEException {
    try {
      JPAKE.STEP1 step1 = new JPAKE.STEP1();

      step1.gx1 = new BigInteger(one.getString("gx1"), 16);
      JSONObject jsonZKP = one.getJSONObject("zkp_x1");
      step1.zkp_x1.gr = new BigInteger(jsonZKP.getString("gr"), 16);
      step1.zkp_x1.b = new BigInteger(jsonZKP.getString("b"), 16);
      step1.gx2 = new BigInteger(one.getString("gx2"), 16);
      jsonZKP = one.getJSONObject("zkp_x2");
      step1.zkp_x2.gr = new BigInteger(jsonZKP.getString("gr"), 16);
      step1.zkp_x2.b = new BigInteger(jsonZKP.getString("b"), 16);

      mPAKE.processStep1(step1);
      JPAKE.STEP2 step2 = mPAKE.genereateStep2();
      JSONObject json = new JSONObject();
      json.put("A", BigIntegerHelper.toEvenLengthHex(step2.A));
      JSONObject json_zkp_A = new JSONObject();
      json_zkp_A.put("gr", BigIntegerHelper.toEvenLengthHex(step2.zkp_A.gr));
      json_zkp_A.put("b", BigIntegerHelper.toEvenLengthHex(step2.zkp_A.b));
      json_zkp_A.put("id", mSignerId);
      json.put("zkp_A", json_zkp_A);
      return json;
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_PARTY_GENERATE_MESSAGE_TWO_FROM_MESSAGE_ONE_JSON_ERROR,
          e);
    }
  }

  public BigInteger generateKeyFromMessageTwo(JSONObject two)
      throws JPAKEException {
    JPAKE.STEP2 step2 = new JPAKE.STEP2();
    try {
      step2.A = new BigInteger(two.getString("A"), 16);
      JSONObject jsonZKP = two.getJSONObject("zkp_A");
      step2.zkp_A.gr = new BigInteger(jsonZKP.getString("gr"), 16);
      step2.zkp_A.b = new BigInteger(jsonZKP.getString("b"), 16);

      BigInteger K = mPAKE.processStep2(step2);

      return K;
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_PARTY_GENERATE_KEY_FROM_MESSAGE_TWO_JSON_ERROR, e);
    }
  }

  private BigInteger getHashedPassword(String secret) {
    return new BigInteger(1, secret.getBytes());
  }

  /** This method is used for unit testing */
  public String getDecimalHashedPassword(String secret) {
    return getHashedPassword(secret).toString(10);
  }

}
