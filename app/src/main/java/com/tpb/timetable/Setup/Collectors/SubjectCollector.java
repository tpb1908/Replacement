package com.tpb.timetable.Setup.Collectors;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.tpb.timetable.Data.DBHelper;
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
    private FloatingActionButton mDoneFAB;


    private boolean mShouldFinishWhenDone;
    private Class mNextWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_collector);

        final Intent launchIntent = getIntent();
        mDoneFAB = (FloatingActionButton) findViewById(R.id.fab_add_finish);
        mAddSubjectFab = (FloatingActionButton) findViewById(R.id.fab_add);
        try {
            mNextWindow = (Class) launchIntent.getSerializableExtra("nextWindow");
        } finally {
            mShouldFinishWhenDone = mNextWindow == null;
            if(mShouldFinishWhenDone) {
                mDoneFAB.setImageDrawable(getApplicationContext().getDrawable(R.drawable.fab_icon_tick_white));
            } else {
                mDoneFAB.setImageDrawable(getApplicationContext().getDrawable(R.drawable.fab_icon_next_white));
            }
        }

        mSubjectRecycler = (RecyclerView) findViewById(R.id.recycler_subjects);
        mSubjectAdapter = new SubjectAdapter(getApplicationContext(), this);
        mSubjectRecycler.setAdapter(mSubjectAdapter);

        mCurrentRotation = getResources().getConfiguration().orientation;
        setupLayoutManager();

        UIHelper.theme(mSubjectRecycler);

        mAddSubjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent newSubject = new Intent(SubjectCollector.this, SubjectInput.class);
                UIHelper.setExpandLocation(v, newSubject);
                startActivity(newSubject);
            }
        });



        UIHelper.themeFAB(mDoneFAB);
        UIHelper.themeFAB(mAddSubjectFab);

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
    protected void onResume() {
        super.onResume();
        mSubjectAdapter.runQueuedUpdates();
    }

    @Override
    public void open(Subject subject, @Nullable View v) {
        final Intent editSubject = new Intent(SubjectCollector.this, SubjectInput.class);
        editSubject.putExtra("getSubject", subject);
        UIHelper.setExpandLocation(v, editSubject);
        startActivity(editSubject);
    }

    @Override
    public void removed(final Subject s) {
        final CoordinatorLayout snackBarLayout = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        final Snackbar snackbar = Snackbar
                .make(snackBarLayout, s.getName() + " deleted",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBHelper.getInstance(getApplicationContext()).getAllSubjects().add(s);
                        mSubjectAdapter.runQueuedUpdates();
                    }
                });
        snackbar.show();
    }

    @Override
    public void finish() {
        if(mShouldFinishWhenDone) {
            super.finish();
        } else {
            final Intent next = new Intent(SubjectCollector.this, mNextWindow);
            startActivity(next);
        }
    }
}
