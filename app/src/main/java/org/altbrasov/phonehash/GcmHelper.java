package org.altbrasov.phonehash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonElement;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by adi on 11/23/14
 */
public class GcmHelper {

    /*
    Generates the regId, may be called with a null String in case of an error
     */
    public static interface GenerateRegIdListener{
        void onGeneratedRegId(String regId);
    }

    private static final String REG_ID = "reg_id";
    private static final String APP_VER_CODE = "app_vers";
    private static final String SENDER_ID = "518001443909";

    public static boolean checkPlayServices(final Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //we have a solution !
                Dialog fixDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0);
                fixDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        checkPlayServices(activity);
                    }
                });
                fixDialog.show();
            } else {
                //this could throw an exception
                //throw new UnsupportedOperationException("play services is not supported on this device");
                Toast.makeText(activity, "You have an unsupported device :(", Toast.LENGTH_LONG).show();
                //activity.finish();

            }
            return false;
        }
        return true;
    }

    public static String getRegId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String regId = prefs.getString(REG_ID, null);
        if (TextUtils.isEmpty(regId)) {
            return null;
        }

        int appVer = prefs.getInt(APP_VER_CODE, -1);
        if (appVer != BuildConfig.VERSION_CODE)
            return null;

        return regId;
    }

    private static void saveRegId(Context context, String regId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(REG_ID, regId);
        prefsEditor.putInt(APP_VER_CODE, BuildConfig.VERSION_CODE);
        prefsEditor.apply();
    }

    public static void getAndGenerateRegId(Context context, GenerateRegIdListener listener) {
        String regId = getRegId(context);
        if (TextUtils.isEmpty(regId)) {
            new GenerateRegIdTask(context, listener).execute();
        }
        else if(listener!= null)
            listener.onGeneratedRegId(regId);
    }


    private static void showToast(Context context, boolean itsOk) {
        if (itsOk)
            Toast.makeText(context, "Push messages activated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "FAILED to register for push messages", Toast.LENGTH_LONG).show();
    }


    private static class GenerateRegIdTask extends AsyncTask<Void, Void, String> {
        private final Context context;
        private final GenerateRegIdListener listener;


        public GenerateRegIdTask(Context context, GenerateRegIdListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... nothings) {
            try {
                final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                return gcm.register(SENDER_ID);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String regId) {
            if(listener != null)
                listener.onGeneratedRegId(regId);
            if (TextUtils.isEmpty(regId)) {
                showToast(context, false);
            } else {
                //enable the line under to cache the regId for grabbing using getRegId
                // saveRegId(context, regId);
                showToast(context, true);

            }
        }
    }
}
