package com.tpb.timetable.Setup;

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
import android.util.Log;
import android.view.View;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Setup.Adapters.TermAdapter;
import com.tpb.timetable.Setup.Input.TermInput;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 03/07/16.
 */
public class TermCollector extends AppCompatActivity implements AdapterManager<Term> {
    private static final String TAG = "TermCollector";

    private TermAdapter mTermAdapter;
    private RecyclerView mTermRecycler;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCurrentRotation;
    private FloatingActionButton mAddTermFAB;
    private FloatingActionButton mDoneFAB;

    private boolean mShouldFinishWhenDone;
    private Class mNextWindow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_collector);

        final Intent launchIntent = getIntent();
        try {
            mNextWindow = (Class) launchIntent.getSerializableExtra("nextWindow");
            mShouldFinishWhenDone = false;
        } catch(Exception e) {
            mShouldFinishWhenDone = true;
        }

        mTermRecycler = (RecyclerView) findViewById(R.id.recycler_terms);
        mTermAdapter = new TermAdapter(getApplicationContext(),this);
        mTermRecycler.setAdapter(mTermAdapter);
        mCurrentRotation = getResources().getConfiguration().orientation;
        setupLayoutManager();
        UIHelper.theme(mTermRecycler);

        mAddTermFAB = (FloatingActionButton) findViewById(R.id.fab_add);
        mDoneFAB = (FloatingActionButton) findViewById(R.id.fab_add_finish);

        mDoneFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO- Prompt if no terms entered
                if(mShouldFinishWhenDone) {
                    finish();
                } else {
                    final Intent next = new Intent(TermCollector.this, mNextWindow);
                    startActivity(next);
                }
            }
        });
        if(mShouldFinishWhenDone) {
            mDoneFAB.setImageDrawable(UIHelper.getColoredDrawable(R.drawable.fab_icon_tick));
        } else {
            Log.i(TAG, "onCreate: Setting done drawable to icon next");
            mDoneFAB.setImageDrawable(UIHelper.getColoredDrawable(R.drawable.fab_icon_next));
        }
        UIHelper.themeFAB(mDoneFAB);

        mAddTermFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent newTerm = new Intent(TermCollector.this, TermInput.class);
                UIHelper.setExpandLocation(v, newTerm);
                startActivity(newTerm);
            }
        });
        UIHelper.themeFAB(mAddTermFAB);
    }

    private void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mTermAdapter.numTerms() > 0) {
                mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }
            mTermRecycler.setLayoutManager(mLayoutManager);
        }
        if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mTermAdapter.numTerms() > 0) {
            mLayoutManager.setSpanCount(2);
        } else {
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    public void open(Term term, @Nullable View v) {
        final Intent editTerm = new Intent(TermCollector.this, TermInput.class);
        editTerm.putExtra("term", term);
        UIHelper.setExpandLocation(v, editTerm);
        startActivity(editTerm);
    }

    @Override
    public void removed(final Term t) {
        final CoordinatorLayout snackBarLayout = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        final Snackbar snackbar = Snackbar
                .make(snackBarLayout, t.getName() + " deleted",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBHelper.getInstance(getApplicationContext()).getAllTerms().addToPosition(t);
                        mTermAdapter.runQueuedUpdates();
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTermAdapter.runQueuedUpdates();
    }
}
