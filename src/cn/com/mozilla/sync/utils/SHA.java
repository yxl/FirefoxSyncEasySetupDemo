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
