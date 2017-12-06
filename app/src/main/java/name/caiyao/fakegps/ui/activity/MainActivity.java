package name.caiyao.fakegps.ui.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import name.caiyao.fakegps.R;
import name.caiyao.fakegps.bean.Address;
import name.caiyao.fakegps.dao.TempDao;
import name.caiyao.fakegps.data.DbHelper;
import name.caiyao.fakegps.hook.MyTimeService;
import name.caiyao.fakegps.hook.ScreenListener;
import name.caiyao.fakegps.ui.fragment.CollectionFragment;
import name.caiyao.fakegps.ui.fragment.HelpFragment;
import name.caiyao.fakegps.ui.fragment.OneFragment;
import name.caiyao.fakegps.ui.fragment.SettingFragment;
import name.caiyao.fakegps.util.MyToast;
import name.caiyao.fakegps.util.ToastUtil;

import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;


import static name.caiyao.fakegps.R.drawable.marker;

public class MainActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapClickListener, LocationSource, AMapLocationListener, CollectionFragment.CalbackValue {
    private TextView tv_count;
    private GeocodeSearch geocoderSearch;
    private MapView mv;
    static private AMap aMap;
    static LatLng tempLatLng;
    private LatLng searchLatLng;
    private int lac = 0, cid = 0, count = 0;
    private SQLiteDatabase mSQLiteDatabase;
    static public List<LatLng> onMatchIntList = new ArrayList<>();//坐标连线集合

    NavigationView mNavView;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    static private LatLonPoint latLonPoint;
    static private String addressName;
    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private TempDao dao;
    final MarkerOptions searchmarkerOptions = new MarkerOptions();
    static public Marker tempmarker;
    private boolean isClick; //判断是否点击地图
    private boolean falg;//点击地图，不让toast显示两次
    private TextView tv_state;
    private TempDao tempDao;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private Intent intent1;
    private ScreenListener l;
    private List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mv = (MapView) findViewById(R.id.mv);
        tv_state = (TextView) findViewById(R.id.tv_state);
        assert mv != null;
        mv.onCreate(savedInstanceState);
        aMap = mv.getMap();
        setUpMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setOnMapClickListener(this);
        setUpDrawer();
        initNavigationView();

        intent1 = new Intent(this, MyTimeService.class);
        startService(intent1);

