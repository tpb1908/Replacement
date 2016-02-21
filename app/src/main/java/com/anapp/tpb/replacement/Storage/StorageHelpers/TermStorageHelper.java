package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.Term;

import java.util.ArrayList;

/**
 * Created by Theo on 25/01/2016.
 * TermStorageHelper manages storage of terms throughout the app
 */
public class TermStorageHelper extends StorageHelper {
    private static final String DATABASE_NAME = "Terms";
    private static final int VERSION = 1;

    private static final String TABLE_TERMS = "Terms";
    private static final String KEY_ID = "id";
    private static final String KEY_TERM_NAME = "TermName";
    private static final String KEY_START_DATE = "StartDate";
    private static final String KEY_END_DATE = "EndDate";

    public static final String[] COLUMNS = {KEY_ID, KEY_TERM_NAME, KEY_START_DATE, KEY_END_DATE};

    public TermStorageHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TERM_TABLE = "CREATE TABLE IF NOT EXISTS " +
                DATABASE_NAME +
                "(" + KEY_ID + " Integer PRIMARY KEY AUTOINCREMENT, " +
                KEY_TERM_NAME + " VARCHAR, " +
                KEY_START_DATE + " Integer, " +
                KEY_END_DATE + " Integer)";
        db.execSQL(CREATE_TERM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Dropping table
        db.execSQL("DROP TABLE IF EXISTS Terms");
        //Recreating it with new parameters
        this.onCreate(db);
    }

    public Term addTerm(Term t) {
        Log.d("Adding term", t.toString());
        //Getting a writable database
        SQLiteDatabase db = this.getWritableDatabase();
        //Creating ContentValues to add key
        ContentValues values = new ContentValues();
        values.put(KEY_TERM_NAME, t.getName());
        values.put(KEY_START_DATE, t.getStartDate());
        values.put(KEY_END_DATE, t.getEndDate());


        t.setId((int) db.insert(DATABASE_NAME, null, values));

        return t;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, VERSION, VERSION);
    }

    public Term getTerm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TERMS, //A table
                COLUMNS, //Column names
                "id = ?", //selections
                new String[]{String.valueOf(id)}, //selection arguments
                null, //group by
                null, //having
                null, //order by
                null); //limit
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Term t = new Term();
        t.setId(Integer.parseInt(cursor.getString(0)));
        t.setName(cursor.getString(1));
        t.setStartDate(cursor.getInt(2));
        t.setEndDate(cursor.getInt(3));

        Log.d("Term returned " + id, t.toString());

        return t;
    }

    public ArrayList<Term> getAllTerms() {
        ArrayList<Term> list = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_TERMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Term t;
        if (cursor.moveToFirst()) {
            do {
                t = new Term();
                t.setId(Integer.parseInt(cursor.getString(0)));
                t.setName(cursor.getString(1));
                t.setStartDate(Long.parseLong(cursor.getString(2)));
                t.setEndDate(Long.parseLong(cursor.getString(3)));
                list.add(t);
            } while (cursor.moveToNext());
        }
        Log.d("getAllTerms", list.toString());
        return list;
    }

    public int update(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TERM_NAME, t.getName());
        values.put(KEY_START_DATE, t.getStartDate());
        values.put(KEY_END_DATE, t.getEndDate());

        int i = db.update(TABLE_TERMS,
                values,
                KEY_ID + " = " + t.getId(),
                null);

        db.close();
        return i;
    }

    public void delete(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERMS,
                KEY_ID + " = " + t.getId(),
                null);
        db.close();

        Log.d("deleteSubject", t.toString());
    }
}
