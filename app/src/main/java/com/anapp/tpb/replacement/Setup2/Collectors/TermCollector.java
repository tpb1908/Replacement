package com.anapp.tpb.replacement.Setup2.Collectors;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataPresentation.ClassTimeCollector;
import com.anapp.tpb.replacement.Setup2.Input.TermInput;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import me.yugy.github.reveallayout.RevealLayout;

/**
 * Created by theo on 21/04/16.
 */
public class TermCollector extends AppCompatActivity {
    private static final String TAG = "TermCollector";
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton nextFab;
    private FloatingActionButton addTermFab;
    private TermAdapter mAdapter;

    private Class nextWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_collector);

        DataHelper storageHelper = new DataHelper(getApplicationContext());
        nextFab = (FloatingActionButton) findViewById(R.id.fab_term_add_finish);
        addTermFab = (FloatingActionButton) findViewById(R.id.fab_add_term);
        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_term);
        mRevealView = findViewById(R.id.reveal_view_term);
        nextWindow = (Class) getIntent().getSerializableExtra("nextWindow");
        mAdapter = new TermAdapter(this, storageHelper);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_terms);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        addListeners();

    }

    private void addListeners() {
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
                if(mAdapter.getItemCount() == 0) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(TermCollector.this, R.style.DialogTheme);
                    alert.setTitle("No terms");
                    alert.setMessage("Are you sure you want to continue?\n" +
                            "If you don't enter any terms, the app may send class notifications " +
                            "outside of your term times");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            nextWindow();
                        }
                    });
                    alert.setNegativeButton("Cancel", null);
                    alert.show();
                } else {
                    nextWindow();
                }
            }
        });
    }

    private void nextWindow() {
        nextFab.setClickable(false);
        nextFab.setVisibility(View.INVISIBLE);
        addTermFab.setVisibility(View.INVISIBLE);
        int[] location = new int[2];
        nextFab.getLocationOnScreen(location);
        location[0] += nextFab.getWidth() / 2;
        location[1] += nextFab.getHeight() / 2;

        final Intent intent = new Intent(TermCollector.this, nextWindow);
        intent.putExtra("nextWindow", ClassTimeCollector.class);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Term t = (Term) data.getSerializableExtra("term");
            if (data.getBooleanExtra("edited", false)) {
                mAdapter.updateTermValue(t);
            } else {
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


    private static class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {
        private ArrayList<Term> terms;
        private DataHelper storageHelper;
        private TermCollector parent;

        public TermAdapter (TermCollector parent, DataHelper storageHelper) {
            this.parent = parent;
            this.storageHelper = storageHelper;
            terms = storageHelper.getAllTerms();
        }

        public void addItem (Term term) {
            term = storageHelper.addTerm(term);
            terms.add(term);
            Collections.sort(terms);
            notifyItemInserted(terms.indexOf(term));
        }

        public void delete (int position) {
            storageHelper.delete(terms.get(position));
            terms.remove(position);
            notifyItemRemoved(position);
        }

        public ArrayList<Term> getTerms () {
            return terms;
        }

        public void updateTermValue (Term t) {
            terms.set(terms.indexOf(t), t);
            Collections.sort(terms);
            storageHelper.update(t);
            notifyItemChanged(terms.indexOf(t));
        }

        @Override
        public TermViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            //Layout inflater comes from parent
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_term_date, parent, false);
            return new TermViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder (final TermViewHolder holder, final int position) {
            holder.termNameTextView.setText(terms.get(position).getName());
            String dateRange;
            Date start = new Date(terms.get(holder.getAdapterPosition()).getStartDate());
            Date end = new Date(terms.get(holder.getAdapterPosition()).getEndDate());
            dateRange = start.toString().substring(0, 10) + " to " + end.toString().substring(0, 10);
            holder.termDateRangeTextView.setText(dateRange);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    delete(holder.getAdapterPosition());

                }
            });
        }

        private void updateTerm (int position) {
            Intent i = new Intent(parent.getApplicationContext(), com.anapp.tpb.replacement.Setup.DataCollection.TermInput.class);
            i.putExtra("terms", terms);
            i.putExtra("editingTerm", terms.get(position));
            parent.startActivityForResult(i, 1);

        }

        @Override
        public int getItemCount () {
            return terms.size();
        }

        public static class TermViewHolder extends RecyclerView.ViewHolder {
            public TextView termNameTextView;
            public TextView termDateRangeTextView;
            public ImageButton deleteButton;
            private TermAdapter parent;

            public TermViewHolder (View v, TermAdapter p) {
                super(v);
                this.parent = p;
                termNameTextView = (TextView) v.findViewById(R.id.text_term_name);
                termDateRangeTextView = (TextView) v.findViewById(R.id.text_term_date_range);
                deleteButton = (ImageButton) v.findViewById(R.id.button_delete);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        parent.updateTerm(getAdapterPosition());
                    }
                });
            }
        }
    }

}

