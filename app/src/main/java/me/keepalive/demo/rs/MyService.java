package me.keepalive.demo.rs;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import me.keepalive.demo.uts.Ml;

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Ml.d("MyService onBind");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Ml.d("MyService onStartCommand");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Ml.d("MyService onCreate");
        bindDaemonService();


        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB_MR2){
            startForeground(1,new Notification());
        }else{
            startForeground(1,new Notification());
            // 8.0以后无效
            startService(new Intent(this,InnerService.class));
        }
    }
    public static class InnerService extends  Service{
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            stopSelf();
        }
    }

    protected void bindDaemonService() {
        Intent intent = new Intent();
        ComponentName component = new ComponentName(getApplicationContext(), MyService.class);
        intent.setComponent(component);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private IBinder binder;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = iBinder;
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        public void binderDied() {
            if (binder != null) {
                binder.unlinkToDeath(this, 0);
                binder = null;
            }
            bindDaemonService();
        }
    };
}