        l = new ScreenListener(this);
        l.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                //Log.e("onUserPresent", "onUserPresent");
            }

            @Override
            public void onScreenOn() {
                startService(intent1);
                // Log.i("520it", "" + "************  锁屏后开启服务  **************");
            }

            @Override
            public void onScreenOff() {

            }
        });

        Marker();
        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(marker));
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        //开始定位
        //  initLoc();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);//构造 GeocodeSearch 对象，并设置监听。

        SharedPreferences sp = getSharedPreferences("startTag", MODE_PRIVATE);
        String startTag = sp.getString("start", "");
        tv_state.setText(startTag);


        fabAdd();  //悬浮添加
        fabdel();  //悬浮撤回


    }

    public void setUpMap() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        tv_count = (TextView) findViewById(R.id.count);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Marker() {

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.onCreate(mSQLiteDatabase);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(6);
        polylineOptions.color(Color.BLUE);
        polylineOptions.visible(true);
        Cursor cursor = mSQLiteDatabase.query(DbHelper.APP_TEMP_NAME, new String[]{"latitude,longitude"},
                null, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));
                LatLng initlatLng = new LatLng(lat, lon);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(initlatLng);
                markerOptions.draggable(true);  //可以拖动
                aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                aMap.addMarker(markerOptions);
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(initlatLng));
                polylineOptions.add(initlatLng);
                mv.getMap().addPolyline(polylineOptions);
                cursor.moveToNext();
            }
            cursor.close();
        }
        mSQLiteDatabase.close();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:

                onClear();
                break;

            case R.id.search:
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_search, null, false);
                final EditText et_key = (EditText) view.findViewById(R.id.key);
                new AlertDialog.Builder(this).setView(view)
                        .setTitle("搜索位置")
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                search(et_key.getText().toString());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClear() {
        new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("确定要删除所有位置吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tempDao = new TempDao(getApplicationContext());
                tempDao.deleteTable();
                tv_count.setText("0");

                aMap.clear();
                onMatchIntList.clear();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();

    }

    /**
     * 悬浮按钮撤回
     */

    public void fabdel() {

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempmarker != null) {
                    tempmarker.remove();

                }
                MyToast.setToast(getApplicationContext(), "要等等,已撤回标记", 500);
                Log.e("tag", "撤回 ");
            }
        });

    }

    /**
     * 悬浮按钮添加
     */


    public void fabAdd() {


        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isClick) {
                    if (addressName == null || tempLatLng == null) {

                        Toast.makeText(getApplicationContext(), "正在解析坐标信请稍后点击添加", Toast.LENGTH_SHORT).show();
                    } else {
                        if (tempLatLng != null && addressName != null) {


                          /*  onMatchIntList.add(tempLatLng);//List<LatLng>连线集合
                            latLonPoint = convertToLatLonPoint(tempLatLng);
                            getAddress(latLonPoint);
                            onMatch();//连线*/

                            dao = new TempDao(getApplicationContext());
                            Address address = new Address();
                            address.setLatitude(tempLatLng.latitude);
                            address.setLongitude(tempLatLng.longitude);
                            address.setAddname(addressName);
                            dao.insertAdd(address);

                            onMatch(); //连线

                            dao = new TempDao(getApplicationContext());
                            list = dao.selectAllData();
                            tv_count.setText(String.valueOf(list.size()));
                        }
                        tempLatLng = null;
                        isClick = false;
                    }
                } else {
                    MyToast.setToast(getApplicationContext(), "请先点击地图！", 500);
                }

            }
        });

    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }


    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
        l.unregisterListener();

    }

    private void search(final String key) {
        final String TAG = "tag";
        PoiSearch.Query query = new PoiSearch.Query(key, null, null);
        query.setPageSize(10);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {

                if (rCode == 1000) {
                    final ArrayList<PoiItem> poiItems = poiResult.getPois();
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems.size() != 0) {
                        String[] keyList = new String[poiItems.size()];
                        for (int j = 0; j < poiItems.size(); j++) {
                            keyList[j] = poiItems.get(j).getTitle();
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("选择位置")
                                .setSingleChoiceItems(keyList, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(poiItems.get(which).getLatLonPoint().getLatitude(),
                                                poiItems.get(which).getLatLonPoint().getLongitude())));
                                        searchLatLng = new LatLng(poiItems.get(which).getLatLonPoint().getLatitude(),
                                                poiItems.get(which).getLatLonPoint().getLongitude());
                                        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                        searchmarkerOptions.position(searchLatLng);
                                        //   IntList.add(searchLatLng);//List<LatLng>集合
                                        LatLonPoint latLonPoint = convertToLatLonPoint(searchLatLng);
                                        getAddress(latLonPoint);
                                        aMap.addMarker(searchmarkerOptions);

                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        Toast.makeText(MainActivity.this, "没有搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        tempLatLng = null;
        searchLatLng = null;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        tempmarker = aMap.addMarker(markerOptions);
        markerOptions.title("经度:" + latLng.latitude + ",纬度:" + latLng.longitude);
        tempLatLng = latLng;//tempLatLng存放坐标
        LatLonPoint latLonPoint = convertToLatLonPoint(latLng);
        getAddress(latLonPoint);

        falg = true;
        isClick = true;


    }

    //    /**
//     * 响应地理编码
//     */
    public void getLatlon(final String name) {
        //showDialog();
        GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    //   @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        //  dismissDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);

            } else {
                //  ToastUtil.show(GeocoderActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求

    }

    /**
     * 逆地理编码回调
     */
    //@Override
    public void onRegeocodeSearched(final RegeocodeResult result, final int rCode) {

        final MarkerOptions markerOptions = new MarkerOptions();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress();

                markerOptions.title(addressName);
                aMap.addMarker(markerOptions);
                if (falg) {
                    MyToast.setToast(getApplicationContext(), addressName, 500);
                    falg = false;
                } else {
                    addressName = null;
                }
            } else {
                ToastUtil.show(MainActivity.this, "无结果");
            }
        } else {
            ToastUtil.showerror(getApplicationContext(), rCode);
        }


    }

    public void onMatch() {

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        mSQLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.onCreate(mSQLiteDatabase);
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(6);
        polylineOptions.color(Color.BLUE);
        polylineOptions.visible(true);
        Cursor cursor = mSQLiteDatabase.query(DbHelper.APP_TEMP_NAME, new String[]{"latitude,longitude"},
                null, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));
                LatLng initlatLng = new LatLng(lat, lon);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(initlatLng);
                markerOptions.draggable(true);  //可以拖动
                aMap.addMarker(markerOptions);
                //aMap.moveCamera(CameraUpdateFactory.changeLatLng(initlatLng));
                polylineOptions.add(initlatLng);
                mv.getMap().addPolyline(polylineOptions);
                cursor.moveToNext();
            }
            cursor.close();
        }
        mSQLiteDatabase.close();
    }

    private void initNavigationView() {
        //初始化NavigationView顶部head的icon和name
        ImageView icon = (ImageView) mNavView.getHeaderView(0).findViewById(R.id.nav_head_icon);
        icon.setImageResource(R.drawable.nav_head_icon);
        TextView name = (TextView) mNavView.getHeaderView(0).findViewById(R.id.nav_head_name);
        name.setText(R.string.app_name);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_item1:
                        dao = new TempDao(getApplicationContext());
                        List<String> list = dao.selectAllData();
                        tv_count.setText(String.valueOf(list.size()));

                        aMap.clear();
                        Marker();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new OneFragment()).commit();
                        invalidateOptionsMenu();
                        break;
                    case R.id.nav_item2:
                        toolbar.setTitle(R.string.Collection);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new CollectionFragment()).commit();
                        invalidateOptionsMenu();
                        break;
                    case R.id.nav_set:
                        toolbar.setTitle(R.string.setting);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new SettingFragment()).commit();
                        invalidateOptionsMenu();
                        break;
                    case R.id.menu_share:
                        toolbar.setTitle(R.string.help);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new HelpFragment()).commit();
                        invalidateOptionsMenu();
                        break;
                    case R.id.nav_about:
                        toolbar.setTitle(R.string.about);
                        break;

                }
                invalidateOptionsMenu();
