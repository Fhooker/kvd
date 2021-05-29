package me.keepalive.demo.rs;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

@SuppressLint("NewApi")
public class MyJobService extends JobService {


    private static final String TAG = "MyJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startJob(this);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void startJob(Context ctx) {
        JobScheduler jobScheduler = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo.Builder builder = new JobInfo.Builder(8,
                new ComponentName(ctx.getPackageName(), MyJobService.class.getName()))
                //@RequiresPermission(android.Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .setPersisted(true);

        // 小于7.0,没意义
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            builder.setPeriodic(1000);
        } else {
            //7.0之后默认5s,延迟1s循环
            builder.setMinimumLatency(1000);
        }
        jobScheduler.schedule(builder.build());
    }
}