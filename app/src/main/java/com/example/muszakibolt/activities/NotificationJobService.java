package com.example.muszakibolt.activities;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


public class NotificationJobService extends JobService {
    private NotificationHelper notificationHelper;
    private static final String LOG_TAG = NotificationJobService.class.getName();
    @Override
    public boolean onStartJob(JobParameters params) {
        notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.send("Sikeres vásárlás!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
