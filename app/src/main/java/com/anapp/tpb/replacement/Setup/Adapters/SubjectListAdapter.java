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
import com.anapp.tpb.replacement.Setup.DataCollection.SubjectInput;
import com.anapp.tpb.replacement.Setup.DataPresentation.SubjectCollector;
import com.anapp.tpb.replacement.Storage.StorageHelpers.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;

/**
 * Created by pearson-brayt15 on 04/02/2016.
 */
public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolder> {
    private SubjectCollector parent;
    private DataHelper storageHelper;
    private ArrayList<Subject> subjects;

    public SubjectListAdapter(SubjectCollector parent, DataHelper storageHelper) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        subjects = storageHelper.getAllSubjects();
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    private void updateSubject(int position) {
        //Update intent must be started through parent, SubjectCollector
        Intent i = new Intent(parent.getApplicationContext(), SubjectInput.class);
        i.putExtra("editingSubject", subjects.get(position));
        parent.startActivityForResult(i, 1);

    }

    public void updateSubjectValue(Subject l) {
        subjects.set(subjects.indexOf(l), l);
        storageHelper.updateSubject(l);
        notifyItemChanged(subjects.indexOf(l));
        Log.d("Data", "Updating " + l.toString() + " in recycler");
    }

    public void addSubject(Subject l) {
        l = storageHelper.addSubject(l);
        subjects.add(l);
        notifyItemInserted(getItemCount());
        Log.d("Data", "Adding " + l.toString() + " to recycler");
    }

    public void delete(int position) {
        storageHelper.deleteSubject(subjects.get(position));
        subjects.remove(position);
        notifyItemRemoved(position);
        Log.d("Data", "Removing subject at position " + position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_listitem, parent, false);
        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Adapter position- ", "" + holder.getAdapterPosition());
                delete(holder.getAdapterPosition());
            }
        });
        holder.subjectName.setText(subjects.get(position).getName());
        holder.classRoom.setText(subjects.get(position).getClassroom());
        holder.teacherName.setText(subjects.get(position).getTeacher());
        holder.colourBar.setBackgroundColor(subjects.get(position).getColor());
        Log.d("Data", "Binding subject" + subjects.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SubjectListAdapter parent;
        private TextView subjectName;
        private TextView teacherName;
        private TextView classRoom;
        private View colourBar;
        private ImageButton deleteButton;

        public ViewHolder(View v, SubjectListAdapter p) {
            super(v);
            this.parent = p;
            this.subjectName = (TextView) v.findViewById(R.id.subjectName);
            this.teacherName = (TextView) v.findViewById(R.id.teacherName);
            this.classRoom = (TextView) v.findViewById(R.id.classroom);
            this.deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
            this.colourBar = v.findViewById(R.id.colourBar);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.updateSubject(getAdapterPosition());
                }
            });

        }

    }
}
