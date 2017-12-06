package name.caiyao.fakegps.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amap.api.location.DPoint;

import java.util.ArrayList;
import java.util.List;
import name.caiyao.fakegps.bean.Address;
import name.caiyao.fakegps.data.DbHelper;

/**
 * Created by sky on 2017/3/10.
 */

public class TempDao {
    private String sql;
    DbHelper dbHelper;
    private List<String> mList = new ArrayList<String>();
    private Context mContext;

    public TempDao(Context context) {
        dbHelper = new DbHelper(context);
        this.mContext = context;
    }


    //数据库插入地理位置
    public long insertAdd(Address address) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        dbHelper.onCreate(db);
        long count = 0;
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("latitude", address.getLatitude());
        values.put("longitude", address.getLongitude());
        values.put("lac", address.getLac());
        values.put("cid", address.getCid());
        values.put("addname", address.getAddname());

        try {
            count = db.insert("temp", null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return count;
    }

    public void deleteOnClick(int index) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (index > 0) {
            //delete from temp where id= (select id from temp limit 5,1）   删除第6条数据
            sql = "delete from temp where id= (select id from temp limit " + index + ",1)";
        } else {

            sql = "delete from temp where id= (select id from temp limit " + index + ")";
        }
        db.beginTransaction();

        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    //删除所有数据
    public void deleteTable() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        String sql = "drop table temp";
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void deleteLast(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        String sql="delete from temp where  id=(select * from  temp  order  by  id  desc  limit  1)";

    }

    public List<String> selectAllData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.beginTransaction();
        String sql = "select addname from temp ";
        try {
            Cursor cursor = db.rawQuery(sql, null);
            db.setTransactionSuccessful();
            while (cursor.moveToNext()) {
                String addName = cursor.getString(cursor.getColumnIndex("addname"));
                mList.add(addName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return mList;
    }

}
