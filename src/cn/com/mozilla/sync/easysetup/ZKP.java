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

import java.math.BigInteger;
import java.security.MessageDigest;

import cn.com.mozilla.sync.utils.BigIntegerHelper;
import cn.com.mozilla.sync.utils.RandomHelper;

public class ZKP {
  public BigInteger gr = BigInteger.ZERO; // g^r, r is a random number
  public BigInteger b  = BigInteger.ZERO; // b = r - x*h, h=hash(g, g^r, g^x,

  // signer)

  public static boolean verify(BigInteger p, BigInteger q, BigInteger g,
      BigInteger gx, ZKP zkp_x, String signerID) {
    /* sig={g^r, b} */
    BigInteger h = hash(g, zkp_x.gr, gx, signerID);
    if (gx.compareTo(BigInteger.ZERO) == 1 && // g^x > 0
        gx.compareTo(p.subtract(BigInteger.ONE)) == -1 && // g^x < p-1
        gx.modPow(q, p).compareTo(BigInteger.ONE) == 0 && // g^x^q = 1
        /*
         * Below, I took an straightforward way to compute g^b * g^x^h, which
         * needs 2 exp. Using a simultaneous computation technique would only
         * need 1 exp.
         */
        g.modPow(zkp_x.b, p).multiply(gx.modPow(h, p)).mod(p)
            .compareTo(zkp_x.gr) == 0) // g^r=g^x * g^x^h
      return true;
    else
      return false;
  }

  public ZKP() {
  }

  public ZKP(BigInteger p, BigInteger q, BigInteger g, BigInteger gx,
      BigInteger x, String signerID) {
    this(p, q, g, gx, x, signerID, RandomHelper.getRandomBigInteger(q));
  }

  /**
   * Generate a ZKP whose r value is not random but constant for unit test
   */
  public ZKP(BigInteger p, BigInteger q, BigInteger g, BigInteger gx,
      BigInteger x, String signerID, BigInteger r) {
    gr = g.modPow(r, p);
    BigInteger h = hash(g, gr, gx, signerID); // h
    b = r.subtract(x.multiply(h).mod(q)).mod(q); // b = r-x*h
  }

  private static BigInteger hash(BigInteger g, BigInteger gr, BigInteger gx,
      String signerID) {
    MessageDigest sha = null;
    byte[] md = null;

    try {
      sha = MessageDigest.getInstance("SHA-256");
      sha.reset();

      /*
       * Note: you should ensure the items in H(...) have clear boundaries. It
       * is simple if the other party knows sizes of g, gr, gx and signerID and
       * hence the boundary is unambiguous. If not, you'd better prepend each
       * item with its byte length, but I've omitted that here.
       */

      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(g));
      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(gr));
      hashByteArrayWithLength(sha,
          BigIntegerHelper.BigIntegerToByteArrayWithoutSign(gx));
      hashByteArrayWithLength(sha, signerID.getBytes());
      md = sha.digest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return BigIntegerHelper.ByteArrayToBigIntegerWithoutSign(md);
  }

  private static void hashByteArrayWithLength(MessageDigest sha, byte[] data) {
    int length = data.length;
    byte[] b = new byte[] { (byte) (length >>> 8), (byte) (length & 0xff) };
    sha.update(b);
    sha.update(data);
  }
}
