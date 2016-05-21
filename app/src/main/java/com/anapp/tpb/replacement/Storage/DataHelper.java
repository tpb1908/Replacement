package com.anapp.tpb.replacement.Storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anapp.tpb.replacement.Home.Interfaces.DataUpdateListener;
import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
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
public class DataHelper extends SQLiteOpenHelper implements DataUpdateListener<Object> {
    private static DataHelper instance;

    private static final String TAG = "DataHelper";
    private static final String DATABASE_NAME = "WORK";
    private static final int VERSION = 1;
    private static final String KEY_ID = "ID";
    private static final String KEY_SUBJECT_ID = "SUBJECT_ID";
    private static final String KEY_START_DATE = "START_DATE";
    private static final String KEY_END_DATE = "END_DATE";

    private static final String TABLE_TERMS = "TERMS";
    private static final String KEY_TERM_NAME = "TERM_NAME";
    private static final String[] TERM_COLUMNS = {KEY_ID, KEY_TERM_NAME, KEY_START_DATE, KEY_END_DATE};

    private static final String TABLE_SUBJECTS = "SUBJECTS";
    private static final String KEY_SUBJECT_NAME = "SUBJECT_NAME";
    private static final String KEY_CLASSROOM = "CLASSROOM";
    private static final String KEY_TEACHER = "TEACHER";
    private static final String KEY_COLOR = "COLOR";
    private static final String[] SUBJECT_COLUMNS = {KEY_ID, KEY_SUBJECT_NAME, KEY_CLASSROOM, KEY_TEACHER, KEY_COLOR};

    private static final String TABLE_CLASS_TIMES = "CLASS_TIMES";
    private static final String KEY_START_TIME = "START_TIME";
    private static final String KEY_END_TIME = "END_TIME";
    private static final String KEY_DAY = "DAY";
    private static final String CLASS_COLUMNS[] = {KEY_ID, KEY_START_TIME, KEY_END_TIME, KEY_DAY, KEY_SUBJECT_ID};

    private static final String TABLE_TASKS_CURRENT = "CURRENT_TASKS";
    private static final String TABLE_TASKS_ARCHIVE = "ARCHIVED_TASKS";
    private static final String KEY_TYPE = "TYPE";
    private static final String KEY_TASK_TITLE = "TITLE";
    private static final String KEY_TASK_DETAIL = "DETAIL";
    private static final String KEY_SHOW_REMINDER = "REMINDER";
    private static final String KEY_TIME = "TIME";
    private static final String KEY_COMPLETE = "COMPLETE";
    private static final String KEY_PERCENT_COMPLETE = "PERCENT_COMPLETE";
    private static final String KEY_DATE_COMPLETE = "DATE_COMPLETE";
    private static final String[] TASK_COLUMNS = new String[] {KEY_ID, KEY_TYPE, KEY_TASK_TITLE, KEY_TASK_DETAIL, KEY_START_DATE, KEY_END_DATE, KEY_SHOW_REMINDER, KEY_TIME, KEY_COMPLETE, KEY_PERCENT_COMPLETE, KEY_DATE_COMPLETE, KEY_SUBJECT_ID};


    private static DataWrapper<Task> currentTaskWrapper = new DataWrapper<>();
    private static boolean isCurrentTaskCacheValid = false;
    private static DataWrapper<Subject> subjectDataWrapper = new DataWrapper<>();
    private static boolean isSubjectCacheValid = false;
    private static DataWrapper<ClassTime> classWrapper = new DataWrapper<>();
    private static boolean isClassTimeCacheValid = false;
    private static DataWrapper<ClassTime> todayClassWrapper = new DataWrapper<>();
    private static boolean isTodayClassTimeChacheValid;


    public static synchronized DataHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DataHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DataHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TERM_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_TERMS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TERM_NAME + " VARCHAR, " +
                KEY_START_DATE + " INTEGER, " +
                KEY_END_DATE + " INTEGER)";
        db.execSQL(CREATE_TERM_TABLE);

        final String CREATE_TABLE_SUBJECTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_SUBJECTS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_NAME + " VARCHAR, " +
                KEY_CLASSROOM + " VARCHAR, " +
                KEY_TEACHER + " VARCHAR, " +
                KEY_COLOR + " INTEGER )";
        db.execSQL(CREATE_TABLE_SUBJECTS);

