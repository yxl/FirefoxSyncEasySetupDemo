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
package cn.com.mozilla.sync;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.widget.EditText;
import cn.com.mozilla.sync.task.EasySetupStep;
import cn.com.mozilla.sync.task.EasySetupTask;
import cn.com.mozilla.sync.task.IEasySetupListener;

/**
 * Easy Setup activity.
 */
public class MainActivity extends Activity implements IEasySetupListener {

  private String                                      mPIN                       = "";

  // three EditText controls used to show PIN
  private EditText                                    mPIN1EditText;
  private EditText                                    mPIN2EditText;
  private EditText                                    mPIN3EditText;

  private ProgressDialog                              mProgressDialog;

  // variables holding EasySetupTask thread
  private static final AtomicReference<EasySetupTask> mEasySetupThread           = new AtomicReference<EasySetupTask>();
  private EasySetupTask                               mEasySetupTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mPIN1EditText = (EditText) findViewById(R.id.EasySetupActivity_PIN1);
    mPIN1EditText.setText("");

    mPIN2EditText = (EditText) findViewById(R.id.EasySetupActivity_PIN2);
    mPIN2EditText.setText("");

    mPIN3EditText = (EditText) findViewById(R.id.EasySetupActivity_PIN3);
    mPIN3EditText.setText("");

    mProgressDialog = new ProgressDialog(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    super.onResume();
    startEasySetup();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause() {
    super.onPause();
    stopEasySetup();
  }

  private void startEasySetup() {
    mPIN = "";
    showPIN();

    mEasySetupTask = new EasySetupTask(this, this);
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setTitle("Sync");
    mProgressDialog.setMessage("Connecting...");
    mProgressDialog.setCancelable(true);
    mProgressDialog.setOnCancelListener(new OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        mEasySetupTask.cancel(true);
      }
    });

    boolean retVal = mEasySetupThread.compareAndSet(null, mEasySetupTask);
    if (retVal) {
      mEasySetupTask.execute("https://setup.services.mozilla.com/");
    }
  }

  private void stopEasySetup() {
    mEasySetupTask.cancel(true);
    mEasySetupThread.compareAndSet(mEasySetupTask, null);
    showPIN();
  }

  private void showPIN() {
    String[] texts = new String[] { "", "", "" };
    if (mPIN.length() == 12) {
      for (int i = 0; i < 3; i++) {
        texts[i] = mPIN.substring(i * 4, i * 4 + 4);
      }
    }
    mPIN1EditText.setText(texts[0]);
    mPIN2EditText.setText(texts[1]);
    mPIN3EditText.setText(texts[2]);
  }

  @Override
  public void onEasySetupCancelled() {
    mProgressDialog.dismiss();
    mPIN = "";
  }

  @Override
  public void onEasySetupEnd(Throwable result) {
    stopEasySetup();

    // if result is not null, there exists errors.
    if (result != null) {

      // show a retry dialog
      String title = "Setup Failed!";
      String message = result.getMessage();
      showOkCancelDialog(this, android.R.drawable.ic_dialog_alert, title,
          message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              stopEasySetup();
              startEasySetup();
            }
          });

    } else {
      setResult(RESULT_OK);
      finish();
    }
    mProgressDialog.dismiss();
  }

  private static void showOkCancelDialog(Context context, int icon,
      String title, String message, DialogInterface.OnClickListener onYes) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setCancelable(true);
    builder.setIcon(icon);
    builder.setTitle(title);
    builder.setMessage(message);

    builder.setInverseBackgroundForced(true);
    builder.setPositiveButton("OK", onYes);
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  @Override
  public void onEasySetupProgress(EasySetupStep step) {
    switch (step) {
    case BEGIN:
      mProgressDialog.show();
      mProgressDialog.setMessage("Getting channel...");
      break;
    case RETRIVE_CHANNEL:
      mPIN = mEasySetupTask.getPin();
      showPIN();
      mProgressDialog.dismiss();
      break;
    case PUT_MOBILE_MESSAGE_ONE:
      break;
    case RETRIVE_DESKTOP_MESSAGE_ONE:
      mProgressDialog.show();
      mProgressDialog.setMessage(step.toString());
      break;
    default:
      mProgressDialog.setMessage(step.toString());
    }
  }
}
