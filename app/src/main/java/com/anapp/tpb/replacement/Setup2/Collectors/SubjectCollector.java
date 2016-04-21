package com.anapp.tpb.replacement.Setup2.Collectors;

import android.app.Activity;
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

import com.anapp.tpb.replacement.Home.Home;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup2.Input.SubjectInput;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

import me.yugy.github.reveallayout.RevealLayout;

/**
 * Created by theo on 21/04/16.
 */
public class SubjectCollector extends AppCompatActivity {
    private static final String TAG = "SubjectCollector";
    private RevealLayout mRevealLayout;
    private View mRevealView;
    private FloatingActionButton nextFab;
    private FloatingActionButton addSubjectFab;
    private SubjectAdapter mAdapter;
    private DataHelper storageHelper;
    private Class nextWindow;


    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_collector);
        nextFab = (FloatingActionButton) findViewById(R.id.subjectCollectorNextFab);
        addSubjectFab = (FloatingActionButton) findViewById(R.id.subjectAddFab);
        mRevealLayout = (RevealLayout) findViewById(R.id.reveal_layout_subject);
        mRevealView = findViewById(R.id.reveal_view_subject);
        storageHelper = new DataHelper(this);
        nextWindow = (Class) getIntent().getSerializableExtra("nextWindow");
        mAdapter = new SubjectAdapter(this, storageHelper);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.subjectRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        addListeners();
    }

    private void addListeners () {
        addSubjectFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(getApplicationContext(), SubjectInput.class);
                startActivityForResult(i, 1);
            }
        });

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (mAdapter.getItemCount() == 0) {
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
                    intent.putExtra("subjects", storageHelper.getAllSubjects());
                    intent.putExtra("nextWindow", Home.class);

                    mRevealView.setVisibility(View.VISIBLE);
                    mRevealLayout.setVisibility(View.VISIBLE);

                    mRevealLayout.show(location[0], location[1]); // Expand from center of FAB. Actually, it just plays reveal animation.
                    nextFab.postDelayed(new Runnable() {
                        @Override
                        public void run () {
                            startActivity(intent);
                            overridePendingTransition(0, R.anim.hold);
                        }
                    }, 600); // 600 is default duration of reveal animation in RevealLayout
                    nextFab.postDelayed(new Runnable() {
                        @Override
                        public void run () {
                            nextFab.setClickable(true);
                            mRevealLayout.setVisibility(View.INVISIBLE);
                            //mViewToReveal.setVisibility(View.INVISIBLE);
                        }
                    }, 960); // Or some numbers larger than 600.
                }
            }
        });
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Subject s = (Subject) data.getSerializableExtra("subject");
            if (data.getBooleanExtra("edited", false)) {
                mAdapter.updateSubjectValue(s);
            } else {
                mAdapter.addSubject(s);
            }
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        mRevealView.setVisibility(View.INVISIBLE);
        nextFab.setVisibility(View.VISIBLE);
        addSubjectFab.setVisibility(View.VISIBLE);
    }

    private static class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
        private SubjectCollector parent;
        private DataHelper storageHelper;
        private ArrayList<Subject> subjects;

        public SubjectAdapter (SubjectCollector parent, DataHelper storageHelper) {
            this.parent = parent;
            this.storageHelper = storageHelper;
            subjects = storageHelper.getAllSubjects();
        }


        private void updateSubject (int position) {
            Intent i = new Intent(parent.getApplicationContext(), SubjectInput.class);
            i.putExtra("editingSubject", subjects.get(position));
            parent.startActivityForResult(i, 1);

        }

        public void updateSubjectValue (Subject l) {
            subjects.set(subjects.indexOf(l), l);
            storageHelper.updateSubject(l);
            notifyItemChanged(subjects.indexOf(l));
        }

        public void addSubject (Subject l) {
            l = storageHelper.addSubject(l);
            subjects.add(l);
            notifyItemInserted(getItemCount());
        }

        public void delete (int position) {
            storageHelper.deleteSubject(subjects.get(position));
            subjects.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public SubjectViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_listitem, parent, false);
            return new SubjectViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder (final SubjectViewHolder holder, final int position) {
            holder.subjectName.setText(subjects.get(position).getName());
            holder.classRoom.setText(subjects.get(position).getClassroom());
            holder.teacherName.setText(subjects.get(position).getTeacher());
            holder.colourBar.setBackgroundColor(subjects.get(position).getColor());
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    delete(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount () {
            return subjects.size();
        }

        public static class SubjectViewHolder extends RecyclerView.ViewHolder {
            private SubjectAdapter parent;
            private TextView subjectName;
            private TextView teacherName;
            private TextView classRoom;
            private View colourBar;
            private ImageButton deleteButton;

            public SubjectViewHolder (View v, SubjectAdapter p) {
                super(v);
                this.parent = p;
                this.subjectName = (TextView) v.findViewById(R.id.subjectName);
                this.teacherName = (TextView) v.findViewById(R.id.teacherName);
                this.classRoom = (TextView) v.findViewById(R.id.classroom);
                this.deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
                this.colourBar = v.findViewById(R.id.colourBar);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        parent.updateSubject(getAdapterPosition());
                    }
                });

            }

        }
    }
}
