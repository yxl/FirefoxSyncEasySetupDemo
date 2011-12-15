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
package cn.com.mozilla.sync.task;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import cn.com.mozilla.sync.easysetup.EasySetupClient;
import cn.com.mozilla.sync.easysetup.JPAKEException;

public class EasySetupTask extends AsyncTask<String, EasySetupStep, Throwable> {
  private static final String LOG_TAG                = "EasySetupTask";

  private static final int    POLL_INTERVAL          = 1000;
  private static final int    MAX_P0LL_RETRIES_ONE   = 300;
  private static final int    MAX_P0LL_RETRIES_TWO   = 20;
  private static final int    MAX_P0LL_RETRIES_THREE = 20;

  private IEasySetupListener  mListener;
  private EasySetupClient     mClient;

  public EasySetupTask(Context context, IEasySetupListener listener) {
    mListener = listener;
  }

  private String mPin = "";

  public String getPin() {
    return mPin;
  }

  @Override
  protected Throwable doInBackground(String... arg0) {
    Throwable result = null;

    String serverURL = arg0[0];
    try {
      publishProgress(EasySetupStep.BEGIN);
      mClient = new EasySetupClient(serverURL);
      mPin = mClient.requestPIN();

      publishProgress(EasySetupStep.RETRIVE_CHANNEL);

      mClient.putMessageOne();
      publishProgress(EasySetupStep.PUT_MOBILE_MESSAGE_ONE);

      int i;
      for (i = 0; i < MAX_P0LL_RETRIES_ONE; i++) {
        Thread.sleep(POLL_INTERVAL);
        if (mClient.getDesktopMessageOne())
          break;
      }
      if (i == MAX_P0LL_RETRIES_ONE) {
        throw new Exception("getDesktopMessageOne timeout!");
      }
      publishProgress(EasySetupStep.RETRIVE_DESKTOP_MESSAGE_ONE);

      mClient.putMobileMessageTwo();
      publishProgress(EasySetupStep.PUT_MOBILE_MESSAGE_TWO);

      for (i = 0; i < MAX_P0LL_RETRIES_TWO; i++) {
        Thread.sleep(POLL_INTERVAL);
        if (mClient.getDesktopMessageTwo())
          break;
      }
      if (i == MAX_P0LL_RETRIES_TWO) {
        throw new Exception("getDesktopMessageTwo timeout!");
      }
      publishProgress(EasySetupStep.RETRIVE_DESKTOP_MESSAGE_TWO);

      mClient.putMobileMessageThree();
      publishProgress(EasySetupStep.PUT_MOBILE_MESSAGE_THREE);

      for (i = 0; i < MAX_P0LL_RETRIES_THREE; i++) {
        Thread.sleep(POLL_INTERVAL);
        if (mClient.getDesktopMessageThree())
          break;
      }
      if (i == MAX_P0LL_RETRIES_THREE) {
        throw new Exception("getDesktopMessageThree timeout!");
      }
      publishProgress(EasySetupStep.RETRIVE_DESKTOP_MESSAGE_THREE);

      mClient.shutdown();

      JSONObject jsonData = mClient.getAccountInfo();
      String serverUrl = jsonData.getString("serverURL");
      String account = jsonData.getString("account");
      String passWord = jsonData.getString("password");
      String passPhrase = jsonData.getString("synckey");
      Log.i(LOG_TAG, "Server: " + serverUrl);
      Log.i(LOG_TAG, "Account: " + account);
      Log.i(LOG_TAG, "Passw word: " + passWord);
      Log.i(LOG_TAG, "Sync key: " + passPhrase);
    } catch (JPAKEException e) {
      result = e;
    } catch (Exception e) {
      result = e;
    }

    return result;
  }

  @Override
  protected void onCancelled() {
    mClient.shutdown();
    mListener.onEasySetupCancelled();
    super.onCancelled();
  }

  @Override
  protected void onProgressUpdate(EasySetupStep... values) {
    mListener.onEasySetupProgress(values[0]);
  }

  @Override
  protected void onPostExecute(Throwable result) {
    mClient.shutdown();
    mListener.onEasySetupEnd(result);
  }
}
