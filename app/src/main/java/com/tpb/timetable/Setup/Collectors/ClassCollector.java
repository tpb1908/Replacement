package com.tpb.timetable.Setup.Collectors;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.tpb.timetable.R;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 24/09/16.
 */

public class ClassCollector extends AppCompatActivity {
    private static final String TAG = "ClassCollector";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_collector);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DayPagerAdapter dpa = new DayPagerAdapter(getSupportFragmentManager());
        final ViewPager vp = (ViewPager) findViewById(R.id.day_pager);
        vp.setAdapter(dpa);
        final TabLayout tl = (TabLayout) findViewById(R.id.tabs);
        tl.setupWithViewPager(vp);

        UIHelper.theme((ViewGroup) findViewById(android.R.id.content));
    }

    private class DayPagerAdapter extends FragmentPagerAdapter {
        private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};

        public DayPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new DayFragment();
        }

        @Override
        public int getCount() {
            return days.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return days[position];
        }
    }

}