        final String CREATE_TABLE_CLASS_TIMES = "CREATE TABLE IF NOT EXISTS " +
                TABLE_CLASS_TIMES +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                KEY_START_TIME + " INTEGER, " +
                KEY_END_TIME + " INTEGER, " +
                KEY_DAY + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";
        db.execSQL(CREATE_TABLE_CLASS_TIMES);

        final String CREATE_TASK_COLUMNS =
                KEY_TYPE + " INTEGER, " +
                KEY_TASK_TITLE + " VARCHAR, " +
                KEY_TASK_DETAIL + " VARCHAR, " +
                KEY_START_DATE + " INTEGER, " +
                KEY_END_DATE + " INTEGER, " +
                KEY_SHOW_REMINDER + " BOOLEAN, " +
                KEY_TIME + " INTEGER, " +
                KEY_COMPLETE + " BOOLEAN, " +
                KEY_PERCENT_COMPLETE + " INTEGER, " +
                        KEY_DATE_COMPLETE + " INTEGER, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";

        final String CREATE_TABLE_TASKS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_TASKS_CURRENT +
                "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CREATE_TASK_COLUMNS;
        db.execSQL(CREATE_TABLE_TASKS);

        //Archive table uses IDs of old tasks
        final String CREATE_TABLE_TASKS_ARCHIVE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_TASKS_ARCHIVE +
                "(" + KEY_ID + " INTEGER PRIMARY KEY, " +
                CREATE_TASK_COLUMNS;
        db.execSQL(CREATE_TABLE_TASKS_ARCHIVE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Inserts a new task into the database
     * @param task The task to be inserted
     * @return Returns the task t, with its database id
     */
    public Task addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, task.getType());
        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DETAIL, task.getDetail());
        values.put(KEY_START_DATE, task.getStartDate());
        values.put(KEY_END_DATE, task.getEndDate());
        values.put(KEY_SHOW_REMINDER, task.showReminder());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_COMPLETE, task.isComplete());
        values.put(KEY_PERCENT_COMPLETE, task.getPercentageComplete());
        values.put(KEY_DATE_COMPLETE, task.getCompleteDate());
        values.put(KEY_SUBJECT_ID, task.getSubjectID());
        task.setId((int) db.insert(TABLE_TASKS_CURRENT, null, values));
        Log.i(TAG, "Adding task " + task.toString());
        db.close();
        if(!currentTaskWrapper.contains(task)) currentTaskWrapper.add(task);
        return task;
    }

    public void archiveTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        task.setComplete(true);
        values.put(KEY_ID, task.getId());
        values.put(KEY_TYPE, task.getType());
        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DETAIL, task.getDetail());
        values.put(KEY_START_DATE, task.getStartDate());
        values.put(KEY_END_DATE, task.getEndDate());
        values.put(KEY_SHOW_REMINDER, task.showReminder());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_COMPLETE, task.isComplete());
        values.put(KEY_PERCENT_COMPLETE, task.getPercentageComplete());
        values.put(KEY_SUBJECT_ID, task.getSubjectID());
        values.put(KEY_DATE_COMPLETE, task.getCompleteDate());
        db.insert(TABLE_TASKS_ARCHIVE, null, values);
        db.delete(TABLE_TASKS_CURRENT,
                KEY_ID + " = " + task.getId(),
                null);
        db.close();
        currentTaskWrapper.remove(task);
        Log.i(TAG, "Archiving task " + task.toString());
    }

    /**
     * If it exists, returns the task with ID of id
     * @param id The id of the task
     * @return The task. If it does not exist, a blank task is returned
     */
    public Task getCurrentTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_TASKS_CURRENT,
                TASK_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        Task task = new Task();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                task.setId(cursor.getInt(0));
                task.setType(cursor.getInt(1));
                task.setTitle(cursor.getString(2));
                task.setDetail(cursor.getString(3));
                task.setStartDate(cursor.getLong(4));
                task.setEndDate(cursor.getLong(5));
                task.setShowReminder(cursor.getInt(6) > 0);
                task.setTime(cursor.getInt(7));
                task.setComplete(cursor.getInt(8) > 0);
                task.setPercentageComplete(cursor.getInt(9));
                task.setCompleteDate(cursor.getInt(10));
                task.setSubjectID(cursor.getInt(11));
                task.setSubject(getSubjectForData(db, task.getSubjectID()));
            }
            cursor.close();
        }
        Log.i(TAG, "Reading current task " + task.toString());
        db.close();
        return task;
    }

    /**
     * Returns all of the tasks in the database
     * @return An arraylist of every task in the database. In the order that they were added
     */
    public DataWrapper<Task> getAllCurrentTasks() {
        if(isCurrentTaskCacheValid) return currentTaskWrapper;
        ArrayList<Task> tasks = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_TASKS_CURRENT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        Task task;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    task = new Task();
                    task.setId(cursor.getInt(0));
                    task.setType(cursor.getInt(1));
                    task.setTitle(cursor.getString(2));
                    task.setDetail(cursor.getString(3));
                    task.setStartDate(cursor.getLong(4));
                    task.setEndDate(cursor.getLong(5));
                    task.setShowReminder(cursor.getInt(6) > 0);
                    task.setTime(cursor.getInt(7));
                    task.setComplete(cursor.getInt(8) > 0);
                    task.setPercentageComplete(cursor.getInt(9));
                    task.setCompleteDate(cursor.getInt(10));
                    task.setSubjectID(cursor.getInt(11));
                    task.setSubject(getSubjectForData(db, task.getSubjectID()));
                    tasks.add(task);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        currentTaskWrapper.setData(tasks);
        isCurrentTaskCacheValid = true;
        db.close();
        Log.i(TAG, "Reading current tasks " + tasks.toString());
        return currentTaskWrapper;
    }


    /**
     * Returns all tasks within a range of the current data
     * @param range The range, in days, to return
     * @return An arraylist of all the tasks in range
     */
    //TODO- Use the task cache
    public ArrayList<Task> getCurrentTasksInRange(int range) {
        ArrayList<Task> result = new ArrayList<>();
        Calendar rangeEnd = Calendar.getInstance();
        rangeEnd.add(Calendar.DATE, range);
        long end = rangeEnd.getTimeInMillis();
        final String QUERY = "SELECT * FROM " + TABLE_TASKS_CURRENT +
                " WHERE " + KEY_END_DATE + " < " +  end;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        Task task;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    task = new Task();
                    task.setId(cursor.getInt(0));
                    task.setType(cursor.getInt(1));
                    task.setTitle(cursor.getString(2));
                    task.setDetail(cursor.getString(3));
                    task.setStartDate(cursor.getLong(4));
                    task.setEndDate(cursor.getLong(5));
                    task.setShowReminder(cursor.getInt(6) > 0);
                    task.setTime(cursor.getInt(7));
                    task.setComplete(cursor.getInt(8) > 0);
                    task.setPercentageComplete(cursor.getInt(9));
                    task.setCompleteDate(cursor.getInt(10));
                    task.setSubject(getSubjectForData(db, task.getSubjectID()));
                    result.add(task);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();

        return result;
    }

