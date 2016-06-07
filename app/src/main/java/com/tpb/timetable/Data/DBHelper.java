package com.tpb.timetable.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tpb.timetable.Data.Templates.Assessment;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Data.Templates.Data;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Data.Templates.Term;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by theo on 27/05/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    private static final String TAG = "DB";
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

    private static final String TABLE_ASSESSMENTS = "ASSESSMENTS";
    private static final String KEY_ASSESSMENT_NAME = "ASSESSMENT_NAME";
    private static final String KEY_ASSESSMENT_DATE = "DATE";
    private static final String KEY_ASSESSMENT_REVISION = "REVISION";
    private static final String KEY_ASSESSMENT_PERCENTAGE = "PERCENTAGE";
    private static final String KEY_ASSESSMENT_COMPLETE = "COMPLETE";
    private static final String[] ASSESSMENT_COLUMNS = new String[] {KEY_ID, KEY_ASSESSMENT_NAME, KEY_ASSESSMENT_DATE, KEY_ASSESSMENT_REVISION, KEY_ASSESSMENT_COMPLETE, KEY_ASSESSMENT_PERCENTAGE, KEY_SUBJECT_ID};

    private final ArrayWrapper<Term> termWrapper = new ArrayWrapper<>(this);
    private final ArrayWrapper<Subject> subjectWrapper = new ArrayWrapper<>(this);
    private final ArrayWrapper<ClassTime> classTimeWrapper = new ArrayWrapper<>(this);
    private final ArrayWrapper<Assessment> assessmentWrapper = new ArrayWrapper<>(this);
    private final ArrayWrapper<Task> taskWrapper = new ArrayWrapper<>(this);
    private ArrayList<ArrayWrapper<ClassTime>> classesForDay = new ArrayList<>(7);
    private boolean classesForDayValid = false;




    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
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


        final String CREATE_TABLE_ASSESSMENTS = "CREATE TABLE IF NOT EXISTS " +
                TABLE_ASSESSMENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ASSESSMENT_NAME + " VARCHAR, " +
                KEY_ASSESSMENT_DATE + " INTEGER, " +
                KEY_ASSESSMENT_REVISION + " VARCHAR, " +
                KEY_ASSESSMENT_COMPLETE + " BOOLEAN, " +
                KEY_ASSESSMENT_PERCENTAGE + " INTEGER, " +
                KEY_SUBJECT_ID + " INTEGER, " +
                "FOREIGN KEY(" + KEY_SUBJECT_ID + ") " + "REFERENCES " + TABLE_SUBJECTS + "(" + KEY_ID + ")) ";
        db.execSQL(CREATE_TABLE_ASSESSMENTS);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * What is to happen now-
     * All arraylist methods are accessed through the wrapper
     * Some type specific methods are accessed through the db helper
     * Database access only happens through the wrapper, this stops any confusion
     * and repetaed calls
     *
     */



    //Term methods
    private void remove(Term t) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TERMS,
                KEY_ID + " = " + t.getID(),
                null);
        db.close();
        Log.i(TAG, "Deleting term " + t.toString());
    }

    public Term getTerm(int id) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(TABLE_TERMS,
                TERM_COLUMNS,
                KEY_ID + "  = ?", //selections
                new String[]{String.valueOf(id)},
                null, //group by
                null, //having
                null, //order by
                null); //limit
        final Term term = new Term();
        if (cursor != null) {
            cursor.moveToFirst();
            term.setID(cursor.getInt(0));
            term.setName(cursor.getString(1));
            term.setStartDate(cursor.getLong(2));
            term.setEndDate(cursor.getLong(3));
            cursor.close();
        }
        db.close();
        Log.i(TAG, "Reading term " + term.toString());

        return term;
    }

    public ArrayWrapper<Term> getAllTerms() {
        if(termWrapper.isDataValid()) return termWrapper;
        final ArrayList<Term> result = new ArrayList<>();
        final String query = "SELECT  * FROM " + TABLE_TERMS;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(query, null);
        Term term;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    term = new Term();
                    term.setID(cursor.getInt(0));
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
        termWrapper.setData(result);
        return termWrapper;
    }

    public Term getCurrentTerm() {
        final SQLiteDatabase db = this.getWritableDatabase();
        final long CURRENT = new Date().getTime();
        final String QUERY = "SELECT  * FROM " + TABLE_TERMS +
                " WHERE " + KEY_START_DATE + " > " + CURRENT +
                " AND " + KEY_END_DATE + " < " + CURRENT;
        final Cursor cursor = db.rawQuery(QUERY, null);
        Term t = new Term();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                t.setID(cursor.getInt(0));
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

    private void addTerms(ArrayList<Term> toAdd) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            for(Term t : toAdd) {
                values.put(KEY_TERM_NAME, t.getName());
                values.put(KEY_START_DATE, t.getStartDate());
                values.put(KEY_END_DATE, t.getEndDate());
                db.insert(TABLE_TERMS, null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
        } catch(Exception e) {
            Log.i(TAG, "Error when bulk inserting terms");
        } finally {
            db.endTransaction();
        }
    }

    //End term methods


    //Start subject methods

    public Subject getSubject(int id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.query(TABLE_SUBJECTS,
                SUBJECT_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        final Subject subject = new Subject();
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                subject.setID(cursor.getInt(0));
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
        final Cursor cursor = db.query(TABLE_SUBJECTS,
                SUBJECT_COLUMNS,
                KEY_ID + "  = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        final Subject subject = new Subject();
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                subject.setID(cursor.getInt(0));
                subject.setName(cursor.getString(1));
                subject.setClassroom(cursor.getString(2));
                subject.setTeacher(cursor.getString(3));
                subject.setColor(cursor.getInt(4));
            }
            cursor.close();
        }
        return subject;
    }

    public ArrayWrapper<Subject> getAllSubjects() {
        if(subjectWrapper.isDataValid()) return subjectWrapper;
        final ArrayList<Subject> subjects = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_SUBJECTS;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(QUERY, null);
        Subject subject;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    subject = new Subject();
                    subject.setID(cursor.getInt(0));
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
        subjectWrapper.setData(subjects);
        return subjectWrapper;
    }


    private void remove(Subject s) {
        final SQLiteDatabase db = this.getWritableDatabase();
        Log.i(TAG, "Deleting subject " + s.toString());
        db.delete(TABLE_CLASS_TIMES,
                KEY_SUBJECT_ID + " = " + s.getID(),
                null);
        getAllClasses();

        db.delete(TABLE_TASKS_CURRENT,
                KEY_SUBJECT_ID + " = " + s.getID(),
                null);
        db.delete(TABLE_TASKS_ARCHIVE,
                KEY_SUBJECT_ID + " = " + s.getID(),
                null);

        db.delete(TABLE_SUBJECTS,
                KEY_ID + " = " + s.getID(),
                null);

        db.close();
        getAllClasses();
        getAllSubjects();
        getAllTasks();
    }

    private void addSubjects(ArrayList<Subject> toAdd) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            for(Subject s : toAdd) {
                values.put(KEY_SUBJECT_NAME, s.getName());
                values.put(KEY_CLASSROOM, s.getClassroom());
                values.put(KEY_TEACHER, s.getTeacher());
                values.put(KEY_COLOR, s.getColor());
                db.insert(TABLE_SUBJECTS, null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
        } catch(Exception e) {
            Log.i(TAG, "Error when bulk inserting subjects");
        } finally {
            db.endTransaction();
        }

    }
    //End subject methods


    //Start class methods

    public ClassTime getClass(int id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.query(TABLE_CLASS_TIMES,
                CLASS_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        final ClassTime time = new ClassTime();
        if (cursor != null) {
            cursor.moveToFirst();
            time.setID(cursor.getInt(0));
            time.setStartTime(cursor.getInt(1));
            time.setEndTime(cursor.getInt(2));
            time.setDay(cursor.getInt(3));
            time.setSubjectID(cursor.getInt(4));
            time.setSubject(getSubjectForData(db, time.getSubjectID()));
            cursor.close();
        }
        db.close();
        Log.i(TAG, "Returning class " + time.toString());

        return time;
    }

    private void remove(ClassTime ct) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS_TIMES,
                KEY_ID + " = " + ct.getID(),
                null);
        db.close();
        Log.i(TAG, "Removing class " + ct.toString());
    }

    public ArrayWrapper<ClassTime> getAllClasses() {
        if(classTimeWrapper.isDataValid()) return classTimeWrapper;
        final ArrayList<ClassTime> classes = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_CLASS_TIMES;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(QUERY, null);
        ClassTime time;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    time = new ClassTime();
                    time.setID(cursor.getInt(0));
                    time.setSubjectID(cursor.getInt(1));
                    time.setStartTime(cursor.getInt(2));
                    time.setEndTime(cursor.getInt(3));
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
        classTimeWrapper.setData(classes);
        Log.i(TAG, "Returning all classes" + classes.toString());
        return classTimeWrapper;
    }

    public ArrayWrapper<ClassTime> getClassesForDay(int day) {
        if(!classesForDayValid) {
            if(!classTimeWrapper.isDataValid()) getAllClasses();
            for(int i = 0; i < 7; i++) {
                classesForDay.add(new ArrayWrapper<ClassTime>(this));
            }
            for(ClassTime ct : classTimeWrapper.mData) {
                classesForDay.get(ct.getDay()).add(ct);
            }
            classesForDayValid = true;
        }
        return classesForDay.get(day);
    }

    public ArrayWrapper<ClassTime> getClassesToday() {
        return getClassesForDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1);
    }
    //End class methods


    //Begin task methods

    private Task getCurrentTask(int id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.query(TABLE_TASKS_CURRENT,
                TASK_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        final Task task = new Task();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                task.setID(cursor.getInt(0));
                task.setType(cursor.getInt(1));
                task.setTitle(cursor.getString(2));
                task.setDetail(cursor.getString(3));
                task.setStartDate(cursor.getLong(4));
                task.setEndDate(cursor.getLong(5));
                task.setShowReminder(cursor.getInt(6) > 0);
                task.setTime(cursor.getInt(7));
                task.setComplete(cursor.getInt(8) > 0);
                task.setPercentageComplete(cursor.getInt(9));
                task.setDateComplete(cursor.getInt(10));
                task.setSubjectID(cursor.getInt(11));
                task.setSubject(getSubjectForData(db, task.getSubjectID()));
            }
            cursor.close();
        }
        Log.i(TAG, "Reading current task " + task.toString());
        db.close();
        return task;
    }

    public ArrayWrapper<Task> getAllTasks() {
        if(taskWrapper.isDataValid()) return taskWrapper;
        final ArrayList<Task> tasks = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_TASKS_CURRENT;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(QUERY, null);
        Task task;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    task = new Task();
                    task.setID(cursor.getInt(0));
                    task.setType(cursor.getInt(1));
                    task.setTitle(cursor.getString(2));
                    task.setDetail(cursor.getString(3));
                    task.setStartDate(cursor.getLong(4));
                    task.setEndDate(cursor.getLong(5));
                    task.setShowReminder(cursor.getInt(6) > 0);
                    task.setTime(cursor.getInt(7));
                    task.setComplete(cursor.getInt(8) > 0);
                    task.setPercentageComplete(cursor.getInt(9));
                    task.setDateComplete(cursor.getInt(10));
                    task.setSubjectID(cursor.getInt(11));
                    task.setSubject(getSubjectForData(db, task.getSubjectID()));
                    tasks.add(task);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        taskWrapper.setData(tasks);
        db.close();
        Log.i(TAG, "Current tasks " + tasks.toString());
        return taskWrapper;
    }

    private void remove(Task task) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS_CURRENT,
                KEY_ID + " = " + task.getID(),
                null);
        db.close();
        Log.i(TAG, "Deleting a task " + task.toString());
    }
    //End task methods

    public Assessment getAssessment(int id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.query(TABLE_ASSESSMENTS,
                ASSESSMENT_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        final Assessment assessment = new Assessment();
        if(cursor != null) {
            cursor.moveToFirst();
            assessment.setID(cursor.getInt(0));
            assessment.setName(cursor.getString(1));
            assessment.setDate(cursor.getLong(2));
            assessment.setRevision(cursor.getString(3));
            assessment.setComplete(cursor.getInt(4) > 0);
            assessment.setPercentage(cursor.getInt(5));
            assessment.setSubjectID(cursor.getInt(6));
            assessment.setSubject(getSubjectForData(db, assessment.getSubjectID()));
            cursor.close();
        }
        db.close();
        return assessment;
    }

    public ArrayWrapper<Assessment> getAllAssessments() {
        if(assessmentWrapper.isDataValid()) return assessmentWrapper;
        final ArrayList<Assessment> assessments = new ArrayList<>();
        final String QUERY = "SELECT * FROM " + TABLE_ASSESSMENTS;
        final SQLiteDatabase db = this.getWritableDatabase();
        final Cursor cursor = db.rawQuery(QUERY, null);
        Assessment assessment;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    assessment = new Assessment();
                    cursor.moveToFirst();
                    assessment.setID(cursor.getInt(0));
                    assessment.setName(cursor.getString(1));
                    assessment.setDate(cursor.getLong(2));
                    assessment.setRevision(cursor.getString(3));
                    assessment.setComplete(cursor.getInt(4) > 0);
                    assessment.setPercentage(cursor.getInt(5));
                    assessment.setSubjectID(cursor.getInt(6));
                    assessment.setSubject(getSubjectForData(db, assessment.getSubjectID()));
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        assessmentWrapper.setData(assessments);
        db.close();
        Log.i(TAG, "Assessments " + assessments.toString());
        return assessmentWrapper;
    }

    private void remove(Assessment assessment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSESSMENTS,
                KEY_ID + " = " + assessment.getID(),
                null);
        db.close();
        Log.i(TAG, "Deleting an assessment " + assessment.toString());

    }

    private int add(Data data) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        int id = -1;
        if(data instanceof Task) {
            Task t = (Task) data;
            values.put(KEY_TYPE, t.getType());
            values.put(KEY_TASK_TITLE, t.getTitle());
            values.put(KEY_TASK_DETAIL, t.getDetail());
            values.put(KEY_START_DATE, t.getStartDate());
            values.put(KEY_END_DATE, t.getEndDate());
            values.put(KEY_SHOW_REMINDER, t.getShowReminder());
            values.put(KEY_TIME, t.getTime());
            values.put(KEY_COMPLETE, t.isComplete());
            values.put(KEY_PERCENT_COMPLETE, t.getPercentageComplete());
            values.put(KEY_DATE_COMPLETE, t.getDateComplete());
            values.put(KEY_SUBJECT_ID, t.getSubjectID());
            id = (int) db.insert(TABLE_TASKS_CURRENT, null, values);
            Log.i(TAG, "Adding " + t.toString());
        } else if(data instanceof ClassTime) {
            ClassTime ct = (ClassTime) data;
            values.put(KEY_SUBJECT_ID, ct.getSubjectID());
            values.put(KEY_START_TIME, ct.getStartTime());
            values.put(KEY_END_TIME, ct.getEndTime());
            values.put(KEY_DAY, ct.getDay());
            id = (int) db.insert(TABLE_CLASS_TIMES, null, values);
            Log.i(TAG, "Adding " + ct.toString());
        } else if(data instanceof Subject) {
            Subject s = (Subject) data;
            values.put(KEY_SUBJECT_NAME, s.getName());
            values.put(KEY_CLASSROOM, s.getClassroom());
            values.put(KEY_TEACHER, s.getTeacher());
            values.put(KEY_COLOR, s.getColor());
            id = ((int) db.insert(TABLE_SUBJECTS, null, values));
            Log.i(TAG, "Adding " + s.toString());
        } else if(data instanceof Assessment) {
            Assessment a = (Assessment) data;
            values.put(KEY_ASSESSMENT_NAME, a.getName());
            values.put(KEY_ASSESSMENT_DATE, a.getDate());
            values.put(KEY_ASSESSMENT_REVISION, a.getRevision());
            values.put(KEY_ASSESSMENT_COMPLETE, a.isComplete());
            values.put(KEY_ASSESSMENT_PERCENTAGE, a.getPercentage());
            values.put(KEY_SUBJECT_ID, a.getSubjectID());
            id = (int) db.insert(TABLE_ASSESSMENTS, null, values);
            Log.i(TAG, "Adding " + a.toString());
        } else if(data instanceof Term) {
            Term t = (Term) data;
            values.put(KEY_TERM_NAME, t.getName());
            values.put(KEY_START_DATE, t.getStartDate());
            values.put(KEY_END_DATE, t.getEndDate());
            id = ((int) db.insert(TABLE_TERMS, null, values));
            Log.i(TAG, "Adding " + t.toString());
        }
        db.close();
        return id;
    }

    private void addAll(ArrayList<?> o) {
        if(!o.isEmpty()) {
            if(o.get(0) instanceof Task) {
            } else if(o.get(0) instanceof ClassTime) {
            } else if(o.get(0) instanceof Subject) {
                try {
                    addSubjects((ArrayList<Subject>) o);
                    subjectWrapper.clear();
                    getAllSubjects();
                } catch(Exception e) {
                    Log.e(TAG, "Cast to subject arraylist failed");
                }

            } else if(o.get(0) instanceof Assessment) {
            } else if(o.get(0) instanceof Term) {
            }
        }
    }

    private void update(Object o) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        if(o instanceof Task) {
            final Task t = (Task) o;
            values.put(KEY_TYPE, t.getType());
            values.put(KEY_TASK_TITLE, t.getTitle());
            values.put(KEY_TASK_DETAIL, t.getDetail());
            values.put(KEY_START_DATE, t.getStartDate());
            values.put(KEY_END_DATE, t.getEndDate());
            values.put(KEY_SHOW_REMINDER, t.getShowReminder());
            values.put(KEY_TIME, t.getTime());
            values.put(KEY_COMPLETE, t.isComplete());
            values.put(KEY_PERCENT_COMPLETE, t.getPercentageComplete());
            values.put(KEY_SUBJECT_ID, t.getSubjectID());
            values.put(KEY_DATE_COMPLETE, t.getDateComplete());
            db.update(TABLE_TASKS_CURRENT,
                    values,
                    KEY_ID + " = " + t.getID(),
                    null);
        } else if(o instanceof ClassTime) {
            final ClassTime ct = (ClassTime) o;
            values.put(KEY_SUBJECT_ID, ct.getSubjectID());
            values.put(KEY_START_TIME,  ct.getStartTime());
            values.put(KEY_END_TIME,  ct.getEndTime());
            values.put(KEY_DAY,  ct.getDay());
            db.update(TABLE_CLASS_TIMES,
                    values,
                    KEY_ID + " = " +  ct.getID(),
                    null);
        } else if(o instanceof Subject) {
            final Subject s = (Subject) o;
            values.put(KEY_SUBJECT_NAME, s.getName());
            values.put(KEY_CLASSROOM, s.getClassroom());
            values.put(KEY_TEACHER, s.getTeacher());
            values.put(KEY_COLOR, s.getColor());
            db.update(TABLE_SUBJECTS,
                    values,
                    KEY_ID + " = " + s.getID(),
                    null);
        } else if(o instanceof Assessment) {
            final Assessment a = (Assessment) o;
            values.put(KEY_ASSESSMENT_NAME, a.getName());
            values.put(KEY_ASSESSMENT_DATE, a.getDate());
            values.put(KEY_ASSESSMENT_REVISION, a.getRevision());
            values.put(KEY_ASSESSMENT_COMPLETE, a.isComplete());
            values.put(KEY_ASSESSMENT_PERCENTAGE, a.getPercentage());
            values.put(KEY_SUBJECT_ID, a.getSubjectID());
            db.update(TABLE_ASSESSMENTS,
                    values,
                    KEY_ID + " = " + a.getID(),
                    null);
        } else if(o instanceof Term) {
            final Term t = (Term) o;
            values.put(KEY_TERM_NAME, t.getName());
            values.put(KEY_START_DATE, t.getStartDate());
            values.put(KEY_END_DATE, t.getEndDate());
            db.update(TABLE_TERMS,
                    values,
                    KEY_ID + " = " + t.getID(),
                    null);
        }
        db.close();
    }

    private void remove(Object o) {
        if(o instanceof Task) {
            remove((Task) o);
        } else if(o instanceof ClassTime) {
            remove((ClassTime) o);
        } else if(o instanceof Subject) {
            remove((Subject) o);
        } else if(o instanceof Assessment) {
            remove((Assessment) o);
        } else if(o instanceof Term) {
            remove((Term) o);
        }
    }

    /**
     * Everything below this point is abstract wrappers and interfaces
     */

    /**
     * The ArrayChangeListener interface is used to listen for any change in the
     * data in an ArrayList
     * @param <T> The type being held in the wrapper which is being listened to
     */

    public interface ArrayChangeListener<T extends Comparable<T>> {

        void dataSetChanged();

        void dataSorted();

        void add(T t);

        void add(int index, T t);

        void set(int index, T t);

        void moved(int oldIndex, int newIndex);

        void updated(int index, T t);

        void removed(int index, T t);

        void cleared();

    }


    public class ArrayWrapper<T extends Data & Comparable<T>> {
        private DBHelper mDBHelper;
        private boolean mDataValid;
        private ArrayList<T> mData = new ArrayList<>();
        private ArrayList<ArrayChangeListener<T>> mListeners = new ArrayList<>();

        public ArrayWrapper(DBHelper dbHelper) {
            this.mDBHelper = dbHelper;
        }

        public boolean isDataValid() {
            return mDataValid;
        }

        public void addListener(ArrayChangeListener<T> listener) {
            mListeners.add(listener);
        }

        public void removeListener(ArrayChangeListener<T> listener) {
            mListeners.remove(listener);
        }

        public void removeAllListeners() {
            mListeners.clear();
        }

        public void setData(ArrayList<T> data) {
            this.mData = data;
            mDataValid = true;
        }

        public Iterator<T> iterator() {
            return mData.iterator();
        }

        public  void sort() {
            Collections.sort(mData);
        }

        public boolean isEmpty() {
            return mData.isEmpty();
        }

        public int size() {
            return mData.size();
        }

        public boolean contains(T t) {
            return mData.contains(t);
        }

        public int indexOf(T t) {
            return mData.indexOf(t);
        }

        public int lastIndexOf(T t) {
            return mData.lastIndexOf(t);
        }

        public T get(int index) {
            return mData.get(index);
        }

        public T getWithID(int id) {
            for(T t : mData) {
                if(t.getID() == id) return t;
            }
            return null;
        }

        public void set(int index, T t) {
            mData.set(index, t);
            for(ArrayChangeListener<T> l : mListeners) l.set(index, t);
            mDBHelper.add(t);
        }

        public void add(T t) {
            mData.add(t);
            for(ArrayChangeListener<T> l : mListeners) l.add(t);
            t.setID(mDBHelper.add(t));
        }

        public void add(int index, T t) {
            mData.add(index, t);
            for(ArrayChangeListener<T> l : mListeners) l.add(index, t);
            t.setID(mDBHelper.add(t));
        }

        public void addAll(ArrayList<T> toAdd) {
            mData.addAll(toAdd);
            mDBHelper.addAll(toAdd);
        }

        public void addToPos(T t) {
            int pos;
            for(pos = 0; pos < mData.size(); pos++) {
                if(t.compareTo(mData.get(pos)) < 0) break;
            }
            add(pos, t);
        }

        public void move(int oldIndex, int newIndex) {
            final T t = mData.get(oldIndex);
            mData.remove(t);
            if(oldIndex > newIndex) {
                mData.add(newIndex, t);
                for(ArrayChangeListener<T> l : mListeners) l.moved(oldIndex, newIndex);
            } else {
                mData.add(newIndex-1, t);
                for(ArrayChangeListener<T> l : mListeners) l.moved(newIndex-1, oldIndex);
            }
        }

        public void update(T t) {
            mData.set(mData.indexOf(t), t);
            for(ArrayChangeListener<T> l : mListeners) l.updated(mData.indexOf(t), t);
            mDBHelper.update(t);
        }

        public void remove(T t) {
            final int index = mData.indexOf(t);
            mData.remove(index);
            for(ArrayChangeListener<T> l : mListeners) l.removed(index, t);
            mDBHelper.remove(t);
        }

        public void remove(int index) {
            final T t = mData.get(index);
            mData.remove(index);
            for(ArrayChangeListener<T> l : mListeners) l.removed(index, t);
            mDBHelper.remove(t);
        }

        public void removeAll(T t) {
            for(int i = mData.indexOf(t); i >= 0; i = mData.indexOf(t)) mData.remove(i);
            for(ArrayChangeListener<T> l : mListeners) l.dataSetChanged();
            mDBHelper.remove(t);
        }

        public void clear() {
            mData.clear();
            mDataValid = false;
            for(ArrayChangeListener<T> l : mListeners) l.cleared();
        }

        public int getPosOfID(int id) {
            for(int i = 0; i < mData.size(); i++) {
                if(mData.get(i).getID() == id) return i;
            }
            return -1;
        }

        @Override
        public String toString() {
            return "ArrayWrapper " + mData.toString();
        }
    }

}
