package name.caiyao.fakegps.hook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sky on 2017/3/24.
 */

public class StopReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,MyTimeService.class);
        context.startService(intent1);
    }
}
