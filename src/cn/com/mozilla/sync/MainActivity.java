package cn.com.mozilla.sync;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import cn.com.mozilla.sync.task.EasySetupStep;
import cn.com.mozilla.sync.task.EasySetupTask;
import cn.com.mozilla.sync.task.IEasySetupListener;

/**
 * Easy Setup activity.
 */
public class MainActivity extends Activity implements IEasySetupListener {

  // Constant used to retrieve start activity result of
  // SyncPreferencesActivity
  private static final int                            WEAVE_PREFERENCES_ACTIVITY = 0;

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

  private void afterWeavePreferencesActivity(int resultCode) {
    if (resultCode == RESULT_OK) {
      setResult(RESULT_OK);
      finish();
    }
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

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onActivityResult(int, int,
   * android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case WEAVE_PREFERENCES_ACTIVITY: {
      afterWeavePreferencesActivity(resultCode);
      return;
    }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
