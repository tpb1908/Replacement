package com.tpb.timetable.Setup.Collectors;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 24/09/16.
 */

public class ClassCollector extends AppCompatActivity implements AdapterManager<ClassTime> {
    private static final String TAG = "ClassCollector";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_collector);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ViewPager vp = (ViewPager) findViewById(R.id.day_pager);
        final DayPagerAdapter dpa = new DayPagerAdapter(getSupportFragmentManager(), vp);
        vp.setAdapter(dpa);
        //TODO- Find out what happens whent he fragments are destroyed
        vp.setOffscreenPageLimit(5);
        final TabLayout tl = (TabLayout) findViewById(R.id.tabs);
        tl.setupWithViewPager(vp);
        UIHelper.theme((ViewGroup) findViewById(android.R.id.content));
        final FloatingActionButton newFAB = (FloatingActionButton) findViewById(R.id.fab_add);
        final FloatingActionButton done = (FloatingActionButton) findViewById(R.id.fab_add_finish);
        newFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void open(ClassTime classTime, @Nullable View v) {

    }

    @Override
    public void removed(ClassTime classTime) {

    }

    private class DayPagerAdapter extends FragmentPagerAdapter {
        private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        private DayFragment[] mFragments = new DayFragment[5];
        private ViewPager vp;

        public DayPagerAdapter(FragmentManager fm, ViewPager vp) {
            super(fm);
            this.vp = vp;
        }

        @Override
        public Fragment getItem(int position) {
            mFragments[position] = DayFragment.newInstance(position);
            return mFragments[position];

        }

        @Override
        public int getCount() {
            return days.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return days[position];
        }

        public void addClass(ClassTime ct) {
            mFragments[vp.getCurrentItem()].addClass(ct);
        }

    }


}
