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
import com.anapp.tpb.replacement.Setup.Adapters.LessonListAdapter;
import com.anapp.tpb.replacement.Setup.DataCollection.LessonInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.LessonStorageHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Lesson;

import me.yugy.github.reveallayout.RevealLayout;

public class LessonCollector extends AppCompatActivity {
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton nextFab;
    private FloatingActionButton addLessonFab;

    private RecyclerView mRecyclerView;
    private LessonListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LessonStorageHelper storageHelper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_collector);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(R.string.lesson_collector_title);

        nextFab = (FloatingActionButton) findViewById(R.id.lessonCollectorNextFab);
        addLessonFab = (FloatingActionButton) findViewById(R.id.lessonAddFab);

        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_lesson);
        mRevealView = findViewById(R.id.reveal_view_lesson);

        storageHelper = new LessonStorageHelper(this);

        addLessonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LessonInput.class);
                startActivityForResult(i, 1);
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFab.setClickable(false); // Avoid naughty guys clicking FAB again and again...
                nextFab.setVisibility(View.INVISIBLE);
                addLessonFab.setVisibility(View.INVISIBLE);
                int[] location = new int[2];
                nextFab.getLocationOnScreen(location);
                location[0] += nextFab.getWidth() / 2;
                location[1] += nextFab.getHeight() / 2;

                final Intent intent = new Intent(LessonCollector.this, LessonTimeCollector.class);
                intent.putExtra("lessons", mAdapter.getLessons());

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

        mAdapter = new LessonListAdapter(this, storageHelper);

        mRecyclerView = (RecyclerView) findViewById(R.id.lessonRecyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result received ", "From lesson input");
        if (resultCode == Activity.RESULT_OK) {
            Lesson l = (Lesson) data.getSerializableExtra("lesson");
            if (data.getBooleanExtra("edited", false)) {
                Log.d("Lesson received", "Editing " + l.toString());
                mAdapter.updateLessonValue(l);
            } else {
                Log.d("Lesson received", l.toString());
                mAdapter.addLesson(l);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRevealView.setVisibility(View.INVISIBLE);
        nextFab.setVisibility(View.VISIBLE);
        addLessonFab.setVisibility(View.VISIBLE);
    }

}
