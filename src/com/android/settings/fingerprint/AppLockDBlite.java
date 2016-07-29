
package com.android.settings.fingerprint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppLockDBlite extends SQLiteOpenHelper {
 
    private static AppLockDBlite mAppLockDBlite;
    private AppLockDBlite(Context context) {
        super(context, AppLockUntil.DBNAME, null, AppLockUntil.VERSION);
    }

    public synchronized static AppLockDBlite getInstance(Context context) {  
        if (mAppLockDBlite == null) {  
            mAppLockDBlite = new AppLockDBlite(context);  
        }  
        return mAppLockDBlite;  
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.e("zhangcy", "-------->AppLockDBlite onCreate");
        db.execSQL("create table if not exists " + AppLockUntil.TNAME + "(" + AppLockUntil.TID
                + " integer primary key autoincrement," + AppLockUntil.PKGNAME + " text,"
                + AppLockUntil.LOCK + " interger);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    public void add(String pkgname, int lockflag) {
        synchronized(this){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AppLockUntil.PKGNAME, pkgname);
            values.put(AppLockUntil.LOCK, lockflag);
            if (!isDateExits(pkgname))
                db.insert(AppLockUntil.TNAME, null, values);
        }
    }

    public void delete(String pkgName) {
        synchronized(this){
            int id = -1;
            id = getExitAppID(pkgName);
            SQLiteDatabase db = getWritableDatabase();
            db.delete(AppLockUntil.TNAME, AppLockUntil.TID + "=" + id, null);
        }
    }

    public Cursor query() {
        synchronized(this){
            SQLiteDatabase db = getWritableDatabase();
            return db.query(AppLockUntil.TNAME, null, null, null, null, null, null);
        }
    }

    public boolean isDateExits(String pkgName) {
        synchronized(this){
            Cursor c = query();
            if(c == null)
                return false;
            while (c.moveToNext()) {
                if (pkgName.equals(c.getString(c.getColumnIndexOrThrow(AppLockUntil.PKGNAME)))) {
                    c.close();
                    return true;
                }
            }
            c.close();
            return false;
       }
    }

    public int getLockFlag(String pkgName) {
        synchronized(this){
            Cursor c = query();
            int lock = 1;
            if(c == null)
                return lock;
            while (c.moveToNext()) {
                if (pkgName.equals(c.getString(c.getColumnIndexOrThrow(AppLockUntil.PKGNAME)))) {
                    lock = c.getInt(c.getColumnIndexOrThrow(AppLockUntil.LOCK));
                    c.close();
                    return lock;
                }
            }
            c.close();
            return lock;
        }
    }

    public int getExitAppID(String pkgName) {
        synchronized(this){
            Cursor c = query();
            if(c == null)
                return -1;
            while (c.moveToNext()) {
                if (pkgName.equals(c.getString(c.getColumnIndexOrThrow(AppLockUntil.PKGNAME)))) {
                    int id = c.getInt(c.getColumnIndexOrThrow(AppLockUntil.TID));
                    c.close();
                    return id;
                }
            }
            c.close();
            return -1;
        }
    }
}
