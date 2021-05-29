package me.keepalive.demo.rs.acc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class AccountHelper {

    public static void addAccount(Context ctx) {
        AccountManager accountmanager = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] as = accountmanager.getAccountsByType("me.keepalive.account");
        if (as != null && as.length > 0) {
            return;
        }
        Account account = new Account("mm", "me.keepalive.account");
        accountmanager.addAccountExplicitly(account, "mm", new Bundle());
    }

    public static void autoSync() {
        Account account = new Account("mm", "me.keepalive.account");
        //告诉进程同步
        ContentResolver.setIsSyncable(account,"me.keepalive.provider",1);
        //设置自动同步
        ContentResolver.setSyncAutomatically(account,"me.keepalive.providerr",true);
        // 队列
        ContentResolver.addPeriodicSync(account,"me.keepalive.provider",new Bundle(),1);
        ContentResolver.setMasterSyncAutomatically(true);
    }

}
