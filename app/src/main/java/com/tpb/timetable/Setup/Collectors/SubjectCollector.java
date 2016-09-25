package com.tpb.timetable.Setup.Collectors;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Setup.Adapters.SubjectAdapter;
import com.tpb.timetable.Setup.Input.SubjectInput;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 24/09/16.
 */

public class SubjectCollector extends AppCompatActivity implements AdapterManager<Subject> {
    private static final String TAG = "SubjectCollector";

    private SubjectAdapter mSubjectAdapter;
    private RecyclerView mSubjectRecycler;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCurrentRotation;
    private FloatingActionButton mAddSubjectFab;
    private FloatingActionButton mDoneFab;

    private boolean mShouldFinishWhenDone;
    private Class mNextWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_collector);

        final Intent launchIntent = getIntent();
        try {
            mNextWindow = (Class) launchIntent.getSerializableExtra("nextWindow");
            mShouldFinishWhenDone = false;
        } catch(Exception e) {
            mShouldFinishWhenDone = true;
        }

        mSubjectRecycler = (RecyclerView) findViewById(R.id.recycler_subjects);
        mSubjectAdapter = new SubjectAdapter(getApplicationContext(), this);
        mSubjectRecycler.setAdapter(mSubjectAdapter);

        mCurrentRotation = getResources().getConfiguration().orientation;
        setupLayoutManager();

        UIHelper.theme(mSubjectRecycler);

        mAddSubjectFab = (FloatingActionButton) findViewById(R.id.fab_add);

        mAddSubjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent newSubject = new Intent(SubjectCollector.this, SubjectInput.class);
                UIHelper.setExpandLocation(v, newSubject);
                startActivity(newSubject);
            }
        });

        mDoneFab = (FloatingActionButton) findViewById(R.id.fab_add_finish);

    }

    private void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mSubjectAdapter.numSubjects() > 0) {
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }
            mSubjectRecycler.setLayoutManager(mLayoutManager);
        }
        if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mSubjectAdapter.numSubjects() > 0) {
            mLayoutManager.setSpanCount(2);
        } else {
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    public void open(Subject subject, @Nullable View v) {

    }

    @Override
    public void removed(Subject subject) {

    }
}
