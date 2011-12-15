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

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomHelper {
  /**
   * Get a random BigInteger in [0, n)
   * 
   * @param max
   * @return
   */
  public static BigInteger getRandomBigInteger(BigInteger n) {
    BigInteger r = null;
    SecureRandom rnd = new SecureRandom();
    do {
      r = new BigInteger(n.bitLength(), rnd);
    } while (r.compareTo(n) >= 0);
    return r;
  }

  /**
   * Get a random byte array with specified length
   */
  public static byte[] getRandomByteArray(int length) {
    byte[] result = new byte[length];
    SecureRandom rand = new SecureRandom();
    rand.nextBytes(result);
    return result;
  }

  /**
   * GUIDs are 9 random bytes encoded with base64url (RFC 4648). That makes them
   * 12 characters long with 72 bits of entropy.
   */
  public static String makeGUID() {
    return encodeBase64url(getRandomByteArray(9));
  }

  /**
   * Encode byte string as base64url (RFC 4648).
   */
  public static String encodeBase64url(byte[] bytes) {
    return Base64.encodeToString(bytes, Base64.DEFAULT).replace("+", "-")
        .replace("/", "_");
  }
}
