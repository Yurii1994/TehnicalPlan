package com.muv.technicalplan;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class Service extends android.app.Service
{
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        someTaskOfTime();
        return START_STICKY;
    }

    void someTaskOfTime()
    {

    }

    public static boolean isRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (Service.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class Alarm extends BroadcastReceiver {

        public static final String ALARM_EVENT = "net.multipi.ALARM";
        public static final int ALARM_INTERVAL_SEC = 5;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isRunning(context)) {
                context.startService(new Intent(context, Service.class));
            }
        }

        public static void setAlarm(Context context) {
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ALARM_EVENT), 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * ALARM_INTERVAL_SEC, pi);
        }

        public static void cancelAlarm(Context context) {
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent(ALARM_EVENT), 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
