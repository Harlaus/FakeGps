//package name.caiyao.fakegps.ui;
//
//import android.content.Context;
//import android.os.Bundle;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import com.amap.api.maps2d.AMap;
//import com.amap.api.maps2d.AMap.OnMarkerClickListener;
//import com.amap.api.maps2d.CameraUpdateFactory;
//import com.amap.api.maps2d.MapView;
//import com.amap.api.maps2d.model.BitmapDescriptorFactory;
//import com.amap.api.maps2d.model.LatLng;
//import com.amap.api.maps2d.model.Marker;
//import com.amap.api.maps2d.model.MarkerOptions;
//import com.amap.api.services.core.AMapException;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.geocoder.GeocodeResult;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
//import com.amap.api.services.geocoder.RegeocodeAddress;
//import com.amap.api.services.geocoder.RegeocodeQuery;
//import com.amap.api.services.geocoder.RegeocodeResult;
//import android.app.Activity;
//import android.app.ProgressDialog;
//
//import android.view.View.OnClickListener;
//
//
//public class ReGeocoder  implements
//        OnGeocodeSearchListener {
//    static public String addressName;
//    private static GeocodeSearch geocoderSearch;
//    public static void initReGeo(Context context, double[] latlng, final OnReGeoCallback callback){
//    /**
//     * 响应逆地理编码
//     */
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//        public void getAddress(final LatLonPoint latLonPoint) {
//        //showDialog();
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
//    }
//
//    /**
//     * 地理编码查询回调
//     */
//    @Override
//    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//    }
//
//    /**
//     * 逆地理编码回调
//     */
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                addressName = result.getRegeocodeAddress().getFormatAddress();
////                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
////                        AMapUtil.convertToLatLng(latLonPoint), 15));
////                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
//                ToastUtil.show(ReGeocoder.this, addressName);
//            } else {
//               // ToastUtil.show(ReGeocoderActivity.this, "gg");
//            }
//        } else {
//           // ToastUtil.showerror(this, rCode);
//        }
//    }
//    }
//
//public interface OnReGeoCallback{
//    void onSuccess(String addr);
//    void onError(String e);
//}
//
//}