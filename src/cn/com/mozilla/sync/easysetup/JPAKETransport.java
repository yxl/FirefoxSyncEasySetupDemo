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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import cn.com.mozilla.sync.utils.HttpsTransport;

/**
 * @author Yuan Xulei
 */
public class JPAKETransport {
  private static final String  ETAG_HEADER = "ETag";

  private final String         mClientId;

  private final HttpsTransport mHttpsTransport;

  public JPAKETransport(String clientId) {
    mClientId = clientId;
    mHttpsTransport = new HttpsTransport();
  }

  public void shutdown() {
    mHttpsTransport.shutdown();
  }

  public JPAKEResponse execGetMethodWithHeader(String url, String headerName,
      String headerValue) throws JPAKEException {
    HttpGet method = new HttpGet(url);
    return execHttpMethodWithHeader(method, headerName, headerValue);
  }

  public JPAKEResponse execPutMethodWithHeader(String url, String body,
      String headerName, String headerValue) throws JPAKEException,
      UnsupportedEncodingException {
    HttpPut method = new HttpPut(url);
    method.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
    return execHttpMethodWithHeader(method, headerName, headerValue);
  }

  private JPAKEResponse execHttpMethodWithHeader(HttpRequestBase method,
      String headerName, String headerValue) throws JPAKEException {

    method.addHeader("Pragma", "no-cache");
    method.addHeader("Cache-Control", "no-cache");
    method.addHeader("X-KeyExchange-Id", mClientId);
    if (headerName != null) {
      method.addHeader(headerName, headerValue);
    }

    try {
      HttpResponse response = mHttpsTransport.execHttpMethod(method);

      // get the response body
      String body = "";
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        body = EntityUtils.toString(entity).trim();
      }

      // get ETag from reponse headers
      String etag = "";
      Header header = response.getFirstHeader(ETAG_HEADER);
      if (header != null) {
        etag = header.getValue().replace("\"", "").trim();
      }

      // check status code
      int statusCode = response.getStatusLine().getStatusCode();
      boolean isOk = statusCode == HttpStatus.SC_OK;
      if (statusCode != HttpStatus.SC_OK
          && statusCode != HttpStatus.SC_NOT_MODIFIED) {
        throw new JPAKEException(JPAKEException.ExceptionType.HTTP_ERROR,
            "Invalid Http Status Code: " + statusCode + ", reson: "
                + response.getStatusLine().getReasonPhrase());
      }

      return new JPAKEResponse(etag, body, isOk);
    } catch (IOException e) {
      throw new JPAKEException(JPAKEException.ExceptionType.HTTP_ERROR, e);
    }
  }

  public static class JPAKEResponse {
    private String  mETag       = "";
    private String  mBody       = "";
    private boolean mIsStatusOk = false;

    public JPAKEResponse() {
      this("", "", false);
    }

    public JPAKEResponse(String etag, String body, boolean isOk) {
      mETag = etag;
      mBody = body;
      mIsStatusOk = isOk;
    }

    public String getETag() {
      return mETag;
    }

    public String getBody() {
      return mBody;
    }

    public boolean isStatusOk() {
      return mIsStatusOk;
    }
  }
}
