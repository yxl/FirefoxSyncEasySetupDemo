/*
 * Copyright 2010 Patrick Woodworth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
