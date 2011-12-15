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
import java.io.DataOutputStream;
import java.io.IOException;

public class Hex {
  private static final String DIGITS = "0123456789ABCDEF";

  /**
   * Convert binary data to a hex-encoded String
   * 
   * @param b
   *          An array containing binary data
   * @return A String containing the encoded data
   */
  public static String encode(byte[] b) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    for (int i = 0; i < b.length; i++) {
      short value = (short) (b[i] & 0xFF);
      byte high = (byte) (value >> 4);
      byte low = (byte) (value & 0xF);
      os.write(DIGITS.charAt(high));
      os.write(DIGITS.charAt(low));
    }
    return new String(os.toByteArray());
  }

  /**
   * Convert a hex-encoded String to binary data
   * 
   * @param str
   *          A String containing the encoded data
   * @return An array containing the binary data, or null if the string is
   *         invalid
   */
  public static byte[] decode(String str) {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    byte[] raw = str.getBytes();
    for (int i = 0; i < raw.length; i++) {
      if (!Character.isWhitespace((char) raw[i]))
        bs.write(raw[i]);
    }
    byte[] in = bs.toByteArray();
    if (in.length % 2 != 0) {
      return null;
    }

    bs.reset();
    DataOutputStream ds = new DataOutputStream(bs);

    for (int i = 0; i < in.length; i += 2) {
      byte high = (byte) DIGITS.indexOf(Character.toUpperCase((char) in[i]));
      byte low = (byte) DIGITS.indexOf(Character.toUpperCase((char) in[i + 1]));
      try {
        ds.writeByte((high << 4) + low);
      } catch (IOException e) {
      }
    }
    return bs.toByteArray();
  }
}