//    public ArrayList<Task> getCurrentTasksInRange(int range) {
//        ArrayList<Task> result = new ArrayList<>();
//        String query = "SELECT * FROM " + TABLE_TASKS_CURRENT;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        Task task;
//        Calendar rangeEnd = Calendar.getInstance();
//        rangeEnd.setTime(new Date());
//        rangeEnd.add(Calendar.DATE, range);
//        Calendar taskEnd = Calendar.getInstance();
//        if(cursor.moveToFirst()) {
//            do {
//                taskEnd.setTime(new Date(cursor.getInt(5)));
//                if(taskEnd.before(rangeEnd)) {
//                    task = new Task();
//                    task.setId(cursor.getInt(0));
//                    task.setType(cursor.getInt(1));
//                    task.setTitle(cursor.getString(2));
//                    task.setDetail(cursor.getString(3));
//                    task.setStartDate(cursor.getLong(4));
//                    task.setEndDate(cursor.getLong(5));
//                    task.setShowReminder(cursor.getInt(6) > 0);
//                    task.setTime(cursor.getInt(7));
//                    task.setComplete(cursor.getInt(8) > 0);
//                    task.setPercentageComplete(cursor.getInt(9));
//                    task.setCompleteDate(cursor.getInt(10));
//                    task.setSubject(getSubjectForData(db, task.getSubjectID()));
//                    result.add(task);
//                }
//            } while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return result;
//    }

    /**
     * Updates the values of a task, t, in the database
     * @param task The updated task, with an id
     * @return The number of rows affected
     */
    public int updateCurrent(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, task.getType());
        values.put(KEY_TASK_TITLE, task.getTitle());
        values.put(KEY_TASK_DETAIL, task.getDetail());
        values.put(KEY_START_DATE, task.getStartDate());
        values.put(KEY_END_DATE, task.getEndDate());
        values.put(KEY_SHOW_REMINDER, task.showReminder());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_COMPLETE, task.isComplete());
        values.put(KEY_PERCENT_COMPLETE, task.getPercentageComplete());
        values.put(KEY_SUBJECT_ID, task.getSubjectID());
        values.put(KEY_DATE_COMPLETE, task.getCompleteDate());
        int i = db.update(TABLE_TASKS_CURRENT,
                values,
                KEY_ID + " = " + task.getId(),
                null);
        task.setSubject(getSubjectForData(db, task.getSubjectID()));
        db.close();
        currentTaskWrapper.update(task);
        Log.i(TAG, "Updating task" + task.toString());
        return  i;
    }

    /**
     * Removes a task, t, from the database
     * @param task The task to remove
     */
    public void deleteCurrent(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS_CURRENT,
                KEY_ID + " = " + task.getId(),
                null);
        db.close();
        currentTaskWrapper.remove(task);
        Log.i(TAG, "Deleting a task " + task.toString());
    }


    //End task methods

    /**
     * Adds a term to the database
     * @param term The term to add
     * @return The term instance, with its id set
     */
    public Term addTerm(Term term) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TERM_NAME, term.getName());
        values.put(KEY_START_DATE, term.getStartDate());
        values.put(KEY_END_DATE, term.getEndDate());
        term.setId((int) db.insert(TABLE_TERMS, null, values));
        Log.i(TAG, "Adding term " + term.toString());
        return term;
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
                KEY_ID + "  = ?", //selections
                new String[]{String.valueOf(id)},
                null, //group by
                null, //having
                null, //order by
                null); //limit
        Term term = new Term();
        if (cursor != null) {
            cursor.moveToFirst();
            term.setId(cursor.getInt(0));
            term.setName(cursor.getString(1));
            term.setStartDate(cursor.getLong(2));
            term.setEndDate(cursor.getLong(3));
            cursor.close();
        }
        db.close();
        Log.i(TAG, "Reading term " + term.toString());

        return term;
    }

    /**
     * Returns all of the terms in the database
     * @return An arraylist of all of the terms in the database
     */
    public ArrayList<Term> getAllTerms() {
        ArrayList<Term> result = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_TERMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Term term;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    term = new Term();
                    term.setId(cursor.getInt(0));
                    term.setName(cursor.getString(1));
                    term.setStartDate(cursor.getLong(2));
                    term.setEndDate(cursor.getLong(3));
                    result.add(term);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        Collections.sort(result);
        Collections.reverse(result);
        db.close();
        Log.i(TAG, "All terms " + result.toString());
        return result;
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
        Log.i(TAG, "Updating term " + t.toString());
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

        Log.i(TAG, "Deleting term " + t.toString());
    }

