package com.binarycraft.acland.datautil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.binarycraft.acland.entity.Mouza;
import com.binarycraft.acland.entity.Union;

import java.util.Vector;

/**
 * Created by user pc on 3/28/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ACLand.db";

    private static final int DATABASE_VERSION = 1;

    /* Table Names */
    private static final String TABLE_MOUZA = "t_mouza";
    private static final String TABLE_UNION = "t_union";

    /* Column Names */
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_UID = "u_id";

    private static final String CREATE_TABLE_MOUZA = "CREATE TABLE "
            + TABLE_MOUZA + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_UID + " TEXT" + ")";

    private static final String CREATE_TABLE_UNION = "CREATE TABLE "
            + TABLE_UNION + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME
            + " TEXT" + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MOUZA);
        db.execSQL(CREATE_TABLE_UNION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOUZA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNION);
    }

    /*
    * Create a Mouza
    */
    public long createMouza(Mouza mouza) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, mouza.getId());
        values.put(KEY_NAME, mouza.getName());
        values.put(KEY_UID, mouza.getUnionId());

        // insert row
        long mouza_id = db.insert(TABLE_MOUZA, null, values);

        return mouza_id;
    }

    /*
    * Create a Union
    */
    public long createUnion(Union union) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, union.getId());
        values.put(KEY_NAME, union.getName());

        // insert row
        long union_id = db.insert(TABLE_UNION, null, values);

        return union_id;
    }

    /*
    * getting all mouzas
    * */
    public Vector<Mouza> getAllMouzas(String uId) {
        Vector<Mouza> mouzas = new Vector<Mouza>();
        String selectQuery = "";
        if(TextUtils.isEmpty(uId)) {
            selectQuery = "SELECT * FROM " + TABLE_MOUZA;
        }else{
            selectQuery = "SELECT * FROM " + TABLE_MOUZA + " WHERE " + KEY_UID + " = '" + uId + "'";
        }
        Log.e("QUERY", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Mouza mouza = new Mouza();
                mouza.setId(c.getString((c.getColumnIndex(KEY_ID))));
                mouza.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                mouza.setUnionId(c.getString(c.getColumnIndex(KEY_UID)));

                // adding to Mouza list
                mouzas.add(mouza);
            } while (c.moveToNext());
        }

        return mouzas;
    }

    /*
    * getting all unions
    * */
    public Vector<Union> getAllUnions() {
        Vector<Union> unions = new Vector<Union>();
        String selectQuery = "SELECT  * FROM " + TABLE_UNION;

        Log.e("QUERY", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Union union = new Union();
                union.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                union.setId(c.getString(c.getColumnIndex(KEY_ID)));

                // adding to union list
                unions.add(union);
            } while (c.moveToNext());
        }

        return unions;
    }

    public boolean checkDB(){
        String selectQuery = "SELECT  count(*) FROM " + TABLE_UNION;

        Log.e("QUERY", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            int icount = c.getInt(0);
            if(icount>0){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public String getMouzaId(String mouzaName) {
        String mouzaId = "";
        String selectQuery = "SELECT "+KEY_ID+" FROM " + TABLE_MOUZA+" WHERE "+KEY_NAME+" = '"+mouzaName+"'";

        Log.e("QUERY", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            mouzaId = c.getString(0);


        }
        return mouzaId;
    }
}
