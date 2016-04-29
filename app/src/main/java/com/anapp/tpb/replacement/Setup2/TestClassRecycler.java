package com.anapp.tpb.replacement.Setup2;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup2.Input.ClassInput;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by theo on 22/04/16.
 */
public class TestClassRecycler extends RecyclerView.Adapter<TestClassRecycler.ClassViewHolder> {
    private  String TAG = "ClassCollectorAdapter";
    private TestActivity parent;
    private DataHelper dataHelper;
    private ArrayList<Subject> subjects;
    private ArrayList<ClassTime> classesForDay;

    public TestClassRecycler (TestActivity parent, DataHelper dataHelper, int day) {
        this.parent = parent;
        this.dataHelper = dataHelper;
        this.subjects = dataHelper.getAllSubjects();
        this.classesForDay = dataHelper.getClassesForDay(day);
        TAG += " day of " + day;
    }

    public ArrayList<ClassTime> getClasses() {
        return classesForDay;
    }

    public void openClass(ClassTime ct) {
        Intent i = new Intent(parent.getApplicationContext(), ClassInput.class);
        ArrayList<ClassTime> classes = new ArrayList<>(classesForDay);
        classes.remove(ct);
        i.putExtra("editingClass", ct);
        i.putExtra("subjects", subjects);
        i.putExtra("classes", classes);
        i.putExtra("day", ct.getDay());
        Log.i(TAG, "Updating class " + ct);
        parent.startActivityForResult(i, 1);
    }

    public void updateClassValue(ClassTime c) {
        classesForDay = dataHelper.getClassesForDay(c.getDay());
        Collections.sort(classesForDay);
        notifyItemChanged(classesForDay.indexOf(c));
        Log.i(TAG, "Updating class in recycler " + c);
        Log.i(TAG, "Class list is now " + classesForDay);
    }

    public void addClass(ClassTime c) {
        classesForDay.add(c);
        Collections.sort(classesForDay);
        notifyItemInserted(classesForDay.indexOf(c));
        Log.i(TAG, "Adding class to recycler " + c);
        Log.i(TAG, "Class list is now " + classesForDay);
    }

    public void delete(int position) {
        dataHelper.deleteClass(classesForDay.get(position));
        classesForDay.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ClassViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class, parent, false);
        return new ClassViewHolder(v, this);
    }



    @Override
    public void onBindViewHolder (final ClassViewHolder holder, final int position) {
        ClassTime ct = classesForDay.get(position);
        holder.ct = ct;
        Subject s = new Subject();
        for (Subject si : subjects) {
            if (ct.getSubjectID() == si.getId()) {
                s = si;
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
        return classesForDay.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        private ClassTime ct;
        private TextView className;
        private TextView classTime;
        private ImageButton deleteButton;
        private View colourBar;

        public ClassViewHolder(View v, final TestClassRecycler p) {
            super(v);
            setIsRecyclable(false);
            className = (TextView) v.findViewById(R.id.subjectName);
            classTime = (TextView) v.findViewById(R.id.classTime);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
            colourBar = v.findViewById(R.id.colourBar);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p.openClass(ct);
                }
            });
        }

    }
}
