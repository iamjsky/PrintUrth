package com.printurth.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    public long insertColumn(String id, String fileName, String title , String thumbImgName ,
                             String makerName , String uploadAt , String level , String time ,
                             String filament , String description, int isfin, int like, String lastdate){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ID, id);
        values.put(DataBases.CreateDB.FILENAME, fileName);
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.THUMBIMGNAME, thumbImgName);
        values.put(DataBases.CreateDB.MAKERNAME, makerName);
        values.put(DataBases.CreateDB.UPLOADAT, uploadAt);
        values.put(DataBases.CreateDB.LEVEL, level);
        values.put(DataBases.CreateDB.TIME, time);
        values.put(DataBases.CreateDB.FILAMENT, filament);
        values.put(DataBases.CreateDB.DESCRIPTION, description);
        values.put(DataBases.CreateDB.ISFIN, isfin);
        values.put(DataBases.CreateDB.LIKE, like);
        values.put(DataBases.CreateDB.LASTDATE, lastdate);
        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }
    public boolean updateColumnLastdate(String id, String lastdate){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ID, id);

        values.put(DataBases.CreateDB.LASTDATE, lastdate);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }
    public boolean updateColumnLike(String id, int like){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ID, id);

        values.put(DataBases.CreateDB.LIKE, like);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }
    public boolean updateColumnFin(String id, int isfin){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ID, id);

        values.put(DataBases.CreateDB.ISFIN, isfin);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }
    public boolean updateColumn(String id, String fileName, String title , String thumbImgName ,
                                String makerName , String uploadAt , String level , String time ,
                                String filament , String description, int isfin, int like, String lastdate){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ID, id);
        values.put(DataBases.CreateDB.FILENAME, fileName);
        values.put(DataBases.CreateDB.TITLE, title);
        values.put(DataBases.CreateDB.THUMBIMGNAME, thumbImgName);
        values.put(DataBases.CreateDB.MAKERNAME, makerName);
        values.put(DataBases.CreateDB.UPLOADAT, uploadAt);
        values.put(DataBases.CreateDB.LEVEL, level);
        values.put(DataBases.CreateDB.TIME, time);
        values.put(DataBases.CreateDB.FILAMENT, filament);
        values.put(DataBases.CreateDB.DESCRIPTION, description);
        values.put(DataBases.CreateDB.ISFIN, isfin);
        values.put(DataBases.CreateDB.LIKE, like);
        values.put(DataBases.CreateDB.LASTDATE, lastdate);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
}