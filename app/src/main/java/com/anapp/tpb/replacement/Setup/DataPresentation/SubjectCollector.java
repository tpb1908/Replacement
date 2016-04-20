package com.anapp.tpb.replacement.Setup.DataPresentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.anapp.tpb.replacement.Home.Home;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.Adapters.SubjectListAdapter;
import com.anapp.tpb.replacement.Setup.DataCollection.SubjectInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;

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

    private DataHelper storageHelper;

    private Class nextWindow;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_collector);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(Html.fromHtml("<font color='#FFFFFF'>Subjects </font>"));

        nextFab = (FloatingActionButton) findViewById(R.id.subjectCollectorNextFab);
        addSubjectFab = (FloatingActionButton) findViewById(R.id.subjectAddFab);

        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_subject);
        mRevealView = findViewById(R.id.reveal_view_subject);

        storageHelper = new DataHelper(this);

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
                if(mAdapter.getItemCount() == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SubjectCollector.this, R.style.DialogTheme);
                    alert.setMessage("You must enter at least one subject");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                } else {
                    nextFab.setClickable(false);
                    nextFab.setVisibility(View.INVISIBLE);
                    addSubjectFab.setVisibility(View.INVISIBLE);
                    int[] location = new int[2];
                    nextFab.getLocationOnScreen(location);
                    location[0] += nextFab.getWidth() / 2;
                    location[1] += nextFab.getHeight() / 2;

                    final Intent intent = new Intent(SubjectCollector.this, nextWindow);
                    intent.putExtra("subjects", mAdapter.getSubjects());
                    intent.putExtra("nextWindow", Home.class);

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
            }
        });

        nextWindow = (Class) getIntent().getSerializableExtra("nextWindow");

        mAdapter = new SubjectListAdapter(this, storageHelper);

        mRecyclerView = (RecyclerView) findViewById(R.id.subjectRecyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Subject s = (Subject) data.getSerializableExtra("subject");
            if (data.getBooleanExtra("edited", false)) {
                Log.d("Data", "Edited subject received " + s.toString());
                mAdapter.updateSubjectValue(s);
            } else {
                Log.d("Data", "Subject received" + s.toString());
                mAdapter.addSubject(s);
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
