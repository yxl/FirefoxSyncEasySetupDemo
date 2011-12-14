package cn.com.mozilla.sync.task;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import cn.com.mozilla.sync.easysetup.EasySetupClient;
import cn.com.mozilla.sync.easysetup.JPAKEException;

public class EasySetupTask extends AsyncTask<String, EasySetupStep, Throwable> {
  private static final String LOG_TAG = "EasySetupTask";
  
  private static final int   POLL_INTERVAL          = 1000;
  private static final int   MAX_P0LL_RETRIES_ONE   = 300;
  private static final int   MAX_P0LL_RETRIES_TWO   = 20;
  private static final int   MAX_P0LL_RETRIES_THREE = 20;

  private IEasySetupListener mListener;
  private EasySetupClient    mClient;

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
