package com.anapp.tpb.replacement.Setup.DataPresentation;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.Adapters.ClassTimeAdapter;
import com.anapp.tpb.replacement.Setup.DataCollection.ClassInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

public class ClassTimeCollector extends AppCompatActivity {
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private ArrayList<Subject> subjects;
    private ArrayList<ClassTime> classes;
    private ArrayList<ClassRecyclerFragment> fragments;
    private DataHelper storageHelper;
    private int day;



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_time_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Subject times");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        fragments = new ArrayList<>();
        day = 0;

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClassInput.class);
                i.putExtra("subjects", subjects);
                i.putExtra("classes", classes);
                i.putExtra("day", day);
                startActivityForResult(i, 1);
            }
        });

        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");

        storageHelper = new DataHelper(this);
        classes = storageHelper.getAllClasses();
    }

    public void addFragment(ClassRecyclerFragment f) {
        fragments.add(f);
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result received", "From ClassInput");
        if (resultCode == RESULT_OK) {
            ClassTime c = (ClassTime) data.getSerializableExtra("class");
            Log.d("ClassTime", "Day of " + c.getDay());
            if (data.getBooleanExtra("edited", false)) {
                for (ClassRecyclerFragment f : fragments) {
                    if (f.sectionNumber == c.getDay()) {
                        f.mAdapter.updateClassValue(c);
                    }
                }
            } else {
                for (ClassRecyclerFragment f : fragments) {
                    Log.d("Day of " + f.sectionNumber, "Class day of " + c.getDay());
                    if (f.sectionNumber == c.getDay()) {
                        f.mAdapter.addClass(c);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lesson_time_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ClassRecyclerFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private ClassTimeAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private ClassTimeCollector parent;
        private DataHelper storageHelper;
        private ArrayList<Subject> subjects;
        private int sectionNumber;

        public ClassRecyclerFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ClassRecyclerFragment newInstance(ClassTimeCollector parent, int sectionNumber, DataHelper storageHelper, ArrayList<Subject> subjects) {
            ClassRecyclerFragment fragment = new ClassRecyclerFragment();
            fragment.setParent(parent);
            Bundle args = new Bundle();
            fragment.setSectionNumber(sectionNumber);
            fragment.setArguments(args);
            fragment.setStorageHelper(storageHelper);
            fragment.setSubjects(subjects);
            fragment.parent.addFragment(fragment);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lesson_time_input, container, false);
            mAdapter = new ClassTimeAdapter(parent, storageHelper, subjects, sectionNumber);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lessonTimeRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(parent);
            mRecyclerView.setLayoutManager(mLayoutManager);

            return rootView;
        }

        public void setParent(ClassTimeCollector parent) {
            this.parent = parent;
        }

        public void setSubjects(ArrayList<Subject> subjects) {
            this.subjects = subjects;
        }

        public void setStorageHelper(DataHelper storageHelper) {
            this.storageHelper = storageHelper;
        }

        public void setSectionNumber(int sectionNumber) {
            this.sectionNumber = sectionNumber;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ClassTimeCollector parent;

        public SectionsPagerAdapter(ClassTimeCollector parent, FragmentManager fm) {
            super(fm);
            this.parent = parent;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d("Primary item changed", "Position is " + position);
            parent.setDay(position);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ClassRecyclerFragment (defined as a static inner class above).
            return ClassRecyclerFragment.newInstance(parent, position, storageHelper, subjects);
        }

        @Override
        public int getCount() {
            return DAYS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DAYS[position];
        }


    }
}
