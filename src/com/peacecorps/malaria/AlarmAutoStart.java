package com.peacecorps.malaria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class AlarmAutoStart: responsable for starting the alaram when told to do so.
 */

public class AlarmAutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(R.string.alarm_auto_start_boot_completed_intent_check)) {
            context.startService(new Intent(context, AlarmService.class));
        }
    }

}
