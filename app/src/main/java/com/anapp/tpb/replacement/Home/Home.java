package com.anapp.tpb.replacement.Home;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anapp.tpb.replacement.Home.Fragments.Today.TodayFragment;
import com.anapp.tpb.replacement.Home.Utilities.SheetFab;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.IntroActivity;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

public class Home extends AppCompatActivity implements TodayFragment.TodayInterface {
    private static String[] titles = new String[] {"Today", "Tasks", "Timetable"};

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private DataHelper dataHelper;

    private MaterialSheetFab fab;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        dataHelper = new DataHelper(this);
//        Subject  s = new Subject();
//        s.setClassroom("Classroom");
//        s.setColor(getResources().getColor(R.color.colorAccent));
//        s.setName("Subject 1");
//        s.setTeacher("Teacher 1");
//        dataHelper.addSubject(s);
//        s.setClassroom("Classroom 2");
//        s.setColor(getResources().getColor(R.color.colorPrimaryDark));
//        s.setName("Subject 2");
//        s.setTeacher("Teacher 2");
//        dataHelper.addSubject(s);
//
//        Intent j = new Intent(getApplicationContext(), ClassTimeCollector.class);
//        startActivity(j);



        if(pref.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firststart", false);
            editor.apply();
            Intent i = new Intent(getApplicationContext(), IntroActivity.class);
            //startActivity(i);
        } else {
            setContentView(R.layout.activity_home);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            SheetFab sFab= (SheetFab) findViewById(R.id.sheetFab);
            View sheetView = findViewById(R.id.fabSheet);
            View overlay = findViewById(R.id.overlay);
            int sheetColor = getResources().getColor(R.color.colorAccent);
            int fabColor = getResources().getColor(R.color.colorAccent);

            fab = new MaterialSheetFab(sFab, sheetView, overlay, sheetColor, fabColor);

            fab.setEventListener(new MaterialSheetFabEventListener() {
                @Override
                public void onShowSheet () {
                    super.onShowSheet();
                }

                @Override
                public void onSheetHidden () {
                    super.onSheetHidden();
                }

                @Override
                public void onHideSheet () {
                    super.onHideSheet();
                }

                @Override
                public void onSheetShown () {
                    super.onSheetShown();
                }


            });


        }

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
    public void openClass (ClassTime t) {

    }

    @Override
    public void openTask (Task t) {

    }


    @Override
    public void openTest (Task t) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(i);
                return true;
            case R.id.action_edit:
                Toast.makeText(getApplicationContext(),
                        "This should bring up the correct editor", Toast.LENGTH_LONG).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return TodayFragment.newInstance(dataHelper);
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

    private void setUpTestData() {
        ClassTime c = new ClassTime();
        c.setStart(1100);
        c.setEnd(1400);
        c.setDay(1);
        c.setSubjectID(1);
        //c = dataHelper.addClass(c);
    }


}