//    public Term getCurrentTerm() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        final String QUERY = "SELECT  * FROM " + TABLE_TERMS;
//        Cursor cursor = db.rawQuery(QUERY, null);
//        Date current = new Date();
//        Date start;
//        Date end;
//
//        Term t = new Term();
//        if (cursor.moveToFirst()) {
//            do {
//                start = new Date(cursor.getLong(2));
//                end = new Date(cursor.getLong(3));
//                if(current.after(start) && current.before(end)) {
//                    t.setId(Integer.parseInt(cursor.getString(0)));
//                    t.setName(cursor.getString(1));
//                    t.setStartDate(start.getTime());
//                    t.setEndDate(end.getTime());
//                    break;
//                }
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return t;
//    }

    public Term getCurrentTerm() {
        SQLiteDatabase db = this.getWritableDatabase();
        final long CURRENT = new Date().getTime();
        final String QUERY = "SELECT  * FROM " + TABLE_TERMS +
                " WHERE " + KEY_START_DATE + " > " + CURRENT +
                " AND " + KEY_END_DATE + " < " + CURRENT;
        Cursor cursor = db.rawQuery(QUERY, null);
        Term t = new Term();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                t.setId(cursor.getInt(0));
                t.setName(cursor.getString(1));
                t.setStartDate(cursor.getLong(2));
                t.setEndDate(cursor.getLong(3));
            }
            cursor.close();
        }
        db.close();
        Log.i(TAG, "Current term " + t.toString());
        return t;
    }

    //End term methods

    /**
     * Adds a subject to the database
     * @param subject The subject to add
     * @return The subject instance, with its ID set
     */
    public Subject addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, subject.getName());
        values.put(KEY_CLASSROOM, subject.getClassroom());
        values.put(KEY_TEACHER, subject.getTeacher());
        values.put(KEY_COLOR, subject.getColor());

        subject.setId((int) db.insert(TABLE_SUBJECTS, null, values));
        Log.i(TAG, "Adding subject  " + subject.toString());
        db.close();
        if(!subjectDataWrapper.contains(subject)) subjectDataWrapper.add(subject);
        //if(!subjectCache.contains(subject)) subjectCache.add(subject);
        return subject;
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
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        Subject subject = new Subject();
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                subject.setId(cursor.getInt(0));
                subject.setName(cursor.getString(1));
                subject.setClassroom(cursor.getString(2));
                subject.setTeacher(cursor.getString(3));
                subject.setColor(cursor.getInt(4));
            }
            cursor.close();
        }
        db.close();
        return subject;
    }

    private Subject getSubjectForData(SQLiteDatabase db, int id) {
        Cursor cursor = db.query(TABLE_SUBJECTS,
                SUBJECT_COLUMNS,
                KEY_ID + "  = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        Subject subject = new Subject();
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                subject.setId(cursor.getInt(0));
                subject.setName(cursor.getString(1));
                subject.setClassroom(cursor.getString(2));
                subject.setTeacher(cursor.getString(3));
                subject.setColor(cursor.getInt(4));
            }
            cursor.close();
        }
        return subject;
    }

