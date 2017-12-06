package name.caiyao.fakegps.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import name.caiyao.fakegps.R;


public class SplashActivity  extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题栏（状态栏）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.splash);
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);//跳转到MainActivity
                SplashActivity.this.finish();//结束SplashActivity
            }
        }, 1500);//给postDelayed()方法传递延迟参数
    }
}
