//package name.caiyao.fakegps.ui;
//
//import android.content.Context;
//import com.amap.api.services.core.PoiItem;
//import com.amap.api.services.poisearch.PoiResult;
//import com.amap.api.services.poisearch.PoiSearch;
//import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
//import com.amap.api.services.poisearch.PoiSearch.Query;
//import java.util.ArrayList;
//
//
//public class PoiSearchTask implements OnPoiSearchListener {
//    private Context mContext;
//    private RecomandAdapter mRecommandAdapter;
//    public PoiSearchTask(Context context, RecomandAdapter recomandAdapter) {
//        mContext = context;
//        mRecommandAdapter = recomandAdapter;
//    }
//    public void search(String keyWord, String city) {
//        Query query = new PoiSearch.Query(keyWord, "", city);
//        query.setPageSize(10);
//        query.setPageNum(0);
//        PoiSearch poiSearch = new PoiSearch(mContext, query);
//        poiSearch.setOnPoiSearchListener(this);
//        poiSearch.searchPOIAsyn();
//    }
//    @Override
//    public void onPoiSearched(PoiResult poiResult, int resultCode) {
//        if (resultCode == 1000 && poiResult != null) {
//            ArrayList<PoiItem> pois=poiResult.getPois();
//            if(pois==null){
//                return;
//            }
//            List<PositionEntity> entities=new ArrayList<>();
//            for(PoiItem poiItem:pois){
//                PositionEntity entity=new PositionEntity(poiItem.getLatLonPoint().getLatitude(),
//                        poiItem.getLatLonPoint().getLongitude(),
//                        poiItem.getTitle(),poiItem.getCityName());
//                entities.add(entity);
//            }
//            mRecommandAdapter.setPositionEntities(entities);
//            mRecommandAdapter.notifyDataSetChanged();
//        }
//        //TODO 可以根据app自身需求对查询错误情况进行相应的提示或者逻辑处理
//    }
//    @Override
//    public void onPoiItemSearched(PoiItem poiItem, int i) {}}