package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Theo on 25/01/2016.
 * http://hmkcode.com/android-simple-sqlite-database-tutorial/
 */
public abstract class StorageHelper extends SQLiteOpenHelper {
    private final int DATABASE_VERSION;
    private final String DATABASE_NAME;

    public StorageHelper(Context context, String databaseName, int version) {
        super(context, databaseName, null, version);
        DATABASE_NAME = databaseName;
        DATABASE_VERSION = version;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
