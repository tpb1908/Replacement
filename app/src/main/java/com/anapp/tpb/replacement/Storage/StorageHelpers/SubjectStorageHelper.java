package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

/**
 * Created by Theo on 26/01/2016.
 */
public class SubjectStorageHelper extends StorageHelper {
    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;

    private static final String TABLE_SUBJECT = "Subjects";
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT_NAME = "SubjectName";
    private static final String KEY_CLASSROOM = "Classroom";
    private static final String KEY_TEACHER = "Teacher";
    private static final String KEY_COLOR = "Color";

    public static final String[] COLUMNS = {KEY_ID, KEY_SUBJECT_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    public SubjectStorageHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SUBJECT = "CREATE TABLE IF NOT EXISTS " +
                TABLE_SUBJECT +
                "(" + KEY_ID + " Integer PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_NAME + " VARCHAR, " +
                KEY_CLASSROOM + " VARCHAR, " +
                KEY_TEACHER + " VARCHAR, " +
                KEY_COLOR + " Integer )";
        db.execSQL(CREATE_TABLE_SUBJECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);

        this.onCreate(db);
    }

    public Subject addSubject(Subject l) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, l.getName());
        values.put(KEY_CLASSROOM, l.getClassroom());
        values.put(KEY_TEACHER, l.getTeacher());
        values.put(KEY_COLOR, l.getColor());

        l.setId((int) db.insert(TABLE_SUBJECT, null, values));

        return l;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, VERSION, VERSION);
    }

    public Subject getSubject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_SUBJECT,
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

        Subject l = new Subject();
        l.setId(Integer.parseInt(cursor.getString(0)));
        l.setName(cursor.getString(1));
        l.setClassroom(cursor.getString(2));
        l.setTeacher(cursor.getString(3));
        l.setColor(cursor.getInt(4));

        Log.d("Subject returned " + id, l.toString());

        return l;
    }

    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SUBJECT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Subject l;
        if (cursor.moveToFirst()) {
            do {
                l = new Subject();
                l.setId(Integer.parseInt(cursor.getString(0)));
                l.setName(cursor.getString(1));
                l.setClassroom(cursor.getString(2));
                l.setTeacher(cursor.getString(3));
                l.setColor(cursor.getInt(4));
                subjects.add(l);
            } while (cursor.moveToNext());
        }
        Log.d("getAllSubjects", subjects.toString());
        return subjects;
    }

    public int updateSubject(Subject l) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, l.getName());
        values.put(KEY_CLASSROOM, l.getClassroom());
        values.put(KEY_TEACHER, l.getTeacher());
        values.put(KEY_COLOR, l.getColor());

        int i = db.update(TABLE_SUBJECT,
                values,
                KEY_ID + " = " + l.getId(),
                null);

        db.close();
        return i;
    }

    public void deleteSubject(Subject l) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECT,
                KEY_ID + " = " + l.getId(),
                null);
        db.close();
        Log.d("Delete", l.toString());
    }
}