//                //隐藏NavigationView
                item.setChecked(true);//点击了把它设为选中状态
                mDrawerLayout.closeDrawers();//关闭抽屉
                return true;
            }
        });
    }

    private void setUpDrawer() {
        setSupportActionBar(toolbar);
        //设置Toolbar左上角图标
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        //设置左上角显示三道横线
        toggle.syncState();
        // toolbar.setTitle("MdView");
    }

    @Override
    public void onBackPressed() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initLoc() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                //获取定位信息
                StringBuffer buffer = new StringBuffer();
                buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    aMap.addMarker(getMarkerOptions(amapLocation));
                    // isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(marker));
        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet(String.valueOf(list.size()));
        //设置多少帧刷新一次图片资源
        options.period(60);
        return options;
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                MyToast.setToast(getApplicationContext(), "双击退出应用", 500);
                firstTime = secondTime;
                return true;
            } else {
                finish();
                l.unregisterListener();
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/


    @Override
    public void sendMessageValue(boolean booleanValue) {

        if (booleanValue) {
            tv_state.setText("模拟位置已开始");

            SharedPreferences sp = getSharedPreferences("startTag", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("start", "模拟位置已开始");
            editor.commit();
        }
    }

    //gps 转火星坐标
/*    public AMapLocation fromGpsToAmap(Location location) {
        AMapLocation aMapLocation = new AMapLocation(location);
        CoordinateConverter converter = new CoordinateConverter(getApplicationContext());
        converter.from(CoordinateConverter.CoordType.GPS);
        try {
            converter.coord(new DPoint(location.getLatitude(), location.getLongitude()));
            DPoint desLatLng = converter.convert();
            aMapLocation.setLatitude(desLatLng.getLatitude());
            aMapLocation.setLongitude(desLatLng.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aMapLocation;
    }*/
}

