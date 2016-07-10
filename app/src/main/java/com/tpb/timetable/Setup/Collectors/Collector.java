package com.tpb.timetable.Setup.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.tpb.timetable.R;

/**
 * Created by theo on 10/07/16.
 */
public abstract class Collector extends AppCompatActivity {
    private static final String TAG = "Collector";

    private FrameLayout mContentView;
    private boolean mShouldFinishWhenDone;
    private Class mNextWindow;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);
        final Intent launchIntent = getIntent();
        try {
            mNextWindow= (Class) launchIntent.getSerializableExtra("nextWindow");
        } finally {
            mShouldFinishWhenDone = mNextWindow == null;
        }

        final FloatingActionButton mDoneFAB = (FloatingActionButton) findViewById(R.id.fab_done);
        final FloatingActionButton mAddFAB = (FloatingActionButton) findViewById(R.id.fab_add);

        mDoneFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });

        mAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });
        mContentView = (FrameLayout) findViewById(R.id.content_container);
        init();
        setupLayoutManager();
    }

    protected void setLayout(View view) {
        mContentView.addView(view);
    }

    protected void setLayout(int resID) {
        mContentView.addView(getLayoutInflater().inflate(resID, null, false));
    }

    protected void showSnackBar(final Runnable runnable, final String message) {
        final CoordinatorLayout snackBarLayout = (CoordinatorLayout) findViewById(R.id.snackbarPosition);
        final Snackbar snackbar = Snackbar
                .make(snackBarLayout, message,Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        runnable.run();
                    }
                });
        snackbar.show();
    }

    private void done() {
        if(mShouldFinishWhenDone) {
            finish();
        } else {
            final Intent next = new Intent(Collector.this, mNextWindow);
            startActivity(next);
        }
    }

    abstract void init();

    abstract void setupLayoutManager();

    abstract void add();


}
