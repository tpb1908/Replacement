package com.anapp.tpb.replacement.Storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Theo on 20/02/2016.
 */
public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Work";
    private static final int VERSION = 1;
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT_ID = "Subject_ID";

    private static final String TABLE_TERMS = "Terms";
    private static final String KEY_TERM_NAME = "TermName";
    private static final String KEY_START_DATE = "StartDate";
    private static final String KEY_END_DATE = "EndDate";
    private static final String[] TERM_COLUMNS = {KEY_ID, KEY_TERM_NAME, KEY_START_DATE, KEY_END_DATE};

    private static final String TABLE_SUBJECTS = "Subjects";
    private static final String KEY_SUBJECT_NAME = "SubjectName";
    private static final String KEY_CLASSROOM = "Classroom";
    private static final String KEY_TEACHER = "Teacher";
    private static final String KEY_COLOR = "Color";
    private static final String[] SUBJECT_COLUMNS = {KEY_ID, KEY_SUBJECT_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    private static final String TABLE_CLASS_TIMES = "ClassTimes";
    private static final String KEY_START_TIME = "StartTime";
    private static final String KEY_END_TIME = "EndTime";
    private static final String KEY_DAY = "Day";
    private static final String CLASS_COLUMNS[] = {KEY_ID, KEY_START_TIME, KEY_END_TIME, KEY_DAY, KEY_SUBJECT_ID};
    private static final String TABLE_TASKS = "Tasks";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_TASK_TITLE = "Title";
    private static final String KEY_TASK_DETAIL = "Detail";
    private static final String KEY_TASK_START = "StartDate";
    private static final String KEY_TASK_END = "EndDate";
    private static final String KEY_SHOW_REMINDER = "Reminder";
    private static final String KEY_TIME = "Time";
    private static final String KEY_COMPLETE = "Complete";
    private static final String KEY_PERCENT_COMPLETE = "Percent_Complete";
    private static final String[] TASK_COLUMNS = new String[] {KEY_ID, KEY_TYPE, KEY_TASK_TITLE, KEY_TASK_DETAIL, KEY_TASK_START, KEY_TASK_END, KEY_SHOW_REMINDER, KEY_TIME, KEY_COMPLETE, KEY_PERCENT_COMPLETE};
    private static ArrayList<ClassTime> classTimeCache = new ArrayList<>();
    private static boolean classTimeCacheValid = false;


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

        String CREATE_TABLE_SUBJECTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_SUBJECTS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_NAME + " VARCHAR, " +
                KEY_CLASSROOM + " VARCHAR, " +
                KEY_TEACHER + " VARCHAR, " +
                KEY_COLOR + " INTEGER )";
        db.execSQL(CREATE_TABLE_SUBJECTS);

        String CREATE_TABLE_CLASS_TIMES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_CLASS_TIMES +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                KEY_START_TIME + " INTEGER, " +
                KEY_END_TIME + " INTEGER, " +
                KEY_DAY + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";
        db.execSQL(CREATE_TABLE_CLASS_TIMES);
        String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_TASKS +
                "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TYPE + " INTEGER, " +
                KEY_TASK_TITLE + " VARCHAR, " +
                KEY_TASK_DETAIL + " VARCHAR, " +
                KEY_TASK_START + " INTEGER, " +
                KEY_TASK_END + " INTEGER, " +
                KEY_SHOW_REMINDER + " BOOLEAN, " +
                KEY_TIME + " INTEGER, " +
                KEY_COMPLETE + " BOOLEAN, " +
                KEY_PERCENT_COMPLETE + " INTEGER, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";
        db.execSQL(CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Inserts a new task into the database
     * @param t The task to be inserted
     * @return Returns the task t, with its database id
     */
    public Task addTask(Task t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, t.getType());
        values.put(KEY_TASK_TITLE, t.getTitle());
        values.put(KEY_TASK_DETAIL, t.getDetail());
        values.put(KEY_TASK_START, t.getStartDate());
        values.put(KEY_TASK_END, t.getEndDate());
        values.put(KEY_SHOW_REMINDER, t.showReminder());
        values.put(KEY_TIME, t.getTime());
        values.put(KEY_COMPLETE, t.isComplete());
        values.put(KEY_PERCENT_COMPLETE, t.getPercentageComplete());
        values.put(KEY_SUBJECT_ID, t.getSubjectID());
        t.setId((int) db.insert(TABLE_TASKS, null, values));
        Log.d("Data", "Adding task with values of " + t.toString());
        return t;
    }

    /**
     * If it exists, returns the task with ID of id
     * @param id The id of the task
     * @return The task. If it does not exist, a blank task is returned
     */
    public Task getTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_TASKS,
                TASK_COLUMNS,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        Task t = new Task();
        t.setId(cursor.getInt(0));
        t.setType(cursor.getInt(1));
        t.setTitle(cursor.getString(2));
        t.setDetail(cursor.getString(3));
        t.setStartDate(cursor.getInt(4));
        t.setEndDate(cursor.getInt(5));
        t.setShowReminder(cursor.getInt(6) > 0);
        t.setTime(cursor.getInt(7));
        t.setComplete(cursor.getInt(8) > 0);
        t.setPercentageComplete(cursor.getInt(9));
        cursor.close();
        Log.d("Data", "Returning task with values of " + t.toString());

        return t;
    }

    /**
     * Returns all of the tasks in the database
     * @return An arraylist of every task in the database. In the order that they were added
     */
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> list = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Task t;
        if(cursor.moveToFirst()) {
            do {
                t = new Task();
                t.setId(cursor.getInt(0));
                t.setType(cursor.getInt(1));
                t.setTitle(cursor.getString(2));
                t.setDetail(cursor.getString(3));
                t.setStartDate(cursor.getInt(4));
                t.setEndDate(cursor.getInt(5));
                t.setShowReminder(cursor.getInt(6) > 0);
                t.setTime(cursor.getInt(7));
                t.setComplete(cursor.getInt(8) > 0);
                t.setPercentageComplete(cursor.getInt(9));
                list.add(t);
            } while(cursor.moveToNext());
        }
        cursor.close();
        Log.d("Data", "Returning all task " + list.toString());

        return list;
    }

    /**
     * Returns all tasks within a range of the current data
     * @param range The range, in days, to return
     * @return An arraylist of all the tasks in range
     */
    public ArrayList<Task> getTasksInRange(int range) {
        ArrayList<Task> result = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Task t;
        Calendar rangeEnd = Calendar.getInstance();
        rangeEnd.setTime(new Date());
        rangeEnd.add(Calendar.DATE, range);
        Calendar taskEnd = Calendar.getInstance();
        if(cursor.moveToFirst()) {
            do {
                taskEnd.setTime(new Date(cursor.getInt(5)));
                if(taskEnd.before(rangeEnd)) {
                    t = new Task();
                    t.setId(cursor.getInt(0));
                    t.setType(cursor.getInt(1));
                    t.setTitle(cursor.getString(2));
                    t.setDetail(cursor.getString(3));
                    t.setStartDate(cursor.getInt(4));
                    t.setEndDate(cursor.getInt(5));
                    t.setShowReminder(cursor.getInt(6) > 0);
                    t.setTime(cursor.getInt(7));
                    t.setComplete(cursor.getInt(8) > 0);
                    t.setPercentageComplete(cursor.getInt(9));
                    result.add(t);
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Updates the values of a task, t, in the database
     * @param t The updated task, with an id
     * @return The number of rows affected
     */
    public int update(Task t) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, t.getType());
        values.put(KEY_TASK_TITLE, t.getTitle());
        values.put(KEY_TASK_DETAIL, t.getDetail());
        values.put(KEY_TASK_START, t.getStartDate());
        values.put(KEY_TASK_END, t.getEndDate());
        values.put(KEY_SHOW_REMINDER, t.showReminder());
        values.put(KEY_TIME, t.getTime());
        values.put(KEY_COMPLETE, t.isComplete());
        values.put(KEY_PERCENT_COMPLETE,t.getPercentageComplete());
        values.put(KEY_SUBJECT_ID, t.getSubjectID());
        int i = db.update(TABLE_TASKS,
                values,
                KEY_ID + " = " + t.getId(),
                null);
        db.close();
        Log.d("Data", "Updating task with values of " + t.toString());

        return  i;
    }

    /**
     * Removes a task, t, from the database
     * @param t The task to remove
     */
    public void delete(Task t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERMS,
                KEY_ID + " = " + t.getId(),
                null);
        db.close();

        Log.d("Data", "Deleting a task with values of " + t.toString());
    }

    //End task methods

    /**
     * Adds a term to the database
     * @param t The term to add
     * @return The term instance, with its id set
     */
    public Term addTerm(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TERM_NAME, t.getName());
        values.put(KEY_START_DATE, t.getStartDate());
        values.put(KEY_END_DATE, t.getEndDate());
        t.setId((int) db.insert(TABLE_TERMS, null, values));
        Log.d("Data", "Adding term with values of " + t.toString());
        return t;
    }

    /**
     * Returns a term with ID of id
     * @param id The ID of the term to return
     * @return The term with ID of id. An empty term if no term with ID id exists
     */
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
        cursor.close();
        Log.d("Data ", "Reading term with values of " + t.toString());

        return t;
    }

    /**
     * Returns all of the terms in the database
     * @return An arraylist of all of the terms in the database
     */
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
        cursor.close();
        Collections.sort(list);
        Collections.reverse(list);
        Log.d("Data", "Returning all terms " + list.toString());
        return list;
    }

    /**
     * Updates a term in the database, with the same ID as the term passed
     * @param t The updated term
     * @return Returns the number of rows affected
     */
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

    /**
     * Removes the term t from the database if it exists
     * @param t The term to remove
     */
    public void delete(Term t) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERMS,
                KEY_ID + " = " + t.getId(),
                null);
        db.close();

        Log.d("Data", "Deleting term with values of" + t.toString());
    }

    public Term getCurrentTerm() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT  * FROM " + TABLE_TERMS;
        Cursor cursor = db.rawQuery(query, null);
        Date current = new Date();
        Date start;
        Date end;

        Term t = new Term();
        if (cursor.moveToFirst()) {
            do {
                start = new Date(Long.parseLong(cursor.getString(2)));
                end = new Date(Long.parseLong(cursor.getString(3)));
                if(current.after(start) && current.before(end)) {
                    t.setId(Integer.parseInt(cursor.getString(0)));
                    t.setName(cursor.getString(1));
                    t.setStartDate(Long.parseLong(cursor.getString(2)));
                    t.setEndDate(Long.parseLong(cursor.getString(3)));
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return t;
    }

    //End term methods

    /**
     * Adds a subject to the database
     * @param s The subject to add
     * @return The subject instance, with its ID set
     */
    public Subject addSubject(Subject s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, s.getName());
        values.put(KEY_CLASSROOM, s.getClassroom());
        values.put(KEY_TEACHER, s.getTeacher());
        values.put(KEY_COLOR, s.getColor());

        s.setId((int) db.insert(TABLE_SUBJECTS, null, values));
        Log.d("Data", "Adding subject with values of " + s.toString());
        return s;
    }

    /**
     * Deletes everyhing
     */
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, VERSION, VERSION);
    }

    /**
     * Gets a subject from the database
     * @param id The ID of the subject to return
     * @return The subject with ID id
     */
    public Subject getSubject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_SUBJECTS,
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
        s.setId(cursor.getInt(0));
        s.setName(cursor.getString(1));
        s.setClassroom(cursor.getString(2));
        s.setTeacher(cursor.getString(3));
        s.setColor(cursor.getInt(4));
        cursor.close();

        Log.d("Data", "Reading subject with values of " + s.toString());

        return s;
    }

    /**
     * Get all of the subjects in the database
     * @return Returns an arraylist of all of the subjects in the database
     */
    public ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SUBJECTS;
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
        cursor.close();

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

        int i = db.update(TABLE_SUBJECTS,
                values,
                KEY_ID + " = " + s.getId(),
                null);

        db.close();
        Log.d("Data", "Updating subject to values of" + s.toString());
        return i;
    }

    public void deleteSubject(Subject s) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Data", "Deleting subject with values of " + s.toString());
        int numClass = db.delete(TABLE_CLASS_TIMES,
                KEY_SUBJECT_ID + " = " + s.getId(),
                null);
        Log.d("Data", "Deletion of subject resulted in deletion of " + numClass + " classes");

        int numTask = db.delete(TABLE_TASKS,
                 KEY_SUBJECT_ID + " = " + s.getId(),
                null);
        Log.d("Data", "Deletion of subject resulted in deletion of " + numTask + " tasks");

        db.delete(TABLE_SUBJECTS,
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
        classTimeCacheValid = false;
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
        c.setId(cursor.getInt(0));
        c.setSubjectID(cursor.getInt(1));
        c.setStart(cursor.getInt(2));
        c.setEnd(cursor.getInt(3));
        c.setDay(cursor.getInt(4));
        cursor.close();

        Log.d("Data", "Returning class with values of " + c.toString());

        return c;
    }

    public ArrayList<ClassTime> getAllClasses() {
        if(classTimeCacheValid) {
            Log.i(TABLE_CLASS_TIMES, "Returning cached classes " + classTimeCache);
            return classTimeCache;
        }
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
        cursor.close();
        Collections.sort(classes);
        Collections.reverse(classes);
        classTimeCache = classes;
        classTimeCacheValid = true;
        Log.d("Data", "Returning all classes" + classes.toString());
        return classes;
    }

    /**
     * @param day The day, from Monday as 0, to Friday as 4
     * @return An arraylist of the classes on the current day
     */
    public ArrayList<ClassTime> getClassesForDay(int day) {
        ArrayList<ClassTime> result = new ArrayList<>();
        if(classTimeCacheValid) {
            for(ClassTime c :classTimeCache) {
                if(c.getDay() == day) {
                    result.add(c);
                }
            }
            return result;
        }
        String query = "SELECT * FROM " + TABLE_CLASS_TIMES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ClassTime c;
        if(cursor.moveToFirst()) {
            do {
                if(cursor.getInt(4) == day) {
                    c = new ClassTime();
                    c.setId(Integer.parseInt(cursor.getString(0)));
                    c.setSubjectID(cursor.getInt(1));
                    c.setStart(cursor.getInt(2));
                    c.setEnd(cursor.getInt(3));
                    c.setDay(cursor.getInt(4));
                    result.add(c);
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(result);
        return  result;
    }

    /**
     * Uses getClassesForDay() to get todays classes
     * @return An arraylist of the classes for the current day
     */
    public ArrayList<ClassTime> getClassesToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return getClassesForDay(day);
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
        classTimeCacheValid = false;
        return i;
    }

    public void deleteClass(ClassTime c) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_TIMES,
                KEY_ID + " = " + c.getId(),
                null);
        db.close();
        classTimeCacheValid = false;
        Log.d("Data", "Deleting class with values of " + c.toString());
    }
}
