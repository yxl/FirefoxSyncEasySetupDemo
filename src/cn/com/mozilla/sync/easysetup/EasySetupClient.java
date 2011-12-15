/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is FirefoxSyncEasySetupDemo.
 *
 * The Initial Developer of the Original Code is
 * the Mozilla Foundation.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *  Yuan Xulei <xyuan@mozilla.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package cn.com.mozilla.sync.easysetup;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import cn.com.mozilla.sync.easysetup.JPAKEException.ExceptionType;
import cn.com.mozilla.sync.utils.AES;
import cn.com.mozilla.sync.utils.ArrayHelper;
import cn.com.mozilla.sync.utils.Base64;
import cn.com.mozilla.sync.utils.BigIntegerHelper;
import cn.com.mozilla.sync.utils.Hex;
import cn.com.mozilla.sync.utils.RandomHelper;
import cn.com.mozilla.sync.utils.SHA;

/**
 * 
 * @author Yuan Xulei
 * 
 */
public class EasySetupClient {

  private static final String KNOWN_VALUE_STRING         = "0123456789ABCDEF";
  private static final int    CCB_LOCK_SIZE_AES128       = 16;

  private String              mServerURL                 = "https://setup.services.mozilla.com/";
  private String              mChannel                   = "";
  private JPAKETransport      mTransport;

  private JSONObject          mAccountInfo               = null;

  /**
   * It is difficult to distinguish some characters, such as 0 and 0, 1 and l.
   * So we don't use these characters.
   */
  private static final String PERMITTED_SECRET_CHARATERS = "abcdefghijkmnpqrstuvwxyz23456789";
  private static final int    SECRET_LENGTH              = 8;
  private String              mSecret                    = "";
  private JPAKEParty          mJPAKEParty;

  private static final int    CLIENT_ID_LENGTH           = 256;
  private String              mClientId                  = "";
  private String              mETag                      = "";
  private JSONObject          mMessageTwo;
  private byte[]              mKey;

  public EasySetupClient(String serverURL) {
    mClientId = generateRandomClientID();
    mSecret = generateRandomSecret();
    mServerURL = serverURL;
    mTransport = new JPAKETransport(mClientId);
  }

  public void shutdown() {
    mTransport.shutdown();
  }

  public JSONObject getAccountInfo() {
    return mAccountInfo;
  }

  public String requestPIN() throws JPAKEException {
    JPAKETransport.JPAKEResponse response = mTransport.execGetMethodWithHeader(
        mServerURL + "new_channel", null, null);
    mChannel = response.getBody().replace("\"", "").trim();
    return mSecret + mChannel;
  }

  public void putMessageOne() throws JPAKEException {
    mJPAKEParty = new JPAKEParty(mSecret, 3072, "receiver", "sender");
    try {
      JSONObject payload = mJPAKEParty.generateMessageOne();
      JSONObject message = new JSONObject();

      message.put("type", "receiver1");
      message.put("payload", payload);

      JPAKETransport.JPAKEResponse response = mTransport
          .execPutMethodWithHeader(mServerURL + mChannel, message.toString(),
              "If-None-Match", "*");
      mETag = response.getETag();
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    } catch (UnsupportedEncodingException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    }
  }

