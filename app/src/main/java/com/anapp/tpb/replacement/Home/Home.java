package com.anapp.tpb.replacement.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Debug;
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
import android.widget.Toast;

import com.anapp.tpb.replacement.Home.Fragments.Tasks.TaskFragment;
import com.anapp.tpb.replacement.Home.Fragments.Today.TodayFragment;
import com.anapp.tpb.replacement.Home.Input.AssessmentInput;
import com.anapp.tpb.replacement.Home.Input.HomeworkInput;
import com.anapp.tpb.replacement.Home.Interfaces.ClassOpener;
import com.anapp.tpb.replacement.Home.Interfaces.TaskOpener;
import com.anapp.tpb.replacement.Home.Utilities.SheetFab;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataPresentation.ClassTimeCollector;
import com.anapp.tpb.replacement.Setup.DataPresentation.SubjectCollector;
import com.anapp.tpb.replacement.Setup.IntroActivity;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;

public class Home extends AppCompatActivity implements ClassOpener, TaskOpener {
    private static final String TAG = "Home";
    private static String[] titles = new String[] {"Today", "Tasks", "Timetable"};
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DataHelper dataHelper;

    private MaterialSheetFab fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.startMethodTracing("ReplacementTrace");

        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        dataHelper = new DataHelper(this);
        SpectrumDialog.Builder b = new SpectrumDialog.Builder(getApplicationContext());
        Resources r = getResources();
        int[] colors = new int[] {r.getColor(R.color.amber_500), r.getColor(R.color.blue_500), r.getColor(R.color.blue_grey_500),
                r.getColor(R.color.brown_500), r.getColor(R.color.cyan_500), r.getColor(R.color.deep_orange_500), r.getColor(R.color.deep_purple_500),
                r.getColor(R.color.green_500), r.getColor(R.color.indigo_500), r.getColor(R.color.lime_500), r.getColor(R.color.teal_500),
                r.getColor(R.color.yellow_500), r.getColor(R.color.red_500), r.getColor(R.color.pink_500), r.getColor(R.color.light_blue_500)};
        b.setColors(colors);
        b.setDismissOnColorSelected(false);
        b.build().show(getSupportFragmentManager(), "");


        if(pref.getBoolean("firststart", true)) setUpTestData();

        if(pref.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.apply();
            Intent i = new Intent(getApplicationContext(), IntroActivity.class);
            //startActivity(i);

        } else {
            long start = System.nanoTime();
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

            fab = new MaterialSheetFab(sFab, sheetView, overlay, sheetColor, fabColor);
            Log.i(TAG, "Time " + (System.nanoTime()-start)/1E9);
        }
        Debug.stopMethodTracing();
    }


    public void newTask(View v) {

    }

    //Adds arguments for SlidingActivity to start from a button
    public void setExpandLocation(View v, Intent i) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        i.putExtra("leftOffset", location[0]);
        i.putExtra("topOffset", location[1]);
        i.putExtra("viewWidth", v.getWidth());
        i.putExtra("viewHeight", v.getHeight());
    }

    public void newHomework(View v) {
        Intent i = new Intent(this, HomeworkInput.class);
        setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }

    public void newReminder(View v) {

    }

    public void newAssessment(View v) {
        Intent i = new Intent(this, AssessmentInput.class);
        setExpandLocation(v, i);
        startActivityForResult(i, 1);
    }


    @Override
    public void openTask(Task t) {

    }

    @Override
    public void openReminder(Task r) {

    }

    @Override
    public void openHomework(Task h) {

    }

    @Override
    public void openClass(ClassTime c) {
        Log.i("ClassOpener", "Opening class " + c);
    }

    @Override
    public void onBackPressed () {
        if(fab.isSheetVisible()) {
            fab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fab.hideSheet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), SubjectCollector.class);
                i.putExtra("nextWindow", ClassTimeCollector.class);
                startActivity(i);
                return true;
            case R.id.action_edit:
                Toast.makeText(getApplicationContext(),
                        "This should bring up the correct editor", Toast.LENGTH_LONG).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart () {
        //DataHelper may be null when application is restarted
        dataHelper = new DataHelper(this);
        super.onStart();

    }

    private void setUpTestData() {
        ArrayList<Integer> subIDs = new ArrayList<>();
        Subject s = new Subject();
        s.setClassroom("L4.14");
        s.setTeacher("TMM");
        s.setName("Further maths");
        s.setColor(0xff0000ff);
        s = dataHelper.addSubject(s);
        subIDs.add(s.getId());
        s.setClassroom("L0.25");
        s.setTeacher("RJS");
        s.setName("Maths");
        s.setColor(0xff00ffff);
        s = dataHelper.addSubject(s);
        subIDs.add(s.getId());
        s.setClassroom("L2.09");
        s.setTeacher("MQL");
        s.setName("Computer science");
        s.setColor(0xffccebcc);
        s = dataHelper.addSubject(s);
        subIDs.add(s.getId());
        s.setClassroom("L4.08");
        s.setTeacher("SWG");
        s.setName("Physics");
        s.setColor(0xffdd00ff);
        s = dataHelper.addSubject(s);
        subIDs.add(s.getId());
        s.setClassroom("L1.10");
        s.setTeacher("SJY");
        s.setName("Physics");
        s.setColor(0xffcdff00);
        s = dataHelper.addSubject(s);
        subIDs.add(s.getId());
        ClassTime classTime = new ClassTime();
        for(int i = 0; i < 7; i++) {
            classTime.setDay(i);
            for(int j = 0;j < 24; j++) {
                classTime.setStart(100 * j);
                classTime.setEnd((100* (j+1)));
                classTime.setSubjectID( subIDs.get( (int) (Math.random() * subIDs.size())) );
                dataHelper.addClass(classTime);
            }
        }
        Task t = new Task(2);
        t.setComplete(false);
        t.setStartDate(1462205968139L);
        t.setEndDate(1462209968139L);
        t.setTitle("Test work");
        t.setDetail("This is some text \n This is the second line\nThis is the third line\nThis is the fourth line\nThis is the fifth line");
        t.setPercentageComplete(0);
        t.setSubjectID(1);
        dataHelper.addTask(t);
        t.setTitle("Another piece of test work");
        t.setSubjectID(2);
        t.setDetail("A shorter piece of detail without new line");
        dataHelper.addTask(t);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return TodayFragment.newInstance();
            } else {
                return TaskFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


}
