package org.altbrasov.phonehash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adi on 11/23/14
 */
public class GcmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String hash = intent.getExtras().getString("hash");
        String message = intent.getExtras().getString("message");
    }
}
