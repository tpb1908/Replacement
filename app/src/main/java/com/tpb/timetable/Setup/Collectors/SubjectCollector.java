package com.tpb.timetable.Setup.Collectors;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;

/**
 * Created by theo on 10/07/16.
 */
public class SubjectCollector extends Collector implements AdapterManager<Subject> {
    private static final String TAG = "SubjectCollector";

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCurrentRotation;

    @Override
    void init() {
        setContentView(R.layout.content_subject_collector);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_subjects);

        mCurrentRotation = getResources().getConfiguration().orientation;
    }

    @Override
    void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE) { // && mTermAdapter.numTerms() > 0
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE) { // && mTermAdapter.numTerms() > 0
            mLayoutManager.setSpanCount(2);
        } else {
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    void add() {

    }


    @Override
    public void open(Subject subject, @Nullable View v) {

    }

    @Override
    public void removed(Subject subject) {

    }
}
