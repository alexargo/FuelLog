package com.atoennis.fuellog;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class DataProvider extends ContentProvider
{
    private static final String LOG_TAG = DataProvider.class.getSimpleName();

    public DataProvider()
    {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = new FuelTripDbHelper(getContext()).getWritableDatabase();

        int rows = db.delete(FuelTripContract.TripEntry.TABLE_NAME, selection, selectionArgs);

        Log.d(LOG_TAG, String.format("Deleting from uri %s", uri.toString()));
        getContext().getContentResolver().notifyChange(uri, null);

        return rows;
    }

    @Override
    public String getType(Uri uri)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = new FuelTripDbHelper(getContext()).getWritableDatabase();

        long rowId = db.insert(FuelTripContract.TripEntry.TABLE_NAME, null, values);

        Log.d(LOG_TAG, String.format("Inserting from uri %s", uri.toString()));
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, Long.toString(rowId));
    }

    @Override
    public boolean onCreate()
    {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
        String sortOrder)
    {
        SQLiteDatabase db = new FuelTripDbHelper(getContext()).getWritableDatabase();

        Log.d(LOG_TAG, String.format("Querying from uri %s", uri.toString()));

        Cursor cursor = db.query(FuelTripContract.TripEntry.TABLE_NAME, projection, null, null,
            null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
