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
import com.anapp.tpb.replacement.Setup.Adapters.TermListAdapter;
import com.anapp.tpb.replacement.Setup.DataCollection.TermInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;
import com.anapp.tpb.replacement.Storage.StorageHelpers.TermStorageHelper;

import me.yugy.github.reveallayout.RevealLayout;

public class TermDateCollector extends AppCompatActivity {
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton nextFab;
    private FloatingActionButton addTermFab;

    private RecyclerView mRecyclerView;
    private TermListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DataHelper storageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_date_collector);
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitle(R.string.termCollectorTitle);
        setSupportActionBar(t);
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));

        storageHelper = new DataHelper(getApplicationContext());
        nextFab = (FloatingActionButton) findViewById(R.id.termDataNextFab);
        addTermFab = (FloatingActionButton) findViewById(R.id.termAddFab);

        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_term);
        mRevealView = findViewById(R.id.reveal_view_term);

        addTermFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TermInput.class);
                i.putExtra("terms", mAdapter.getTerms());
                startActivityForResult(i, 1);
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFab.setClickable(false); // Avoid naughty guys clicking FAB again and again...
                nextFab.setVisibility(View.INVISIBLE);
                addTermFab.setVisibility(View.INVISIBLE);
                int[] location = new int[2];
                nextFab.getLocationOnScreen(location);
                location[0] += nextFab.getWidth() / 2;
                location[1] += nextFab.getHeight() / 2;

                final Intent intent = new Intent(TermDateCollector.this, SubjectCollector.class);

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

        mAdapter = new TermListAdapter(this, storageHelper);
        mRecyclerView = (RecyclerView) findViewById(R.id.termRecyclerView);
        //mRecyclerView.setHasFixedSize(true); //Improves performance if the view doesn't expand
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Term t = (Term) data.getSerializableExtra("term");
            if (data.getBooleanExtra("edited", false)) {
                Log.d("Data", "Edited term received  " + t.toString());
                mAdapter.updateTermValue(t);
            } else {
                Log.d("Data", "Term received " + t.toString());
                mAdapter.addItem(t);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRevealView.setVisibility(View.INVISIBLE);
        nextFab.setVisibility(View.VISIBLE);
        addTermFab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
