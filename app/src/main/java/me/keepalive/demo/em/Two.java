package me.keepalive.demo.em;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Field;

import me.keepalive.demo.uts.Ml;

/**
 * @author sanbo
 * @See: 速启service java 版本
 */
public class Two {

    private static IBinder mRemote;
    private static Parcel mServiceData;

    private static int mPid = Process.myPid();

    private static int transactCode;

    static {
        switch (Build.VERSION.SDK_INT) {
            case 26:
            case 27:
                transactCode = 26;
                break;
            case 28:
                transactCode = 30;
                break;
            case 29:
                transactCode = 24;
                break;
            default:
                transactCode = 34;
                break;
        }
    }

    public static boolean launchService(Context context, String serviceName) {
        boolean isSusscess = false;
        try {
            Ml.i("inside launchService ");
            long begin = System.currentTimeMillis();
            initAmsBinder();
            initServiceParcel(context, serviceName);
            isSusscess = startServiceByAmsBinder();
            long end = System.currentTimeMillis();
            Ml.i("launch service success, 耗时:" + (end - begin));
        } catch (Throwable e) {
            Ml.e(e);
        }
        return isSusscess;
    }

    private static void initAmsBinder() {
        Class<?> activityManagerNative;
        try {
            activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object amn = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            if (amn == null) {
                return;
            }
            Field mRemoteField = amn.getClass().getDeclaredField("mRemote");
            mRemoteField.setAccessible(true);
            mRemote = (IBinder) mRemoteField.get(amn);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // when processName dead, we should save time to restart and kill self, don`t take a waste of time to recycle
    private static void initServiceParcel(Context context, String serviceName) {
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), serviceName);
        intent.setComponent(component);

        Parcel parcel = Parcel.obtain();
        intent.writeToParcel(parcel, 0);

        mServiceData = Parcel.obtain();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Android 8.1 frameworks/base/core/java/android/app/IActivityManager.aidl
             * ComponentName startService(in IApplicationThread caller, in Intent service,
             *    in String resolvedType, boolean requireForeground, in String callingPackage, int userId);
             *
             * frameworks/base/services/core/java/com/android/server/am/ActiveServices.java
             * if (fgRequired) {
             *     final int mode = mAm.mAppOpsService.checkOperation(
             *             AppOpsManager.OP_START_FOREGROUND, r.appInfo.uid, r.packageName);
             *     switch (mode) {
             *         case AppOpsManager.MODE_ALLOWED:
             *         case AppOpsManager.MODE_DEFAULT: // All okay.
             *             break;
             *         case AppOpsManager.MODE_IGNORED:
             *             // Not allowed, fall back to normal start service, failing siliently if background check restricts that.
             *             fgRequired = false;
             *             forceSilentAbort = true;
             *             break;
             *         default:
             *             return new ComponentName("!!", "foreground not allowed as per app op");
             *     }
             * }
             * requireForeground 要求启动service之后，调用service.startForeground()显示一个通知，不然会崩溃
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            mServiceData.writeInt(1);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null); // resolvedType
//            mServiceData.writeInt(context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O ? 1 : 0);
            mServiceData.writeInt(0);
            mServiceData.writeString(context.getPackageName()); // callingPackage
            mServiceData.writeInt(0); // userId
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // http://aospxref.com/android-7.1.2_r36/xref/frameworks/base/core/java/android/app/ActivityManagerNative.java
            /* ActivityManagerNative#START_SERVICE_TRANSACTION
             *  case START_SERVICE_TRANSACTION: {
             *             data.enforceInterface(IActivityManager.descriptor);
             *             IBinder b = data.readStrongBinder();
             *             IApplicationThread app = ApplicationThreadNative.asInterface(b);
             *             Intent service = Intent.CREATOR.createFromParcel(data);
             *             String resolvedType = data.readString();
             *             String callingPackage = data.readString();
             *             int userId = data.readInt();
             *             ComponentName cn = startService(app, service, resolvedType, callingPackage, userId);
             *             reply.writeNoException();
             *             ComponentName.writeToParcel(cn, reply);
             *             return true;
             *         }
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null);  // resolvedType
            mServiceData.writeString(context.getPackageName()); // callingPackage
            mServiceData.writeInt(0); // userId
        } else {
            /* Android4.4 ActivityManagerNative#START_SERVICE_TRANSACTION
             * case START_SERVICE_TRANSACTION: {
             *             data.enforceInterface(IActivityManager.descriptor);
             *             IBinder b = data.readStrongBinder();
             *             IApplicationThread app = ApplicationThreadNative.asInterface(b);
             *             Intent service = Intent.CREATOR.createFromParcel(data);
             *             String resolvedType = data.readString();
             *             int userId = data.readInt();
             *             ComponentName cn = startService(app, service, resolvedType, userId);
             *             reply.writeNoException();
             *             ComponentName.writeToParcel(cn, reply);
             *             return true;
             *         }
             */
            mServiceData.writeInterfaceToken("android.app.IActivityManager");
            mServiceData.writeStrongBinder(null);
            intent.writeToParcel(mServiceData, 0);
            mServiceData.writeString(null);  // resolvedType
            mServiceData.writeInt(0); // userId
        }
    }

    private static boolean startServiceByAmsBinder() {
        try {
            if (mRemote == null || mServiceData == null) {
                Log.e("Daemon", "REMOTE IS NULL or PARCEL IS NULL !!!");
                return false;
            }
            mRemote.transact(transactCode, mServiceData, null, 1); // flag=FLAG_ONEWAY=1
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
