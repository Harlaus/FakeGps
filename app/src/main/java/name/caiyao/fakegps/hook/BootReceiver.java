package name.caiyao.fakegps.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot

            Intent intent1=new Intent(context,MyTimeService.class);
            context.startService(intent1);
        }
    }
}
