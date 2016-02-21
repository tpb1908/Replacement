package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;

import java.util.ArrayList;

/**
 * Created by Theo on 20/02/2016.
 */
public class ClassTimeStorageHelper extends StorageHelper {
    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;

    private static final String TABLE_CLASS_TIMES = "ClassTimes";
    private static final String TABLE_SUBJECTS = "Subjects";
    private static final String KEY_ID = "id";
    private static final String KEY_LESSON_ID = "Subject_ID";
    private static final String KEY_START_TIME = "StartTime";
    private static final String KEY_END_TIME = "EndTime";
    private static final String KEY_DAY = "Day";

    public static final String[] COLUMNS = {KEY_ID, KEY_LESSON_ID, KEY_START_TIME, KEY_END_TIME, KEY_DAY};
    


    public ClassTimeStorageHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //http://stackoverflow.com/questions/7304888/oncreate-not-being-called-after-getwritabledatabase-getreadabledatabase May be relevant
        Log.d("OnCreate", "Method called");
        try {
            String CREATE_TABLE_CLASS_TIMES = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_CLASS_TIMES +
                    "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_LESSON_ID + " INTEGER, " +
                    "FOREIGN KEY(" + KEY_LESSON_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(id), " +
                    KEY_START_TIME + " INTEGER, " +
                    KEY_END_TIME + " INTEGER, " +
                    KEY_DAY + " INTEGER );";
            db.execSQL(CREATE_TABLE_CLASS_TIMES);
        } catch (Exception e) {
            Log.d("Exception ", "" + e.getStackTrace());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_TIMES);
        this.onCreate(db);
    }

    public ClassTime addClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LESSON_ID, c.getSubjectID());
        values.put(KEY_START_TIME, c.getStart());
        values.put(KEY_END_TIME, c.getEnd());
        values.put(KEY_DAY, c.getDay());

        c.setId((int) db.insert(TABLE_CLASS_TIMES, null, values));

        return c;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, VERSION, VERSION);
    }

    public ClassTime getClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_CLASS_TIMES,
                COLUMNS,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        ClassTime c = new ClassTime();
        c.setId(Integer.parseInt(cursor.getString(0)));
        c.setSubjectID(cursor.getInt(1));
        c.setStart(cursor.getInt(2));
        c.setEnd(cursor.getInt(3));
        c.setDay(cursor.getInt(4));

        Log.d("ClassTime returned" + id, c.toString());

        return c;
    }

    public ArrayList<ClassTime> getAllClasses() {
        ArrayList<ClassTime> classes = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CLASS_TIMES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ClassTime c;
        if (cursor.moveToFirst()) {
            do {
                c = new ClassTime();
                c.setId(Integer.parseInt(cursor.getString(0)));
                c.setSubjectID(cursor.getInt(1));
                c.setStart(cursor.getInt(2));
                c.setEnd(cursor.getInt(3));
                c.setDay(cursor.getInt(4));
                classes.add(c);
            } while (cursor.moveToNext());
        }
        Log.d("getAllClasses", classes.toString());
        return classes;
    }

    public int updateClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LESSON_ID, c.getSubjectID());
        values.put(KEY_START_TIME, c.getStart());
        values.put(KEY_END_TIME, c.getEnd());
        values.put(KEY_DAY, c.getDay());

        int i = db.update(TABLE_CLASS_TIMES,
                values,
                KEY_ID + " = " + c.getId(),
                null);

        db.close();
        return i;
    }

    public void deleteClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_TIMES,
                KEY_ID + " = " + c.getId(),
                null);
        db.close();
        Log.d("Delete", c.toString());
    }

}