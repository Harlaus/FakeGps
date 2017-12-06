package name.caiyao.fakegps.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;


/**
 * Created by sky on 2017/3/23.
 */

public class TimeChangeReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)) {
            long currentTime = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String time = format.format(currentTime);
            Log.i("520it", "time" + "**************************" + time);
            String[] array = time.split(":");
            int hour = Integer.valueOf(array[0]);

            if(!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Toast.makeText(context,"请插入内存卡",Toast.LENGTH_SHORT).show();
            }else {
                File file=new File("/mnt/sdcard/database/time.txt");
                try {
                    FileOutputStream os=new FileOutputStream(file);
                    os.write(String.valueOf(hour).getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
