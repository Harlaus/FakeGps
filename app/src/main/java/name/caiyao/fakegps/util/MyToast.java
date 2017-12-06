package name.caiyao.fakegps.util;

import android.content.Context;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sky on 2017/3/11.
 */

public class MyToast {

    static Toast toast;

    public static void setToast(Context context, String msg,int dur) {

        toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0,600);   //从现在起过delay毫秒以后，每隔period毫秒执行一次。

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, dur);

    }
}
