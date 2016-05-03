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
import com.anapp.tpb.replacement.Setup.DataPresentation.ClassTimeCollector;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassTimeAdapter extends RecyclerView.Adapter<ClassTimeAdapter.ViewHolderClass> {
    private static final String TAG =" ClassTimeAdapter";
    private ArrayList<ClassTime> classes;
    private ArrayList<Subject> subjects;
    private DataHelper storageHelper;
    private ClassTimeCollector parent;

    public ClassTimeAdapter(ClassTimeCollector parent, DataHelper storageHelper, ArrayList<Subject> subjects, int day) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        this.subjects = subjects;
        this.classes = storageHelper.getClassesForDay(day);
        Log.d("Data", "Selected classes for day " +( day)+ ", " + classes.toString());
    }

    private void updateClass(int position) {
        Intent i = new Intent(parent.getApplicationContext(), com.anapp.tpb.replacement.Setup2.Input.ClassInput.class);
        Log.i(TAG, "Updating class " + classes.get(position));
        i.putExtra("editingClass", classes.get(position));
        i.putExtra("subjects", subjects);
        i.putExtra("classes", classes);
        i.putExtra("day", classes.get(position).getDay());
        //Activity must be started through activity, passing to ClassTimeCollector
        parent.startActivityForResult(i, 1);
    }

    //Method used when a ClassInput returns a value from a class which has already been created
    public void updateClassValue(ClassTime c) {
        classes.set(classes.indexOf(c), c);
        storageHelper.updateClass(c);
        Collections.sort(classes);
        notifyItemChanged(classes.indexOf(c));
        Log.d("Data", "Updating " + c.toString() + " in recycler");
    }

    //Adds a class to the recycler, and calls the storagehelper method to save the class
    public void addClass(ClassTime c) {
        c = storageHelper.addClass(c);
        classes.add(c);
        Collections.sort(classes);
        notifyItemInserted(classes.indexOf(c));
        Log.d("Data", "Adding " + c.toString() + " to recycler");
    }

    private void delete(int position) {
        parent.removeClass(classes.get(position));
        storageHelper.deleteClass(classes.get(position));
        classes.remove(position);
        notifyItemRemoved(position);
        Log.d("Data", "Removing class at position " + position + " data now consists of " + classes);
    }

    //Method inflates a new viewholder instance with the class_listitem layout
    @Override
    public ViewHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class, parent, false);
        ViewHolderClass vh = new ViewHolderClass(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolderClass holder, int position) {
        //Adding listener for delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        holder.className.setText(s.getName());
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
        Log.d("Data", "Binding class with value of " + classes.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder {
        private static ClassTimeAdapter parent; //Parent is the same throughout all instances
        private TextView className;
        private TextView classTime;
        private ImageButton deleteButton;
        private View colourBar;

        public ViewHolderClass (View v, ClassTimeAdapter p) {
            super(v);
            parent = p;
            className = (TextView) v.findViewById(R.id.text_class_past_info);
            classTime = (TextView) v.findViewById(R.id.text_class_time);
            deleteButton = (ImageButton) v.findViewById(R.id.button_delete);
            colourBar = v.findViewById(R.id.colour_bar);
            //Adding listener to the entire view, in order to allow editing
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.updateClass(getAdapterPosition());
                }
            });
        }
    }
}
