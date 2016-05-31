package com.tpb.timetable.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Home.Fragments.Tasks.TaskFragment;
import com.tpb.timetable.Home.Fragments.Today.TodayFragment;
import com.tpb.timetable.Home.Input.AssessmentInput;
import com.tpb.timetable.Home.Input.HomeworkInput;
import com.tpb.timetable.Home.Input.ReminderInput;
import com.tpb.timetable.Home.Interfaces.ClassOpener;
import com.tpb.timetable.Home.Interfaces.TaskOpener;
import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ColorResources;

import java.util.ArrayList;

/*TODO https://android-arsenal.com/details/1/3086
* https://android-arsenal.com/details/1/94
* https://android-arsenal.com/details/1/2323
 */


    /*Where I got to-
        The code runs, but none of the listeners are activated
        I need to got through and change the inputs to use the data change methods
        rather than updating through the onActivityResult method.
        Then redo the fucked up setup classes.

        Edit-
        The TaskAdapter listener works just fine, but the data helper listener is more difficult
        What I need to do is create an abstract class that ALL of my TableTemplate classes extend from
        and then get rid of the use of generics in the DataUpdateListener


        //TODO- Move all error messages to string resources
     */

public class Home extends AppCompatActivity implements ClassOpener, TaskOpener, Themable {
    private static final String TAG = "Home";
    private static String[] titles = new String[] {"Today", "Tasks", "Timetable"};
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DBHelper mDB;
    private TodayFragment mTodayFragment;
    private TaskFragment mTaskFragment;
    private MaterialSheetFab mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        ColorResources.getColorResources(this, null);
        mDB = DBHelper.getInstance(this);
        if(pref.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.apply();
            testSubjects();
            //Intent i = new Intent(getApplicationContext(), IntroActivity.class);
            //startActivity(i);
        }
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setting up pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
            //SheetFab for adding items
        SheetFab sFab= (SheetFab) findViewById(R.id.sheetFab);
        View sheetView = findViewById(R.id.fabSheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.colorAccent);
        int fabColor = getResources().getColor(R.color.colorAccent);
        mFab = new MaterialSheetFab(sFab, sheetView, overlay, sheetColor, fabColor);

    }


    @Override
    public void setDarkTheme(boolean used) {

    }

    @Override
    public ViewGroup getViews() {
        return (ViewGroup) getWindow().getDecorView();
    }

    //Adds arguments for SlidingActivity to start from a button
    public void setExpandLocation(View v, Intent i) {
        if(v != null) {
            int[] location = new int[2];
            v.getLocationInWindow(location);
            i.putExtra("leftOffset", location[0]);
            i.putExtra("topOffset", location[1]);
            i.putExtra("viewWidth", v.getWidth());
            i.putExtra("viewHeight", v.getHeight());
            i.putExtra("hasOpenPosition", true);
        } else {
            i.putExtra("hasOpenPosition", false);
        }
    }

    public void newHomework(View v) {
        Intent i = new Intent(this, HomeworkInput.class);
        setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }

    public void newReminder(View v) {
        Intent i = new Intent(this, ReminderInput.class);
        setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }

    public void newTask(View v) {

    }

    public void newAssessment(View v) {
        Intent i = new Intent(this, AssessmentInput.class);
        setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }


    @Override
    public void openTask(Task t, View v) {

    }

    @Override
    public void openReminder(Task r, View v) {

    }

    @Override
    public void openHomework(Task h, View v) {
        Intent i = new Intent(this, HomeworkInput.class);
        setExpandLocation(v, i);
        i.putExtra("task", h);
        startActivityForResult(i, 1);
    }


    @Override
    public void openClass(ClassTime c) {
        Log.i("ClassOpener", "Opening class " + c);
    }

    @Override
    public void onBackPressed () {
        if(mFab.isSheetVisible()) {
            mFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFab.isSheetVisible()) {
            mFab.hideSheet();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart () {
        /*DBHelper may be null when application is restarted
        *If the DBHelper is not null, all this will do is return
        * the already instantiated singleton, so there's no real
        * performance concerns
         */
        mDB = DBHelper.getInstance(this);
        super.onStart();

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                mTodayFragment = TodayFragment.newInstance();
                return mTodayFragment;
            } else if(position == 1) {
                mTaskFragment = TaskFragment.newInstance();
                return mTaskFragment;
            } else {return null;}
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private void testSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();
        Subject s = new Subject();
        s.setName("Maths");
        s.setTeacher("Maths teacher");
        s.setClassroom("Maths classroom");
        s.setColor(getResources().getColor(R.color.blue_600));
        subjects.add(s);
        s =  new Subject();
        s.setName("Physics");
        s.setTeacher("Physics teacher");
        s.setClassroom("Physics classroom");
        s.setColor(getResources().getColor(R.color.red_700));
        subjects.add(s);
        s = new Subject();
        s.setName("Computer science");
        s.setTeacher("Computer science teacher");
        s.setClassroom("Computer science classroom");
        s.setColor(getResources().getColor(R.color.green_400));
        subjects.add(s);
        mDB.getAllSubjects().addAll(subjects);

    }



}