//    private Subject getSubjectForData(SQLiteDatabase db, int id) {
//        final String QUERY = "SELECT * FROM " + TABLE_SUBJECTS +
//                " WHERE " + KEY_ID + " = " + id;
//        Cursor cursor = db.rawQuery(QUERY, null);
//        Subject subject = new Subject();
//        if (cursor != null) {
//            if(cursor.moveToFirst()) {
//                subject.setId(cursor.getInt(0));
//                subject.setName(cursor.getString(1));
//                subject.setClassroom(cursor.getString(2));
//                subject.setTeacher(cursor.getString(3));
//                subject.setColor(cursor.getInt(4));
//            }
//            cursor.close();
//        }
//        return subject;
//    }

    /**
     * Get all of the subjects in the database
     * @return Returns an arraylist of all of the subjects in the database
     */
    public DataWrapper<Subject> getAllSubjects() {
        if(isSubjectCacheValid) return subjectDataWrapper;
        ArrayList<Subject> subjects = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_SUBJECTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);

        Subject subject;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    subject = new Subject();
                    subject.setId(cursor.getInt(0));
                    subject.setName(cursor.getString(1));
                    subject.setClassroom(cursor.getString(2));
                    subject.setTeacher(cursor.getString(3));
                    subject.setColor(cursor.getInt(4));
                    subjects.add(subject);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        Log.i(TAG, "All subjects " + subjects.toString());
        subjectDataWrapper.setData(subjects);
        isSubjectCacheValid = true;
        return subjectDataWrapper;
    }

    public int update(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_NAME, subject.getName());
        values.put(KEY_CLASSROOM, subject.getClassroom());
        values.put(KEY_TEACHER, subject.getTeacher());
        values.put(KEY_COLOR, subject.getColor());

        int i = db.update(TABLE_SUBJECTS,
                values,
                KEY_ID + " = " + subject.getId(),
                null);
        db.close();
        Log.i(TAG, "Updating subject " + subject.toString());
        subjectDataWrapper.update(subject);
        return i;
    }

    //TODO- Integrate fully with caching
    public void deleteSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG, "Deleting subject " + subject.toString());
        db.delete(TABLE_CLASS_TIMES,
                KEY_SUBJECT_ID + " = " + subject.getId(),
                null);
        getAllClasses();

        int numTask = db.delete(TABLE_TASKS_CURRENT,
                KEY_SUBJECT_ID + " = " + subject.getId(),
                null);
        getAllCurrentTasks();
        db.delete(TABLE_TASKS_ARCHIVE,
                KEY_SUBJECT_ID + " = " + subject.getId(), null);

        db.delete(TABLE_SUBJECTS,
                KEY_ID + " = " + subject.getId(),
                null);
        getAllSubjects();
        db.close();
    }

    //End subject methods

    public ClassTime addClass(ClassTime time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_ID, time.getSubjectID());
        values.put(KEY_START_TIME, time.getStart());
        values.put(KEY_END_TIME, time.getEnd());
        values.put(KEY_DAY, time.getDay());

        time.setId((int) db.insert(TABLE_CLASS_TIMES, null, values));
        isClassTimeCacheValid = false;
        db.close();
        if(!classWrapper.contains(time)) classWrapper.add(time);
        Log.i(TAG, "Adding class " + time.toString());
        return time;
    }

    public ClassTime getClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_CLASS_TIMES,
                CLASS_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        ClassTime time = new ClassTime();
        if (cursor != null) {
            cursor.moveToFirst();
            time.setId(cursor.getInt(0));
            time.setStart(cursor.getInt(1));
            time.setEnd(cursor.getInt(2));
            time.setDay(cursor.getInt(3));
            time.setSubjectID(cursor.getInt(4));
            time.setSubject(getSubjectForData(db, time.getSubjectID()));
            cursor.close();
        }
        db.close();
        Log.i(TAG, "Returning class " + time.toString());

        return time;
    }

    public DataWrapper<ClassTime> getAllClasses() {
        if(isClassTimeCacheValid) return classWrapper;

        ArrayList<ClassTime> classes = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_CLASS_TIMES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);

        ClassTime time;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    time = new ClassTime();
                    time.setId(cursor.getInt(0));
                    time.setSubjectID(cursor.getInt(1));
                    time.setStart(cursor.getInt(2));
                    time.setEnd(cursor.getInt(3));
                    time.setDay(cursor.getInt(4));
                    time.setSubject(getSubjectForData(db, time.getSubjectID()));
                    classes.add(time);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        Collections.sort(classes);
        Collections.reverse(classes);
        db.close();
        classWrapper.setData(classes);
        isClassTimeCacheValid = true;
        Log.i(TAG, "Returning all classes" + classes.toString());
        return classWrapper;
    }

    /**
     * @param day The day, from Monday as 0, to Friday as 4
     * @return An arraylist of the classes on the current day
     */
    //TODO- Multiple data wrappers
    public DataWrapper<ClassTime> getClassesForDay(int day) {
        ArrayList<ClassTime> result = new ArrayList<>();
        if(isTodayClassTimeChacheValid) {
            return todayClassWrapper;
        }

        final String QUERY = "SELECT * FROM " + TABLE_CLASS_TIMES +
                " WHERE " + KEY_DAY + " = " + day;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY, null);
        ClassTime time;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    time = new ClassTime();
                    time.setId(cursor.getInt(0));
                    time.setSubjectID(cursor.getInt(1));
                    time.setStart(cursor.getInt(2));
                    time.setEnd(cursor.getInt(3));
                    time.setDay(cursor.getInt(4));
                    time.setSubject(getSubjectForData(db, time.getSubjectID()));
                    result.add(time);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        Collections.sort(result);
        db.close();
        Log.i(TAG, "Returning classes for " + day + " " + result.toString());
        todayClassWrapper.setData(result);
        isTodayClassTimeChacheValid = true;
        return todayClassWrapper;

    }
    //Previous method
