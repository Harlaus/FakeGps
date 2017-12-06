package name.caiyao.fakegps.hook;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import name.caiyao.fakegps.R;
import name.caiyao.fakegps.ui.activity.MainActivity;

/**
 * Created by sky on 2017/3/23.
 */

public class MyTimeService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification=new Notification.Builder(this).
                setContentTitle("千网游").
                setContentText("千网游服务").
                setSmallIcon(R.drawable.ic_lan).build();
        startForeground(1,notification);
        TimeChangeReciver reciver=new TimeChangeReciver();
        registerReceiver(reciver,new IntentFilter(Intent.ACTION_TIME_TICK));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent1=new Intent(this,StopReceiver.class);
        sendBroadcast(intent1);

    }
}
