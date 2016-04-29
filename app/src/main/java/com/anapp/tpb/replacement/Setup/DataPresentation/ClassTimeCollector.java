package com.anapp.tpb.replacement.Setup.DataPresentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;


//TODO- Redo all of this on friday
public class ClassTimeCollector extends AppCompatActivity {
    private static final String TAG = "ClassTimeCollector";
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private ArrayList<Subject> subjects;
    private ArrayList<ClassTime> classes;
    private ArrayList<ClassRecyclerFragment> fragments;
    private DataHelper storageHelper;
    private int day;
    private FloatingActionButton addClassFab;
    private FloatingActionButton nextFab;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Class nextWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);

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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected (int position) {
                if(position != day && position == 4) {
                    updateFab(true);
                } else if(day == 4) {
                    updateFab(false);
                }
                day = position;
            }

            @Override
            public void onPageScrollStateChanged (int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        addClassFab = (FloatingActionButton) findViewById(R.id.addClassFab);
        nextFab = (FloatingActionButton) findViewById(R.id.addClassFinishFab);
        addClassFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), com.anapp.tpb.replacement.Setup2.Input.ClassInput.class);
                Log.i(TAG, "Adding subjects " + subjects);
                i.putExtra("subjects", subjects);
                Log.d("Classes", "Classes with intent " + classes);
                i.putExtra("classes", storageHelper.getClassesForDay(day));
                i.putExtra("day", day);
                startActivityForResult(i, 1);
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getApplicationContext(), nextWindow);
                startActivity(i);
            }
        });

        nextWindow = (Class) getIntent().getSerializableExtra("nextWindow");
        storageHelper = new DataHelper(this);
        classes = storageHelper.getAllClasses();
        subjects = storageHelper.getAllSubjects();
    }

    //Method is used to give the ClassTimeCollector access to the fragments
    public void addFragment(ClassRecyclerFragment f) {
        fragments.add(f);
    }

    public void removeClass(ClassTime toRemove) {
        Log.d("Removing class", "Removing " + toRemove);
        int index = classes.indexOf(toRemove);
        if(index >= 0) {
            classes.remove(index);
        }
    }

    public void updateFab(boolean done) {
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics()); //Height of fab + fab margin into pixels
        TranslateAnimation move;
        if(done) {
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
                    addClassFab.setY(nextFab.getY() - px);
                    //The animation below stops the flickering/jump once the animation completes. No idea why
                    animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    animation.setDuration(1);
                    addClassFab.startAnimation(animation);
                }
            });
            move.setDuration(300);
            addClassFab.startAnimation(move);
            nextFab.setVisibility(View.VISIBLE);
        } else {
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
                    addClassFab.setY(nextFab.getY());
                    animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    animation.setDuration(1);
                    addClassFab.startAnimation(animation);
                }
            });
            move.setDuration(300);
            addClassFab.startAnimation(move);
            nextFab.setVisibility(View.INVISIBLE);
        }
    }

    //Method used by viewpage to change controls in parent


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ClassTime c = (ClassTime) data.getSerializableExtra("class");
            Log.d("Data", "Received class with values of " + c.toString());
            //-2 as day are indexed from 0 at saturday, whereas pager sections are 0 at monday
            ClassRecyclerFragment f = fragments.get(c.getDay()-2);
            if (data.getBooleanExtra("edited", false)) {
                f.mAdapter.updateClassValue(c);
                int index = classes.indexOf(c);
                classes.set(index, c);
            } else {
                f.mAdapter.addClass(c);
                classes.add(c);
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

    //Classes for paging through different days
    public static class ClassRecyclerFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private ClassTimeAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private ClassTimeCollector parent;
        private DataHelper storageHelper;
        private ArrayList<Subject> subjects;
        private int sectionNumber;

        public ClassRecyclerFragment() { //Empty public constructor
        }

        public static ClassRecyclerFragment newInstance(ClassTimeCollector parent, int sectionNumber, DataHelper storageHelper, ArrayList<Subject> subjects) {
            ClassRecyclerFragment fragment = new ClassRecyclerFragment();
            fragment.setParent(parent); //The fragment needs a parent in order to communicate data
            Bundle args = new Bundle();
            fragment.setSectionNumber(sectionNumber);
            fragment.setArguments(args);
            fragment.setStorageHelper(storageHelper);
            fragment.setSubjects(subjects);
            parent.addFragment(fragment); //Passing the fragment back to its parent.
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_class_input, container, false);
            mAdapter = new ClassTimeAdapter(parent, storageHelper, subjects, sectionNumber);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.dayClassTimeRecycler);
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
        private ClassTimeCollector parent;

        public SectionsPagerAdapter(ClassTimeCollector parent, FragmentManager fm) {
            super(fm);
            this.parent = parent;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d("Data", "Change in primary position,  position is " + position);
            parent.day = position + 2;
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
