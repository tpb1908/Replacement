package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

/**
 * Created by Theo on 20/02/2016.
 */
public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;
    private static final String KEY_ID = "id";

    private static final String TABLE_SUBJECT = "Subjects";
    private static final String KEY_SUBJECT_NAME = "SubjectName";
    private static final String KEY_CLASSROOM = "Classroom";
    private static final String KEY_TEACHER = "Teacher";
    private static final String KEY_COLOR = "Color";
    private static final String[] SUBJECT_COLUMNS = {KEY_ID, KEY_SUBJECT_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    private static final String TABLE_CLASS_TIMES = "ClassTimes";
    private static final String TABLE_SUBJECTS = "Subjects";
    private static final String KEY_LESSON_ID = "Subject_ID";
    private static final String KEY_START_TIME = "StartTime";
    private static final String KEY_END_TIME = "EndTime";
    private static final String KEY_DAY = "Day";
    private static final String CLASS_COLUMNS[] = {KEY_ID, KEY_LESSON_ID, KEY_START_TIME, KEY_END_TIME, KEY_DAY};

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
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

        String CREATE_TABLE_CLASS_TIMES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_CLASS_TIMES +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LESSON_ID + " INTEGER, " +
                KEY_START_TIME + " INTEGER, " +
                KEY_END_TIME + " INTEGER, " +
                KEY_DAY + " INTEGER, " +
                "FOREIGN KEY(" + KEY_LESSON_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(id)) ";
        db.execSQL(CREATE_TABLE_CLASS_TIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
                SUBJECT_COLUMNS,
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

    //End subject methods

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

    public ClassTime getClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_CLASS_TIMES,
                CLASS_COLUMNS,
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
