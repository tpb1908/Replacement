package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.Lesson;

import java.util.ArrayList;

/**
 * Created by Theo on 26/01/2016.
 */
public class LessonStorageHelper extends StorageHelper {
    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;

    private static final String TABLE_LESSONS = "Lessons";
    private static final String KEY_ID = "id";
    private static final String KEY_LESSON_NAME = "LessonName";
    private static final String KEY_CLASSROOM = "Classroom";
    private static final String KEY_TEACHER = "Teacher";
    private static final String KEY_COLOR = "Color";

    public static final String[] COLUMNS = {KEY_ID, KEY_LESSON_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    public LessonStorageHelper(Context context) {
        super(context, DATABASE_NAME, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_LESSONS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_LESSONS +
                "(" + KEY_ID + " Integer PRIMARY KEY AUTOINCREMENT, " +
                KEY_LESSON_NAME + " VARCHAR, " +
                KEY_CLASSROOM + " VARCHAR, " +
                KEY_TEACHER + " VARCHAR, " +
                KEY_COLOR + " Integer)";
        db.execSQL(CREATE_TABLE_LESSONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);

        this.onCreate(db);
    }

    public Lesson addLesson(Lesson l) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LESSON_NAME, l.getName());
        values.put(KEY_CLASSROOM, l.getClassroom());
        values.put(KEY_TEACHER, l.getTeacher());
        values.put(KEY_COLOR, l.getColor());

        l.setId((int) db.insert(TABLE_LESSONS, null, values));

        return l;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, VERSION, VERSION);
    }

    public Lesson getLesson(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_LESSONS,
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

        Lesson l = new Lesson();
        l.setId(Integer.parseInt(cursor.getString(0)));
        l.setName(cursor.getString(1));
        l.setClassroom(cursor.getString(2));
        l.setTeacher(cursor.getString(3));
        l.setColor(cursor.getInt(4));

        Log.d("Lesson returned " + id, l.toString());

        return l;
    }

    public ArrayList<Lesson> getAllLessons() {
        ArrayList<Lesson> lessons = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_LESSONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Lesson l;
        if (cursor.moveToFirst()) {
            do {
                l = new Lesson();
                l.setId(Integer.parseInt(cursor.getString(0)));
                l.setName(cursor.getString(1));
                l.setClassroom(cursor.getString(2));
                l.setTeacher(cursor.getString(3));
                l.setColor(cursor.getInt(4));
                lessons.add(l);
            } while (cursor.moveToNext());
        }
        Log.d("getAllLessons", lessons.toString());
        return lessons;
    }

    public int update(Lesson l) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LESSON_NAME, l.getName());
        values.put(KEY_CLASSROOM, l.getClassroom());
        values.put(KEY_TEACHER, l.getTeacher());
        values.put(KEY_COLOR, l.getColor());

        int i = db.update(TABLE_LESSONS,
                values,
                KEY_ID + " = " + l.getId(),
                null);

        db.close();
        return i;
    }

    public void delete(Lesson l) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LESSONS,
                KEY_ID + " = " + l.getId(),
                null);
        db.close();
        Log.d("Delete", l.toString());
    }


}
