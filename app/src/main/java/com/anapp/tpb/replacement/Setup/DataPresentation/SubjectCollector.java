package com.anapp.tpb.replacement.Setup.DataPresentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.Adapters.SubjectListAdapter;
import com.anapp.tpb.replacement.Setup.DataCollection.SubjectInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.SubjectStorageHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import me.yugy.github.reveallayout.RevealLayout;

public class SubjectCollector extends AppCompatActivity {
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton nextFab;
    private FloatingActionButton addSubjectFab;

    private RecyclerView mRecyclerView;
    private SubjectListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SubjectStorageHelper storageHelper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_collector);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(R.string.subject_collector_title);

        nextFab = (FloatingActionButton) findViewById(R.id.subjectCollectorNextFab);
        addSubjectFab = (FloatingActionButton) findViewById(R.id.subjectAddFab);

        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_subject);
        mRevealView = findViewById(R.id.reveal_view_subject);

        storageHelper = new SubjectStorageHelper(this);

        addSubjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SubjectInput.class);
                startActivityForResult(i, 1);
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFab.setClickable(false); // Avoid naughty guys clicking FAB again and again...
                nextFab.setVisibility(View.INVISIBLE);
                addSubjectFab.setVisibility(View.INVISIBLE);
                int[] location = new int[2];
                nextFab.getLocationOnScreen(location);
                location[0] += nextFab.getWidth() / 2;
                location[1] += nextFab.getHeight() / 2;

                final Intent intent = new Intent(SubjectCollector.this, LessonTimeCollector.class);
                intent.putExtra("subjects", mAdapter.getSubjects());

                mRevealView.setVisibility(View.VISIBLE);
                mRevealLayout.setVisibility(View.VISIBLE);

                mRevealLayout.show(location[0], location[1]); // Expand from center of FAB. Actually, it just plays reveal animation.
                nextFab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        /**
                         * Without using R.anim.hold, the screen will flash because of transition
                         * of Activities.
                         */
                        overridePendingTransition(0, R.anim.hold);
                    }
                }, 600); // 600 is default duration of reveal animation in RevealLayout
                nextFab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextFab.setClickable(true);
                        mRevealLayout.setVisibility(View.INVISIBLE);
                        //mViewToReveal.setVisibility(View.INVISIBLE);
                    }
                }, 960); // Or some numbers larger than 600.
            }
        });

        mAdapter = new SubjectListAdapter(this, storageHelper);

        mRecyclerView = (RecyclerView) findViewById(R.id.subjectRecyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result received ", "From subject input");
        if (resultCode == Activity.RESULT_OK) {
            Subject l = (Subject) data.getSerializableExtra("subject");
            if (data.getBooleanExtra("edited", false)) {
                Log.d("Subject received", "Editing " + l.toString());
                mAdapter.updateSubjectValue(l);
            } else {
                Log.d("Subject received", l.toString());
                mAdapter.addSubject(l);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRevealView.setVisibility(View.INVISIBLE);
        nextFab.setVisibility(View.VISIBLE);
        addSubjectFab.setVisibility(View.VISIBLE);
    }

}
