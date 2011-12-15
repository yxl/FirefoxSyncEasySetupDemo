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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ArrayHelper {
  public static byte[] copyArray(byte[] original, int start, int length) {
    byte[] copy = new byte[length];
    System.arraycopy(original, start, copy, 0,
        Math.min(original.length - start, length));
    return copy;
  }

  public static boolean equals(byte[] a, byte[] b) {
    if (a == b)
      return true;
    if (a == null || b == null)
      return false;
    int length = a.length;
    if (length != b.length)
      return false;
    for (int i = 0; i < length; i++) {
      if (a[i] != b[i])
        return false;
    }
    return true;
  }

  public static byte[] charArrayToByteArray(char[] src) {
    if (src == null)
      return null;
    int length = src.length;
    byte[] result = new byte[length];
    for (int i = 0; i < length; i++) {
      result[i] = (byte) src[i];
    }
    return result;
  }

  public static byte[] toAsciiBytes(String data) {
    byte[] result = null;
    if (data != null) {
      Charset ascii = Charset.forName("US-ASCII");
      result = ascii.encode(data).array();
    }
    return result;
  }

  public static String toAsciiString(byte[] data) {
    String result = null;
    if (data != null) {
      try {
        result = new String(data, "US-ASCII");
      } catch (UnsupportedEncodingException e) {
      }
    }
    return result;
  }
}
