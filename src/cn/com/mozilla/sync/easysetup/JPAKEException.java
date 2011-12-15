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

/**
 * @author Yuan Xulei
 */
public class JPAKEException extends Exception {

  private static final long                  serialVersionUID = 1L;

  private final JPAKEException.ExceptionType mType;

  public JPAKEException() {
    this(JPAKEException.ExceptionType.GENERAL);
  }

  public JPAKEException(JPAKEException.ExceptionType type) {
    mType = type;
  }

  public JPAKEException(String message) {
    this(JPAKEException.ExceptionType.GENERAL, message);
  }

  public JPAKEException(JPAKEException.ExceptionType type, String message) {
    super(message);
    mType = type;
  }

  public JPAKEException(Throwable cause) {
    this(JPAKEException.ExceptionType.GENERAL, cause);
  }

  public JPAKEException(JPAKEException.ExceptionType type, Throwable cause) {
    super(cause);
    mType = type;
  }

  public JPAKEException(String message, Throwable cause) {
    this(JPAKEException.ExceptionType.GENERAL, message, cause);
  }

  public JPAKEException(JPAKEException.ExceptionType type, String message,
      Throwable cause) {
    super(message, cause);
    mType = type;
  }

  public JPAKEException.ExceptionType getType() {
    return mType;
  }

  public enum ExceptionType {
    GENERAL, HTTP_ERROR, JPAKE_STEP1_PROCESS_VERITY_X3_OR_X4_FAILED, JPAKE_STEP1_PROCESS_GX4_IS_ONE, JPAKE_STEP2_PROCESS_VERITY_B_FAILED, JPAKE_PARTY_GENERATE_MESSAGE_ONE_JSON_ERROR, JPAKE_PARTY_GENERATE_MESSAGE_TWO_FROM_MESSAGE_ONE_JSON_ERROR, JPAKE_PARTY_GENERATE_KEY_FROM_MESSAGE_TWO_JSON_ERROR, JPAKE_PARTY_GENERATE_KEY_FROM_MESSAGE_TWO_HMAC_SHA256_ERROR, SHA_ERROR, JPAKE_CLIENT_PUT_MESSAGE_ONE_ERROR, JPAKE_CLIENT_GET_DESKTOP_MESSAGE_ONE_ERROR, JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_ONE_FAILED, JPAKE_CLIENT_REQUEST_CHANNEL_FAILED, JPAKE_CLIENT_PUT_MESSAGE_TWO_ERROR, JPAKE_CLIENT_GET_DESKTOP_MESSAGE_TWO_ERROR, JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_TWO_FAILED, JPAKE_CLIENT_PUT_MESSAGE_THREE_ERROR, JPAKE_CLIENT_GET_DESKTOP_MESSAGE_THREE_ERROR, JPAKE_CLIENT_VERIFY_DESKTOP_MESSAGE_THREE_FAILED, AES_ENCRYPT_ERROR, AES_DECRYPT_ERROR,
  }
}
