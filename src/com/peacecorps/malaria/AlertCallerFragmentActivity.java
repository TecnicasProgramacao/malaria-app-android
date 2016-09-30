package com.peacecorps.malaria;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;

public class AlertCallerFragmentActivity extends FragmentActivity {
    static SharedPreferenceStore mSharedPreferenceStore;

    private static final String TAG = "AlertCallerFragment";


    /**Calls the Alert Dialog as fragment of Home Screen**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                String weeklyDate = "weeklyDate";
                getSharedPreferences();

                if(mSharedPreferenceStore.mPrefsStore.getBoolean("com.peacecorps.malaria.isWeekly", false)) {

                    final long drugTakenTimeInterval = checkDrugTakenTimeInterval(weeklyDate);

                    Log.d(TAG, "Drug taken time interval = " + Long.toString(drugTakenTimeInterval));
                    assert (drugTakenTimeInterval >= 0) : ("Drug taken time interval is negative.");
                    assert (drugTakenTimeInterval <= 7) : ("Drug taken time interval is bigger than the days in a week (7).");

                    /**Weekly Day has reached, now Alarm will remind for Pill**/
                    final int DAYS_IN_A_WEEK = 7;
                    if (drugTakenTimeInterval == 0 || drugTakenTimeInterval == DAYS_IN_A_WEEK) {
                        callAlarm();
                    } else {
                        finish();
                    }
                } else {
                    callAlarm();
                }
            }
        });

    }

    public void callAlarm() {

        /**Shows the Alert Dialog with Taken, Snooze and Not Taken Button**/

        AlertDialogFragment alert = new AlertDialogFragment();

        alert.show(getSupportFragmentManager(), "alertDemo");

        alert.setCancelable(false);

    }

    /**
     * Finding the interval between Date when last drug was taken and Today.
     *
     * @param time
     */
    public long checkDrugTakenTimeInterval(final String time) throws IllegalArgumentException {

        if(time != null && !time.isEmpty()) {

            long interval = 0;
            long today = new Date().getTime();
            Log.d(TAG, "Today = " + Long.toString(today));

            long takenDate = mSharedPreferenceStore.mPrefsStore.getLong("com.peacecorps.malaria."
                    + time, 0);

            Log.d(TAG, "Drug taken time = " + Long.toString(takenDate));

            final long HOURS_IN_A_DAY = 24;
            final long MINUTES_IN_A_HOUR = 60;
            final long SECONDS_IN_A_MINUTE = 60;
            final long MEDIAN_WEIGHT = 1000;

            long oneDay = MEDIAN_WEIGHT * SECONDS_IN_A_MINUTE * MINUTES_IN_A_HOUR * HOURS_IN_A_DAY;
            interval = (today - takenDate) / oneDay;

            return interval;
        }else {
            throw new IllegalArgumentException("String time is null or empty");
        }

    }

    public void getSharedPreferences() {
        /**reading the application SharedPreferences for storing of time and drug selected**/
        mSharedPreferenceStore.mPrefsStore = getSharedPreferences(
                "com.peacecorps.malaria.storeTimePicked", Context.MODE_PRIVATE);
        mSharedPreferenceStore.mEditor = mSharedPreferenceStore.mPrefsStore
                .edit();
    }
}
