package name.caiyao.fakegps.hook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import name.caiyao.fakegps.util.CoordinateConvert;


public class MainHook implements IXposedHookLoadPackage{

    private int lac = 0, cid = 0;
    //public double latitude=22.20463,longitude=113.5524;//澳门
    private ArrayList<Double> latitudeList = new ArrayList<>();
    private ArrayList<Double> longitudeList = new ArrayList<>();
    private StringBuilder sb = null;
    private int hour = 0;
    private Handler mHandler;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        final Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
        final Context systemContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
        Uri uri = Uri.parse("content://name.caiyao.fakegps.data.AppInfoProvider/app");
        Cursor cursor = systemContext.getContentResolver().query(uri, new String[]{"latitude", "longitude", "lac", "cid"},
                null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                //火星转gps    地图上获取到的是GCJ-02
                double[] doubles= CoordinateConvert.gcj2WGSExactly(cursor.getDouble(cursor.getColumnIndex("latitude")),
                        cursor.getDouble(cursor.getColumnIndex("longitude")));
                latitudeList.add(doubles[0]);
                longitudeList.add(doubles[1]);
                cursor.moveToNext();
            }
            cursor.close();

            /**
             * 当为微信，高德地图的时候进行hook
             */
            if (loadPackageParam.packageName.contains("com.tencent.mm") || loadPackageParam.packageName.contains("com.autonavi.minimap")) {

                mHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        if(msg.what==1){
                            getTime();
                            doHook(loadPackageParam);
                        }

                        mHandler.sendEmptyMessageDelayed(1,60*1000);
                    }
                };
                  mHandler.sendEmptyMessageDelayed(1,50);
            }


        } else {
            HookUtils.HookAndChange(loadPackageParam.classLoader, 39.916544, 116.445943, lac, cid);
        }


    }

    /**
     * 获取广播存在sd卡的文件时间内容
     */

    public void getTime() {
        try {
            if (!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.i("520it", "" + "************** 不存在内存卡************");
            } else {
                File file1 = new File("/mnt/sdcard/database/time.txt");
                InputStream in = new FileInputStream(file1);
                if (in != null) {
                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader reader1 = new BufferedReader(reader);
                    String line = "";
                    String result = "";
                    while ((line = reader1.readLine()) != null) {
                        result += line;
                    }
                    hour = Integer.valueOf(result);
                    in.close();
                    Log.i("520it", "result" + "**************************" + result);
                } else {
                    hour = 7;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 写log到文件
     */

    public void writeLogToFile() {

        if (!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i("520it", "" + "************** 不存在内存卡************");
        } else {
            File file = new File("/mnt/sdcard/database/log.txt");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                PrintStream ps = new PrintStream(outputStream);
                ps.println(sb.toString());
                sb = null;
                ps.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 根据时间做hook操作
     *
     * @param loadPackageParam
     */

    public void doHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (hour >= 7 && hour <= 22) {
            if (hour == 7) {
                if (latitudeList.size() >= 1) {

                    double latitude = latitudeList.get(0) + (double)
                            new Random().nextInt(60) / 1000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(0) + (double)
                            new Random().nextInt(60) / 1000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    Log.i("520it", "" + "************   7HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(0));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(0));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {

                    doDefaultHook(loadPackageParam);

                }

                writeLogToFile();
            } else if (hour == 8) {

                if (latitudeList.size() >= 1) {
                    double latitude = latitudeList.get(0) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(0) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   8HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(0));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(0));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 9) {
                if (latitudeList.size() >= 2) {
                    double latitude = latitudeList.get(1) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(1) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   9HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(1));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(1));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 10) {
                if (latitudeList.size() >= 3) {
                    double latitude = latitudeList.get(2) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(2) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   10HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(2));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(2));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 11) {
                if (latitudeList.size() >= 4) {
                    double latitude = latitudeList.get(3) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(3) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   11HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(3));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(3));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 12) {
                if (latitudeList.size() >= 5) {
                    double latitude = latitudeList.get(4) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(4) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;


                    Log.i("520it", "" + "************   12HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(4));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(4));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 13) {
                if (latitudeList.size() >= 5) {
                    double latitude = latitudeList.get(4) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(4) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   13HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(4));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(4));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 14) {
                if (latitudeList.size() >= 6) {
                    double latitude = latitudeList.get(5) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(5) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;


                    Log.i("520it", "" + "************   14HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(5));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(5));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 15) {
                if (latitudeList.size() >= 7) {
                    double latitude = latitudeList.get(6) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(6) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   15HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(6));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(6));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else if (hour == 16) {
                if (latitudeList.size() >= 8) {
                    double latitude = latitudeList.get(7) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(7) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   16HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(7));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(7));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            } else {
                if (latitudeList.size() >= 8) {
                    double latitude = latitudeList.get(7) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
                    double longitude = longitudeList.get(7) + (double)
                            new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;

                    Log.i("520it", "" + "************   17HOUR  **************");
                    sb=new StringBuilder();
                    sb.append("当前hook的时间点" + hour + "点");
                    sb.append("\n\r");
                    sb.append("偏移前的经度" + longitudeList.get(7));
                    sb.append("\n\r");
                    sb.append("偏移前的纬度" + latitudeList.get(7));
                    sb.append("\n\r");
                    sb.append("偏移后的的经度" + longitude);
                    sb.append("\n\r");
                    sb.append("偏移后的纬度" + latitude);
                    sb.append("\n\r");
                    HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
                } else {
                    doDefaultHook(loadPackageParam);
                }
                writeLogToFile();
            }

        }
    }

    /***
     * 当收藏的地点少于8个（被删除的时候）默认区第一个地点
     *
     * @param loadPackageParam
     */

    public void doDefaultHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        double latitude = latitudeList.get(0) + (double)
                new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
        double longitude = longitudeList.get(0) + (double)
                new Random().nextInt(60) / 100000000 + ((double) new Random().nextInt(99999999)) / 100000000000000d;
        Log.i("520it", "" + "************   7HOUR  **************");
        sb=new StringBuilder();
        sb.append("当前hook的时间点" + hour + "点");
        sb.append("\n\r");
        sb.append("偏移前的经度" + longitudeList.get(0));
        sb.append("\n\r");
        sb.append("偏移前的纬度" + latitudeList.get(0));
        sb.append("\n\r");
        sb.append("偏移后的的经度" + longitude);
        sb.append("\n\r");
        sb.append("偏移后的纬度" + latitude);
        sb.append("\n\r");
        HookUtils.HookAndChange(loadPackageParam.classLoader, latitude, longitude, lac, cid);
    }

}