//    public ArrayList<ClassTime> getClassesForDay(int day) {
//        ArrayList<ClassTime> result = new ArrayList<>();
//        if(isClassTimeCacheValid) {
//            for(ClassTime time : classTimeCache) {
//                if(time.getDay() == day) {
//                    result.add(time);
//                }
//            }
//            return result;
//        }
//        String query = "SELECT * FROM " + TABLE_CLASS_TIMES;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        ClassTime time;
//        if(cursor.moveToFirst()) {
//            do {
//                if(cursor.getInt(4) == day) {
//                    time = new ClassTime();
//                    time.setId(cursor.getInt(0));
//                    time.setSubjectID(cursor.getInt(1));
//                    time.setStart(cursor.getInt(2));
//                    time.setEnd(cursor.getInt(3));
//                    time.setDay(cursor.getInt(4));
//                    time.setSubject(getSubjectForData(db, time.getSubjectID()));
//                    result.add(time);
//                }
//            } while(cursor.moveToNext());
//        }
//        cursor.close();
//        Collections.sort(result);
//        db.close();
//        return  result;
//    }

    /**
     * Uses getClassesForDay() to get today's classes
     * @return An arraylist of the classes for the current day
     */
    //TODO- Check day
    public DataWrapper<ClassTime> getClassesToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return getClassesForDay(day);
    }

    public int update(ClassTime time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT_ID, time.getSubjectID());
        values.put(KEY_START_TIME, time.getStart());
        values.put(KEY_END_TIME, time.getEnd());
        values.put(KEY_DAY, time.getDay());

        int i = db.update(TABLE_CLASS_TIMES,
                values,
                KEY_ID + " = " + time.getId(),
                null);

        db.close();
        classWrapper.update(time);
        Log.i(TAG, "Updating class  " + time.toString());
        isClassTimeCacheValid = false;
        return i;
    }

    public void deleteClass(ClassTime time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_TIMES,
                KEY_ID + " = " + time.getId(),
                null);
        db.close();
        classWrapper.remove(time);
        Log.i(TAG, "Deleting class " + time.toString());
    }

    @Override
    public void updateAll() {
        //TODO- Find a way to check what thing has changed
    }

    @Override
    public void add(Object o) {
        if(o instanceof Task) {
            Task t = (Task) o;
            addTask(t);
        } else if(o instanceof Subject) {
            Subject s = (Subject) o;
            addSubject(s);
        } else if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            addClass(ct);
        } else {
            Log.i(TAG, "Object passed to add doesn't have valid type. " + o.getClass());
        }
    }

    @Override
    public void add(int index, Object o) {
        if(o instanceof Task) {
            Task t = (Task) o;
            addTask(t);
        } else if(o instanceof Subject) {
            Subject s = (Subject) o;
            addSubject(s);
        } else if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            addClass(ct);
        } else {
            Log.i(TAG, "Object passed to add doesn't have valid type. " + o.getClass());
        }
    }

    @Override
    public void remove(Object o) {
        if(o instanceof Task) {
            Task t = (Task) o;
            deleteCurrent(t);
        } else if(o instanceof Subject) {
            Subject s = (Subject) o;
            deleteSubject(s);
        } else if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            deleteClass(ct);
        } else {
            Log.i(TAG, "Object passed to add doesn't have valid type. " + o.getClass());
        }
    }

    @Override
    public void remove(int index, Object o) {
        if(o instanceof Task) {
            Task t = (Task) o;
            deleteCurrent(t);
        } else if(o instanceof Subject) {
            Subject s = (Subject) o;
            deleteSubject(s);
        } else if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            deleteClass(ct);
        } else {
            Log.i(TAG, "Object passed to add doesn't have valid type. " + o.getClass());
        }
    }

    @Override
    public void update(Object o) {
        if(o instanceof Task) {
            Task t = (Task) o;
            updateCurrent(t);
        } else if(o instanceof Subject) {
            Subject s = (Subject) o;
            update(s);
        } else if(o instanceof ClassTime) {
            ClassTime ct = (ClassTime) o;
            update(ct);
        } else {
            Log.i(TAG, "Object passed to add doesn't have valid type. " + o.getClass());
        }
    }

    @Override
    public void move(Object o, int oldPos, int newPos) {
        //This doesn't matter to the DataHelper
    }
}
