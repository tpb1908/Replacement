package com.tpb.timetable.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
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
import com.tpb.timetable.Home.Interfaces.FABManager;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.UIHelper;

import java.util.ArrayList;

/*TODO https://android-arsenal.com/details/1/3086
* https://android-arsenal.com/details/1/2323
 */


public class Home extends AppCompatActivity implements ClassOpener, TaskManager, Themable, FABManager {
    private static final String TAG = "Home";
    private static final String[] titles = new String[] {"Today", "Tasks", "Timetable"};
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DBHelper mDB;
    private TodayFragment mTodayFragment;
    private TaskFragment mTaskFragment;
    private MaterialSheetFab mFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        UIHelper.setTaskDescription(this);
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setting up pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setBackgroundColor(UIHelper.getPrimary());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
            //SheetFab for adding items
        final SheetFab sFab= (SheetFab) findViewById(R.id.sheetFab);
        //UIHelper.themeFAB(sFab);
        final View sheetView = findViewById(R.id.fabSheet);
        final View overlay = findViewById(R.id.overlay);
        mFAB = new MaterialSheetFab(sFab, sheetView, overlay, UIHelper.getPrimary(), UIHelper.getAccent());
        mFAB.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    final AppBarLayout layout = (AppBarLayout) findViewById(R.id.appbar);
                    layout.setExpanded(false);
                }
                super.onShowSheet();
            }

            @Override
            public void onHideSheet() {
                super.onHideSheet();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    final AppBarLayout layout = (AppBarLayout) findViewById(R.id.appbar);
                    layout.setExpanded(true);
                }
            }
        });
        UIHelper.theme((ViewGroup) findViewById(R.id.background));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFAB.isSheetVisible()) {
            mFAB.hideSheet();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart () {
        mDB = DBHelper.getInstance(this);
        super.onStart();
    }

    @Override
    public void onBackPressed () {
        if(mFAB.isSheetVisible()) {
            mFAB.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    public void newHomework(View v) {
        final Intent i = new Intent(this, HomeworkInput.class);
        UIHelper.setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }

    public void newReminder(View v) {
        final Intent i = new Intent(this, ReminderInput.class);
        UIHelper.setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }

    public void newTask(View v) {

    }

    public void newAssessment(View v) {
        final Intent i = new Intent(this, AssessmentInput.class);
        UIHelper.setExpandLocation(v, i);
        startActivity(i);
    }


    //Begin interface methods
    @Override
    public void showFAB() {
        mFAB.showFab();
    }

    @Override
    public void hideFAB() {
        mFAB.hideSheetThenFab();
    }

    @Override
    public ViewGroup getViewGroup() {
        return (ViewGroup) findViewById(R.id.background);
    }

    @Override
    public void openTask(Task t, View v) {}

    @Override
    public void openReminder(Task r, View v) {}

    @Override
    public void openHomework(Task h, View v) {
        final Intent i = new Intent(this, HomeworkInput.class);
        UIHelper.setExpandLocation(v, i);
        i.putExtra("task", h);
        startActivityForResult(i, 1);
    }

    @Override
    public void countChange(int previousCount, int newCount) {}

    @Override
    public void showDeleteSnackBar(final Task t) {
        final CoordinatorLayout snackBarLayout = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        final Snackbar snackbar = Snackbar
                .make(snackBarLayout, t.getTypeName() + " deleted",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDB.getAllTasks().addToPosition(t);
                    }
                });
        snackbar.show();
    }
    //End Interface methods


    @Override
    public void openClass(ClassTime c) {
    }
    
    private void testSubjects() {
        final ArrayList<Subject> subjects = new ArrayList<>();
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
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if(position == 0) showFAB();
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


}
