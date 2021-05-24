package me.keepalive.demo.em;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import me.keepalive.demo.rs.MyReceiver;
import me.keepalive.demo.uts.Ml;

/**
 * @author sanbo
 * @See: 闹铃相关调用
 */
public class One {

    public static void launchAlarm(Context context) {
        try {
            Ml.d("inside launchAlarm");
            // alarm唤醒
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (manager == null) {
                return;
            }
            long INTERVAL_WAKEUP_MS = 10 * 1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + INTERVAL_WAKEUP_MS;
            Intent i = new Intent(context, MyReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            } else {
                manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
            }
            Ml.d(" launchAlarm success");

        } catch (Throwable e) {
            Ml.e(e);
        }
    }

}
