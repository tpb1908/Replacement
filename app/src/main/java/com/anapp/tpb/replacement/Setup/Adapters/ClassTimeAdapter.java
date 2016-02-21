package com.anapp.tpb.replacement.Setup.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataCollection.ClassInput;
import com.anapp.tpb.replacement.Setup.DataPresentation.ClassTimeCollector;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.*;

import java.util.ArrayList;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassTimeAdapter extends RecyclerView.Adapter<ClassTimeAdapter.ViewHolder> {
    private ArrayList<ClassTime> classes;
    private ArrayList<Subject> subjects;
    private DataHelper storageHelper;
    private ClassTimeCollector parent;

    public ClassTimeAdapter(ClassTimeCollector parent, DataHelper storageHelper, ArrayList<Subject> subjects, int day) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        this.subjects = subjects;
        this.classes = new ArrayList<>();
        ArrayList<ClassTime> c = storageHelper.getAllClasses();
        for (ClassTime classTime : c) {
            if (classTime.getDay() == day) {
                Log.d("Class with day ", "Day of " + classTime.getDay());
                classes.add(classTime);
            }
        }
        Log.d("Length of classes ", "For day " + day + " = " + classes.size());

    }

    public void updateClass(int position) {
        Intent i = new Intent(parent.getApplicationContext(), ClassInput.class);
        i.putExtra("editingClass", classes.get(position));
        i.putExtra("subjects", subjects);
        i.putExtra("classes", classes);
        i.putExtra("day", classes.get(position).getDay());
        parent.startActivityForResult(i, 1);
    }

    public void updateClassValue(ClassTime c) {
        classes.set(classes.indexOf(c), c);
        storageHelper.updateClass(c);
        notifyItemChanged(classes.indexOf(c));
    }

    public void addClass(ClassTime c) {
        Log.d("Class Received", "Adding " + c.toString() + " to recycler");
        c = storageHelper.addClass(c);
        classes.add(c);
        notifyItemInserted(getItemCount());
    }

    public void delete(int position) {
        storageHelper.deleteClass(classes.get(position));
        classes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_listitem, parent, false);
        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Adapter position- ", "" + holder.getAdapterPosition());
                delete(holder.getAdapterPosition());
            }
        });
        ClassTime ct = classes.get(position);
        String timeRange;
        Subject s = new Subject();
        for (Subject si : subjects) {
            if (ct.getSubjectID() == si.getId()) {
                s = si;
                break;
            }
        }
        holder.lessonName.setText(s.getName());
        holder.colourBar.setBackgroundColor(s.getColor());
        int start = ct.getStart();
        int end = ct.getEnd();
        if (start < 1000) {
            timeRange = (Integer.toString(start).substring(0, 1) + ":" + Integer.toString(start).substring(1));
        } else {
            timeRange = (Integer.toString(start).substring(0, 2) + ":" + Integer.toString(start).substring(2));
        }
        if (end < 1000) {
            timeRange += " to " + (Integer.toString(end).substring(0, 1) + ":" + Integer.toString(end).substring(1));
        } else {
            timeRange += " to " + (Integer.toString(end).substring(0, 2) + ":" + Integer.toString(end).substring(2));
        }
        holder.classTime.setText(timeRange);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ClassTimeAdapter parent;
        private TextView lessonName;
        private TextView classTime;
        private ImageButton deleteButton;
        private View colourBar;

        public ViewHolder(View v, ClassTimeAdapter l) {
            super(v);
            this.parent = l;
            lessonName = (TextView) v.findViewById(R.id.subjectName);
            classTime = (TextView) v.findViewById(R.id.classTime);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
            colourBar = v.findViewById(R.id.colourBar);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.updateClass(getAdapterPosition());
                }
            });
        }
    }
}
