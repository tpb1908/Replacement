package com.anapp.tpb.replacement.Setup2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup2.Input.ClassInput;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;

import java.util.ArrayList;

//TODO- Use the previous method for getting fragment
public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private DataHelper dataHelper;
    private ViewPager mPager;
    private DayPager mPagerAdapter;
    private ArrayList<DayFragment> fragments;
    private DayFragment current;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);
        dataHelper = new DataHelper(this);
        mPagerAdapter = new DayPager(getSupportFragmentManager(), this, dataHelper);
        mPager = (ViewPager) findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        fragments = new ArrayList<>();
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected (int position) {
                current =  fragments.get(mPager.getCurrentItem());
                Log.i(TAG, "Fragment changed to " + current);
                Log.i(TAG, "Current recycler adapter of " + current.mAdapter);
            }

            @Override
            public void onPageScrollStateChanged (int state) {
            }
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
            }
        });
        FloatingActionButton addClassFab = (FloatingActionButton) findViewById(R.id.fab);
        addClassFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if(current == null) {
                    current =  fragments.get(mPager.getCurrentItem());
                }
                Log.i(TAG, "On Click- Current is " + current );
                Intent i = new Intent(getApplicationContext(), ClassInput.class);
                i.putExtra("subjects", dataHelper.getAllSubjects());
                i.putExtra("classes", current.mAdapter.getClasses());
                i.putExtra("day", current.day);
                startActivityForResult(i, 1);
            }
        });

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            ClassTime classTime = (ClassTime) data.getSerializableExtra("class");
            boolean edited = data.getBooleanExtra("edited", false);
            if(edited) {
                Log.i(TAG, "Edited class returned " + classTime);
                dataHelper.updateClass(classTime);
                current.updateClass(classTime);
            } else {
                Log.i(TAG, "New class returned " + classTime);
                classTime = dataHelper.addClass(classTime);
                current.addClass(classTime);
            }
        }
    }

    public static class DayPager extends FragmentStatePagerAdapter {
        private TestActivity parent;
        private DataHelper dataHelper;

        public DayPager(FragmentManager fragmentManager, TestActivity parent, DataHelper dataHelper) {
            super(fragmentManager);
            this.parent = parent;
            this.dataHelper = dataHelper;


        }

        @Override
        public Fragment getItem (int position) {
            DayFragment d = DayFragment.newInstance(parent, dataHelper, position+2);
            parent.fragments.add(d);
            return d;
        }

        @Override
        public int getCount () {
            return DAYS.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return DAYS[position];
        }
    }

    public static class DayFragment extends Fragment {
        private int day;
        private TestClassRecycler mAdapter;
        private RecyclerView mRecyclerView;
        private TestActivity parent;
        private DataHelper dataHelper;

        public DayFragment() {}

        public static DayFragment newInstance(TestActivity parent, DataHelper dataHelper, int day) {
            DayFragment dayFragment = new DayFragment();
            dayFragment.parent = parent;
            dayFragment.day = day;
            dayFragment.dataHelper = dataHelper;
            return dayFragment;
        }

        public void addClass(ClassTime ct) {
            mAdapter.addClass(ct);
        }

        public void updateClass(ClassTime ct) {
            mAdapter.updateClassValue(ct);
        }

        @Nullable
        @Override
        public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_class_input, container, false);
        }

        @Override
        public void onViewCreated (View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_class_today);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parent);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new TestClassRecycler(parent, dataHelper, day);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public String toString () {
            return "DayFragment {day = " + day + "}";
        }
    }


}
