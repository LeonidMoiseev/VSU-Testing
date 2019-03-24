package com.lion.test_rating.StudentAccount;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;

import com.lion.test_rating.R;

public class TimerReceiver extends BroadcastReceiver {

    int testTime;
    CountDownTimer cTimer;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        testTime = StartTestActivity.testTime;
        someTask();
    }

    void someTask() {

        cTimer = new CountDownTimer(60000, 1) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {

                if (StartTestActivity.testFinished) {
                    cTimer.cancel();
                }

                if (((millisUntilFinished / 1000) < 10) && testTime < 10) {
                    StartTestActivity.mTimeLeft.setText("0" + Integer.toString(testTime - 1)
                            + " : 0" + Integer.toString((int) (millisUntilFinished / 1000)));
                } else if ((millisUntilFinished / 1000) < 10) {
                    StartTestActivity.mTimeLeft.setText(Integer.toString(testTime - 1)
                            + " : 0" + Integer.toString((int) (millisUntilFinished / 1000)));
                } else if (testTime < 10) {
                    StartTestActivity.mTimeLeft.setText("0" + Integer.toString(testTime - 1)
                            + " : " + Integer.toString((int) (millisUntilFinished / 1000)));
                } else {
                    StartTestActivity.mTimeLeft.setText(Integer.toString(testTime - 1)
                            + " : " + Integer.toString((int) (millisUntilFinished / 1000)));
                }

                if (testTime - 1 == 0) {
                    StartTestActivity.mTimeLeft.setTextColor(mContext.getResources().getColor(R.color.red_btn));
                } else StartTestActivity.mTimeLeft.setTextColor(mContext.getResources().getColor(R.color.white));
            }

            public void onFinish() {
                if (testTime - 1 == 0) {
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(mContext);
                    localBroadcastManager.sendBroadcast(new Intent("com.lion.close"));
                    cTimer.cancel();
                } else {
                    testTime--;
                    cTimer.start();
                }
            }

        };
        cTimer.start();
    }
}
