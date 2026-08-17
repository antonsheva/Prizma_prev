package com.eshelon.prizma_prev;

import static com.eshelon.prizma_prev.C_.DB_VERSION;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.eshelon.prizma_prev.objects.JmmrState;

import java.util.ArrayList;

public class Db extends SQLiteOpenHelper {
    public static final int db_version = DB_VERSION;

    public  static final String TABLE_NAME       = "pattern";
    public  static final String PATT_ID         = "id";
    public  static final String PATT_NAME       = "name";
    public  static final String PATT_RANGE      = "range";
    public  static final String PATT_MASK1      = "msk1";
    public  static final String PATT_MASK2      = "msk2";
    public  static final String PATT_MOD_CODE1  = "mc1";
    public  static final String PATT_MOD_CODE2  = "mc2";
    public  static final String PATT_PWR1       = "pwr1";
    public  static final String PATT_PWR2       = "pwr2";


    private static final String DB_PATH = "app/src/main/assets";
    public  static final String DB_NAME ="band_pattern";


    private static final String CREATE_TABLE="create table if not exists "+
            TABLE_NAME + "(" +
            PATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
            PATT_NAME      + " TEXT,"+
            PATT_RANGE     + " INTEGER,"+
            PATT_MASK1     + " INTEGER, " +
            PATT_MASK2     + " INTEGER,"+
            PATT_MOD_CODE1 + " INTEGER, " +
            PATT_MOD_CODE2 + " INTEGER,"+
            PATT_PWR1      + " INTEGER, " +
            PATT_PWR2      + " INTEGER);";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;


    public Db(@Nullable Context context, @Nullable String str, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, db_version);
        Log.i("MY_TEG", "Db constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("MY_TEG", "Db onCreate");
        try{
            db.execSQL(CREATE_TABLE);
            Log.i("MY_TEG", "Create table - OK");
        }catch (Exception e){
            Log.e("MY_TEG", "Error create table");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
    public boolean insertPattern(JmmrState jmmr){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PATT_NAME, jmmr.patt_name);
        cv.put(PATT_RANGE, jmmr.dev_range);
        cv.put(PATT_MASK1, jmmr.msk1);
        cv.put(PATT_MASK2, jmmr.msk2);
        cv.put(PATT_MOD_CODE1, jmmr.mc1);
        cv.put(PATT_MOD_CODE2, jmmr.mc2);
        cv.put(PATT_PWR1, jmmr.pwr1);
        cv.put(PATT_PWR2, jmmr.pwr2);
        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1)return false;
        else            return true;
    }
    public ArrayList<JmmrState>readDataFromDb(){
        ArrayList<JmmrState>tmpArray = new ArrayList<>();
        JmmrState jmmr;
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        int pattQty = cursor.getCount();
        cursor.moveToFirst();
        for(int i=0; i<pattQty; i++){
            jmmr = new JmmrState();

            int ind = cursor.getColumnIndex(PATT_NAME);
            if(ind != -1)jmmr.patt_name = cursor.getString(ind);

            ind = cursor.getColumnIndex(PATT_RANGE);
            if(ind != -1)jmmr.dev_range = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_MASK1);
            if(ind != -1)jmmr.msk1 = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_MASK2);
            if(ind != -1)jmmr.msk2 = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_MOD_CODE1);
            if(ind != -1)jmmr.mc1 = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_MOD_CODE2);
            if(ind != -1)jmmr.mc2 = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_PWR1);
            if(ind != -1)jmmr.pwr1 = cursor.getInt(ind);

            ind = cursor.getColumnIndex(PATT_PWR2);
            if(ind != -1)jmmr.pwr2 = cursor.getInt(ind);

            tmpArray.add(jmmr);
            cursor.moveToNext();
        }
        return tmpArray;
    }
}
