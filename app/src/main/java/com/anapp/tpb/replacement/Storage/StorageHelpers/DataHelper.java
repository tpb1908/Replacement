package com.anapp.tpb.replacement.Storage.StorageHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;

import java.util.ArrayList;

/**
 * Created by Theo on 20/02/2016.
 */
public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;
    private static final String KEY_ID = "id";

    private static final String TABLE_TERMS = "Terms";
    private static final String KEY_TERM_NAME = "TermName";
    private static final String KEY_START_DATE = "StartDate";
    private static final String KEY_END_DATE = "EndDate";
    public static final String[] TERM_COLUMNS = {KEY_ID, KEY_TERM_NAME, KEY_START_DATE, KEY_END_DATE};

    private static final String TABLE_SUBJECT = "Subjects";
    private static final String KEY_SUBJECT_NAME = "SubjectName";
    private static final String KEY_CLASSROOM = "Classroom";
    private static final String KEY_TEACHER = "Teacher";
    private static final String KEY_COLOR = "Color";
    private static final String[] SUBJECT_COLUMNS = {KEY_ID, KEY_SUBJECT_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    private static final String TABLE_CLASS_TIMES = "ClassTimes";
    private static final String TABLE_SUBJECTS = "Subjects";
    private static final String KEY_SUBJECT_ID = "Subject_ID";
    private static final String KEY_START_TIME = "StartTime";
    private static final String KEY_END_TIME = "EndTime";
    private static final String KEY_DAY = "Day";
    private static final String CLASS_COLUMNS[] = {KEY_ID, KEY_SUBJECT_ID, KEY_START_TIME, KEY_END_TIME, KEY_DAY};

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TERM_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_TERMS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TERM_NAME + " VARCHAR, " +
                KEY_START_DATE + " INTEGER, " +
                KEY_END_DATE + " INTEGER)";
        db.execSQL(CREATE_TERM_TABLE);

        String CREATE_TABLE_SUBJECT = "CREATE TABLE IF NOT EXISTS " +
                TABLE_SUBJECT +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_NAME + " VARCHAR, " +
                KEY_CLASSROOM + " VARCHAR, " +
                KEY_TEACHER + " VARCHAR, " +
                KEY_COLOR + " INTEGER )";
        db.execSQL(CREATE_TABLE_SUBJECT);

        String CREATE_TABLE_CLASS_TIMES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_CLASS_TIMES +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                KEY_START_TIME + " INTEGER, " +
                KEY_END_TIME + " INTEGER, " +
                KEY_DAY + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";
        db.execSQL(CREATE_TABLE_CLASS_TIMES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Term addTerm(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TERM_NAME, t.getName());
        values.put(KEY_START_DATE, t.getStartDate());
        values.put(KEY_END_DATE, t.getEndDate());
        t.setId((int) db.insert(DATABASE_NAME, null, values));
        Log.d("Data", "Adding term with values of " + t.toString());
        return t;
    }

    public Term getTerm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TERMS,
                TERM_COLUMNS,
                "id = ?", //selections
                new String[]{String.valueOf(id)},
                null, //group by
                null, //having
                null, //order by
                null); //limit
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Term t = new Term();
        t.setId(cursor.getInt(0));
        t.setName(cursor.getString(1));
        t.setStartDate(cursor.getInt(2));
        t.setEndDate(cursor.getInt(3));
        Log.d("Data ", "Reading term with values of " + t.toString());

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
        Log.d("Data", "Returning all terms " + list.toString());
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
        Log.d("Data", "Updating term to values of " + t.toString());
        return i;
    }

    public void delete(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERMS,
                KEY_ID + " = " + t.getId(),
                null);
        db.close();

        Log.d("Data", "Deleting term with values of" + t.toString());
    }

    //End term methods

    public Subject addSubject(Subject s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, s.getName());
        values.put(KEY_CLASSROOM, s.getClassroom());
        values.put(KEY_TEACHER, s.getTeacher());
        values.put(KEY_COLOR, s.getColor());

        s.setId((int) db.insert(TABLE_SUBJECT, null, values));
        Log.d("Data", "Adding term with values of " + s.toString());
        return s;
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

        Subject s = new Subject();
        s.setId(Integer.parseInt(cursor.getString(0)));
        s.setName(cursor.getString(1));
        s.setClassroom(cursor.getString(2));
        s.setTeacher(cursor.getString(3));
        s.setColor(cursor.getInt(4));

        Log.d("Data", "Reading subject with values of " + s.toString());

        return s;
    }

    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SUBJECT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Subject s;
        if (cursor.moveToFirst()) {
            do {
                s = new Subject();
                s.setId(Integer.parseInt(cursor.getString(0)));
                s.setName(cursor.getString(1));
                s.setClassroom(cursor.getString(2));
                s.setTeacher(cursor.getString(3));
                s.setColor(cursor.getInt(4));
                subjects.add(s);
            } while (cursor.moveToNext());
        }
        Log.d("Data", "Getting all subjects" + subjects.toString());
        return subjects;
    }

    public int updateSubject(Subject s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, s.getName());
        values.put(KEY_CLASSROOM, s.getClassroom());
        values.put(KEY_TEACHER, s.getTeacher());
        values.put(KEY_COLOR, s.getColor());

        int i = db.update(TABLE_SUBJECT,
                values,
                KEY_ID + " = " + s.getId(),
                null);

        db.close();
        Log.d("Data", "Updating subject to values of" + s.toString());
        return i;
    }

    public void deleteSubject(Subject s) {
        String query = "SELECT * FROM " + TABLE_CLASS_TIMES;
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Data", "Deleting subject with values of " + s.toString());
        int numClass = db.delete(TABLE_CLASS_TIMES,
                KEY_SUBJECT_ID + " = " + s.getId(),
                null);
        Log.d("Data", "Deletion of subject resulted in deletion of " + numClass + " classes");

        db.delete(TABLE_SUBJECT,
                KEY_ID + " = " + s.getId(),
                null);
        db.close();
    }

    //End subject methods

    public ClassTime addClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_ID, c.getSubjectID());
        values.put(KEY_START_TIME, c.getStart());
        values.put(KEY_END_TIME, c.getEnd());
        values.put(KEY_DAY, c.getDay());

        c.setId((int) db.insert(TABLE_CLASS_TIMES, null, values));
        Log.d("Data", "Adding class with values of " + c.toString());
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

        Log.d("Data", "Returning class with values of " + c.toString());

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
        Log.d("Data", "Returning all classes" + classes.toString());
        return classes;
    }

    public int updateClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_ID, c.getSubjectID());
        values.put(KEY_START_TIME, c.getStart());
        values.put(KEY_END_TIME, c.getEnd());
        values.put(KEY_DAY, c.getDay());

        int i = db.update(TABLE_CLASS_TIMES,
                values,
                KEY_ID + " = " + c.getId(),
                null);

        db.close();
        Log.d("Data", "Updating class to values of " + c.toString());
        return i;
    }

    public void deleteClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_TIMES,
                KEY_ID + " = " + c.getId(),
                null);
        db.close();
        Log.d("Data", "Deleting class with values of " + c.toString());
    }
}
