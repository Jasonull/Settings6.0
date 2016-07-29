
package com.android.settings.fingerprint;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AppLockProvider extends ContentProvider {
    private static UriMatcher sUriMatcher;

    private AppLockDBlite mAppLockDBlite;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AppLockUntil.AUTHORITY, "lockapp", AppLockUntil.LOCKAPP);
        sUriMatcher.addURI(AppLockUntil.AUTHORITY, "lockapp/#", AppLockUntil.LOCKAPP_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mAppLockDBlite.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case AppLockUntil.LOCKAPP:
                count = db.delete(AppLockUntil.TNAME, selection, selectionArgs);
                break;

            case AppLockUntil.LOCKAPP_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db
                        .delete(AppLockUntil.TNAME,
                                AppLockUntil.TID
                                        + "="
                                        + noteId
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                                + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        switch (sUriMatcher.match(uri)) {
            case AppLockUntil.LOCKAPP:
                return AppLockUntil.CONTENT_TYPE;
            case AppLockUntil.LOCKAPP_ID:
                return AppLockUntil.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        if (sUriMatcher.match(uri) != AppLockUntil.LOCKAPP) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mAppLockDBlite.getWritableDatabase();
        long rowId = db.insert(AppLockUntil.TNAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(AppLockUntil.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mAppLockDBlite = AppLockDBlite.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        // TODO Auto-generated method stub
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(AppLockUntil.TNAME);

        switch (sUriMatcher.match(uri)) {
            case AppLockUntil.LOCKAPP:
                break;

            case AppLockUntil.LOCKAPP_ID:
                qb.appendWhere(AppLockUntil.TID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mAppLockDBlite.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        SQLiteDatabase db = mAppLockDBlite.getWritableDatabase();
        int count = 0;
        Log.e("zhangcy","----------->cccc");
        switch (sUriMatcher.match(uri)) {
            case AppLockUntil.LOCKAPP:
                Log.e("zhangcy","----------->bbbbb");
                db.update(AppLockUntil.TNAME, values, selection, selectionArgs);
                break;

            case AppLockUntil.LOCKAPP_ID:
                Log.e("zhangcy","----------->aa");
                long id = ContentUris.parseId(uri);
                String where = AppLockUntil.TID + id;
                if ((selection != null) && (!"".equals(selection))) {
                    where += " and " + selection;
                }
                count = db.update(AppLockUntil.TNAME, values, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Log.e("zhangcy","----------->fff  count = " + count);
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
