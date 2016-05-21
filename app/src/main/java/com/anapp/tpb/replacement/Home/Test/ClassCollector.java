package com.anapp.tpb.replacement.Home.Test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.Iterator;

/**
 * Created by theo on 18/05/16.
 */
public class ClassCollector extends AppCompatActivity {
    private static final String TAG = "ClassCollector";
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private DataHelper mDataHelper;
    private FloatingActionButton mAddClassFab;
    private FloatingActionButton mDoneFab;
    private Class mNextActivity;
    private int mCurrentDay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);
        mDataHelper = DataHelper.getInstance(getApplicationContext());

    }

    public static class ClassDayFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private RecyclerView.LayoutManager mLayoutManger;
        private DataHelper mDataHelper;
        private ClassCollector parent;
        private int day;

        public ClassDayFragment() {

        }

        public static ClassDayFragment newInstance(ClassCollector parent, final int day) {
            ClassDayFragment cdf = new ClassDayFragment();
            cdf.mDataHelper = parent.mDataHelper;
            return cdf;
        }
    }

    public static class ClassCollectorAdapter extends RecyclerView.Adapter<ClassCollectorAdapter.ClassViewHolder> {
        private static final String TAG = "ClassCollectorAdapter";
        private ClassCollector mParent;
        private DataHelper mDataHelper;
        private DataWrapper<Subject> mSubjects;
        private DataWrapper<ClassTime> mClasses;

        public ClassCollectorAdapter (ClassCollector parent,int day) {
            this.mParent = parent;
            this.mDataHelper = parent.mDataHelper;
            this.mSubjects = mDataHelper.getAllSubjects();
            //this.mClasses = mDataHelper.getClassesForDay(day);
            Log.i(TAG, "Adapter created for day " + day);
        }

        public void updateClass(ClassTime ct) {
            Intent i = new Intent(mParent.getApplicationContext(), ClassInput.class);
            i.putExtra("editingClass", ct);
            i.putExtra("day", ct.getDay());
            Log.i(TAG, "Updating class " + ct);
            mParent.startActivityForResult(i, 1);
        }

        public void updateClassValue(ClassTime c) {
            mDataHelper.update(c);
            Log.i(TAG, "Updating class with values of " + c);
            //TODO- proper repositioning
            mClasses.set(mClasses.indexOf(c), c);
            mClasses.sort();
            notifyItemChanged(mClasses.indexOf(c));
        }

        public void addClass(ClassTime c) {
            mDataHelper.addClass(c);
            mClasses.sort();
            notifyItemInserted(mClasses.indexOf(c));
        }

        public void delete(int position) {
            mDataHelper.deleteClass(mClasses.get(position));
            Log.i(TAG, "Deleting item at position " + position);
            notifyItemRemoved(position);
        }

        @Override
        public ClassViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class, parent, false);
            return new ClassViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder (final ClassViewHolder holder, final int position) {
            ClassTime ct = mClasses.get(position);
            holder.ct = ct;
            Subject s = new Subject();
            Log.i(TAG, "Subjects: " + mSubjects);
            Iterator<Subject> iter = mSubjects.iterator();
            while(iter.hasNext()) {
                s = iter.next();
                if(s.getId() == ct.getSubjectID()) {
                    break;
                }
            }

            Log.i(TAG, "Binding viewholder for class " + ct.toString() + " and subject " + s.toString());
            holder.className.setText(s.getName());
            holder.colourBar.setBackgroundColor(s.getColor());
            int start = ct.getStart();
            int end = ct.getEnd();
            String timeRange = TimeUtils.format(start) + " to " + TimeUtils.format(end);
            holder.classTime.setText(timeRange);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(holder.getAdapterPosition());
                }
            });

        }

        @Override
        public int getItemCount () {
            return mClasses.size();
        }

        public static class ClassViewHolder extends RecyclerView.ViewHolder {
            private static ClassCollectorAdapter parent;
            private ClassTime ct;
            private TextView className;
            private TextView classTime;
            private ImageButton deleteButton;
            private View colourBar;

            public ClassViewHolder(View v, ClassCollectorAdapter p) {
                super(v);
                setIsRecyclable(false);
                parent = p;
                className = (TextView) v.findViewById(R.id.text_homework_subject);
                classTime = (TextView) v.findViewById(R.id.text_class_time);
                deleteButton = (ImageButton) v.findViewById(R.id.button_delete);
                colourBar = v.findViewById(R.id.colour_bar);
                //Adding listener to the entire view, in order to allow editing
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.updateClass(ct);
                    }
                });
            }
        }
    }
}
