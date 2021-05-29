package me.keepalive.demo.rs.acc;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

public class SyncService extends Service {
    AbstractThreadedSyncAdapter adapter;

    public SyncService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        adapter = new SyncAdapate(getApplicationContext(), true);
    }

    public static class SyncAdapate extends AbstractThreadedSyncAdapter {

        public SyncAdapate(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        }
    }
}