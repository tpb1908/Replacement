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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_time_input);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Class times");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the sections
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

    //Method is used to give the ClassTimeCollector access to the fragments
    public void addFragment(ClassRecyclerFragment f) {
        fragments.add(f);
    }

    //Method used by viewpage to change controls in parent
    public void setDay(int day) {
        if (this.day != day) {  //Ignoring the multiple calls from the viewpager
            final FloatingActionButton fabEnd = (FloatingActionButton) findViewById(R.id.fabEnd);
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            //DP conversion  http://stackoverflow.com/questions/30202379/android-views-gettop-getleft-getx-gety-getwidth-getheight-meth
            final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics()); //Height of fab + fab margin into pixels

            TranslateAnimation move;
            /* The TranslationAnimation works in pixels rather than DP, so the value is converted above
            * The screen grid starts from the top left, so in order to shift the fab up enough to let the fabEND
            * take its place, and leave the correct margin, the DP value is - (height of fab (56dp) + fab marge (16dp))
            * In order to actually set the value position of the FAB (its click location), a call to fab.setY() must be used
            * As in order to keep its location constant, it needs a default location, which is the Y value of the fabEnd
             */

            if (day == 4) { //When the day has been changed to 4 (Friday)
                move = new TranslateAnimation(0, 0, 0, -px);
                move.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setY(fabEnd.getY() - px);
                        //The animation below stops the flickering/jump once the animation completes. No idea why
                        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                        animation.setDuration(1);
                        fab.startAnimation(animation);
                    }
                });
                move.setDuration(300);
                fab.startAnimation(move);
                fabEnd.setVisibility(View.VISIBLE);
            } else if (this.day == 4) { //When the current day is 4, and has not yet been changed
                move = new TranslateAnimation(0, 0, 0, px);
                move.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setY(fabEnd.getY());
                        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                        animation.setDuration(1);
                        fab.startAnimation(animation);
                    }
                });
                move.setDuration(300);
                fab.startAnimation(move);
                fabEnd.setVisibility(View.INVISIBLE);
            }
        }
        this.day = day;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ClassTime c = (ClassTime) data.getSerializableExtra("class");
            Log.d("Data", "Received class with values of " + c.toString());
            for (ClassRecyclerFragment f : fragments) {
                //Finding the correct fragment to add the class to
                if (f.sectionNumber == c.getDay()) {
                    if (data.getBooleanExtra("edited", false)) {
                        f.mAdapter.updateClassValue(c);
                    } else {
                        f.mAdapter.addClass(c);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        public static ClassRecyclerFragment newInstance(ClassTimeCollector parent, int sectionNumber, DataHelper storageHelper, ArrayList<Subject> subjects) {
            ClassRecyclerFragment fragment = new ClassRecyclerFragment();
            fragment.setParent(parent); //The fragment needs a parent in order to communicate data
            Bundle args = new Bundle();
            fragment.setSectionNumber(sectionNumber);
            fragment.setArguments(args);
            fragment.setStorageHelper(storageHelper);
            fragment.setSubjects(subjects);
            parent.addFragment(fragment); //Passing the fragment back to its parent. Nobody like children
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_class_time_input, container, false);
            mAdapter = new ClassTimeAdapter(parent, storageHelper, subjects, sectionNumber);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lessonTimeRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(parent);
            mRecyclerView.setLayoutManager(mLayoutManager);

            return rootView;
        }

        //Setters for objects passed from parent

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
            Log.d("Data", "Change in primary position,  position is " + position);
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