  public boolean getDesktopMessageOne() throws JPAKEException {
    try {
      JPAKETransport.JPAKEResponse response = mTransport
          .execGetMethodWithHeader(mServerURL + mChannel, "If-None-Match",
              mETag);
      if (response.isStatusOk()) {
        mETag = response.getETag();
        JSONObject message = new JSONObject(response.getBody());
        mMessageTwo = generateMessageTwoFromDesktopMessageOne(message);
        return true;
      } else {
        return false;
      }
    } catch (ParseException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_ONE_ERROR, e);
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_ONE_ERROR, e);
    }
  }

  public void putMobileMessageTwo() throws JPAKEException {
    try {
      JSONObject message = new JSONObject();

      message.put("type", "receiver2");
      message.put("payload", mMessageTwo);

      JPAKETransport.JPAKEResponse response = mTransport
          .execPutMethodWithHeader(mServerURL + mChannel, message.toString(),
              "If-Match", mETag);

      mETag = response.getETag();
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    } catch (UnsupportedEncodingException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    }
  }

  public boolean getDesktopMessageTwo() throws JPAKEException {
    try {
      JPAKETransport.JPAKEResponse response = mTransport
          .execGetMethodWithHeader(mServerURL + mChannel, "If-None-Match",
              mETag);
      if (response.isStatusOk()) {
        mETag = response.getETag();
        JSONObject message = new JSONObject(response.getBody());
        mKey = generateKeyFromDesktopMessageTwo(message);
        return true;
      } else {
        return false;
      }
    } catch (ParseException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_TWO_ERROR, e);
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_TWO_ERROR, e);
    }
  }

  public void putMobileMessageThree() throws JPAKEException {
    try {
      JSONObject message = new JSONObject();

      JPAKEKeys jpakeKeys = new JPAKEKeys(mKey);
      byte[] knownValue = ArrayHelper.toAsciiBytes(KNOWN_VALUE_STRING);
      byte[] iv = RandomHelper.getRandomByteArray(CCB_LOCK_SIZE_AES128);
      byte[] ciphertext = AES.encrypt(knownValue, jpakeKeys.getCryptoKey(), iv,
          true);
      JSONObject payload = new JSONObject();
      payload.put("ciphertext",
          Base64.encodeToString(ciphertext, Base64.NO_WRAP));
      payload.put("IV", Base64.encodeToString(iv, Base64.NO_WRAP));

      message.put("type", "receiver3");
      message.put("payload", payload);

      JPAKETransport.JPAKEResponse response = mTransport
          .execPutMethodWithHeader(mServerURL + mChannel, message.toString(),
              "If-Match", mETag);
      mETag = response.getETag();
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    } catch (UnsupportedEncodingException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, e);
    }
  }

  public boolean getDesktopMessageThree() throws JPAKEException {
    try {
      JPAKETransport.JPAKEResponse response = mTransport
          .execGetMethodWithHeader(mServerURL + mChannel, "If-None-Match",
              mETag);
      if (response.isStatusOk()) {
        mETag = response.getETag();
        JSONObject message = new JSONObject(response.getBody());
        decryptDesktopMessageThress(message);
        return true;
      } else {
        return false;
      }
    } catch (ParseException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_THREE_ERROR, e);
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_GET_DESKTOP_MESSAGE_THREE_ERROR, e);
    }
  }

  private JSONObject generateMessageTwoFromDesktopMessageOne(JSONObject message)
      throws JPAKEException {
    try {
      String type = message.getString("type");
      if (!type.equals("sender1")) {
        throw new JPAKEException(
            ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_ONE_FAILED,
            "Invalid message type: " + type);
      }
      JSONObject payload = message.getJSONObject("payload");
      return mJPAKEParty.generateMessageTwoFromMessageOne(payload);
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_ONE_FAILED, e);
    }
  }

  private byte[] generateKeyFromDesktopMessageTwo(JSONObject message)
      throws JPAKEException {
    try {
      String type = message.getString("type");
      if (!type.equals("sender2")) {
        throw new JPAKEException(
            ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_TWO_FAILED,
            "Invalid message type: " + type);
      }
      JSONObject payload = message.getJSONObject("payload");
      BigInteger K = mJPAKEParty.generateKeyFromMessageTwo(payload);

      Mac hmacSha256 = Mac.getInstance("HMACSHA256");
      byte[] zerokey = new byte[32];
      Arrays.fill(zerokey, (byte) 0);
      SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(zerokey,
          "SHA256");
      hmacSha256.init(secret_key);
      byte[] key = hmacSha256.doFinal(BigIntegerHelper
          .BigIntegerToByteArrayWithoutSign(K));
      return key;
    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_TWO_FAILED, e);
    } catch (GeneralSecurityException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_TWO_FAILED, e);
    }
  }

  private void decryptDesktopMessageThress(JSONObject message)
      throws JPAKEException {
    try {
      String type = message.getString("type");
      if (!type.equals("sender3")) {
        throw new JPAKEException(
            ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_THREE_FAILED,
            "Invalid message type: " + type);
      }
      JPAKEKeys keys = new JPAKEKeys(mKey);

      JSONObject payload = message.getJSONObject("payload");
      String ciphertextString = payload.getString("ciphertext");
      byte[] iv = Base64.decode(payload.getString("IV"), Base64.DEFAULT);
      byte[] ciphertext = Base64.decode(ciphertextString, Base64.DEFAULT);
      byte[] hmac = Hex.decode(payload.getString("hmac"));
      byte[] cipherTextData = ArrayHelper.toAsciiBytes(ciphertextString);
      byte[] hmacValue = SHA.HMACSHA256(cipherTextData, keys.getHmacKey());
      if (!ArrayHelper.equals(hmac, hmacValue)) {
        throw new JPAKEException(
            ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_TWO_FAILED,
            "ciphertext HMAC verifying failed.");
      }
      byte[] plaintext = AES.decrypt(ciphertext, keys.getCryptoKey(), iv, true);
      mAccountInfo = new JSONObject(new String(plaintext, "utf8"));

    } catch (JSONException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_THREE_FAILED, e);
    } catch (UnsupportedEncodingException e) {
      throw new JPAKEException(
          ExceptionType.JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_THREE_FAILED, e);
    }
  }

  /**
   * Generate a session id, which is 256 random hexadecimal characters from '0'
   * to '9' and 'a' to 'f'.
   */
  private String generateRandomClientID() {
    return Hex.encode(RandomHelper.getRandomByteArray(CLIENT_ID_LENGTH / 2));
  }

  /**
   * Generate a JPAKE Secret as 8 random characters.
   */
  private String generateRandomSecret() {
    StringBuilder secret = new StringBuilder(SECRET_LENGTH);
    Random rand = new Random();
    int n = PERMITTED_SECRET_CHARATERS.length();
    for (int i = 0; i < SECRET_LENGTH; i++) {
      int r = rand.nextInt(n);
      secret.append(PERMITTED_SECRET_CHARATERS.charAt(r));
    }
    return secret.toString();
  }
}
