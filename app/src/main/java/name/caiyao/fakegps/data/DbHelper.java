package name.caiyao.fakegps.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
public class DbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "applist.db";
    public final static String APP_TABLE_NAME = "app";
    private final static int DB_VERSION = 1;
    public final static String APP_TEMP_NAME = "temp";
    public Context context;
    private SQLiteDatabase mdb;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEMP_TABLE = "CREATE TABLE IF NOT EXISTS " + APP_TEMP_NAME + "(id integer PRIMARY KEY  autoincrement," + "latitude DOUBLE,longitude DOUBLE,lac Integer,cid Integer,addname varchar(80))";
        mdb.execSQL(CREATE_TEMP_TABLE);
        String CREATE_APP_TABLE = "CREATE TABLE IF NOT EXISTS " + APP_TABLE_NAME + "(package_name TEXT PRIMARY KEY," + "latitude DOUBLE,longitude DOUBLE,lac Integer,cid Integer,addname varchar(80))";
        mdb.execSQL(CREATE_APP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {

        if (!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "请插入手机内存卡", Toast.LENGTH_SHORT);
        } else {

            String dbPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/database";
            String dbFile = dbPath + "/applist.db";

            File dbp = new File(dbPath);
            if (!dbp.exists()) {
                dbp.mkdir();
            }
            File dbf = new File(dbFile);

            if (!dbf.exists()) {
                try {
                    dbf.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mdb = SQLiteDatabase.openOrCreateDatabase(dbf, null);

        }

        return  mdb;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {

        if (!android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "请插入手机内存卡", Toast.LENGTH_SHORT);
        } else {

            String dbPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/database";
            String dbFile = dbPath + "/applist.db";

            File dbp = new File(dbPath);
            if (!dbp.exists()) {
                dbp.mkdir();
            }
            File dbf = new File(dbFile);

            if (!dbf.exists()) {
                try {
                    dbf.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mdb = SQLiteDatabase.openOrCreateDatabase(dbf, null);

        }

        return  mdb;
    }
}
