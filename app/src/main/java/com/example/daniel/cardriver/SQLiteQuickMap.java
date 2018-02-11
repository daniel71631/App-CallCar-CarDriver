package com.example.daniel.cardriver;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/9/10.
 */

public class SQLiteQuickMap extends SQLiteOpenHelper {

    private final static String DB_NAME = "db_quickmap.db";
    private final static int VERSION = 1;
    public SQLiteDatabase dbquickmap;

    public SQLiteQuickMap(Context context) {
        super(context, DB_NAME, null, VERSION);
        dbquickmap = this.getReadableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists tb_quickmap(_id integer primary key autoincrement ,casecid,casestyle,clientname, clientlocation, clientlocationLa, clientlocationLng, clientDestination)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists tb_quickmap");
            onCreate(db);
        }
    }

    public Cursor selectCusor(String sql, String[] selectionArgs) {
        return dbquickmap.rawQuery(sql, selectionArgs);
    }

    public int selectCount(String sql, String[] selectionArgs) {
        Cursor cursor = dbquickmap.rawQuery(sql, selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

    public List<Map<String, Object>> selectList(String sql, String[] selectionArgs) {
        Cursor cursor = dbquickmap.rawQuery(sql, selectionArgs);
        return cursorToList(cursor);
    }

    public List<Map<String, Object>> cursorToList(Cursor cursor) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        String[] arrColumnName = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();

            for (int i = 0; i < arrColumnName.length; i++) {
                Object cols_value = cursor.getString(i);
                map.put(arrColumnName[i], cols_value);
            }
            list.add(map);
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public boolean execData(String sql, Object[] bindArgs) {
        try {
            dbquickmap.execSQL(sql, bindArgs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void destroy() {
        if (dbquickmap != null) {
            dbquickmap.close();
        }
    }

    public void deleteAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete("tb_quickmap", null, null);
    }
}
