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
import com.anapp.tpb.replacement.Setup.DataPresentation.LessonTimeCollector;
import com.anapp.tpb.replacement.Storage.StorageHelpers.ClassTimeStorageHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.*;

import java.util.ArrayList;

/**
 * Created by Theo on 19/02/2016.
 */
public class LessonTimeAdapter extends RecyclerView.Adapter<LessonTimeAdapter.ViewHolder> {
    private ArrayList<ClassTime> classTimes;
    private ArrayList<Subject> subjects;
    private ClassTimeStorageHelper storageHelper;
    private LessonTimeCollector parent;

    public LessonTimeAdapter(LessonTimeCollector parent, ClassTimeStorageHelper storageHelper) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        classTimes = storageHelper.getAllClasses();

    }

    public void updateClass(int position) {
        Intent i = new Intent(parent.getApplicationContext(), ClassInput.class);
        i.putExtra("editingClass", classTimes.get(position));
        parent.startActivityForResult(i, 1);
    }

    public void updateClassValue(ClassTime c) {
        classTimes.set(classTimes.indexOf(c), c);
        storageHelper.update(c);
        notifyItemChanged(classTimes.indexOf(c));
    }

    public void addClass(ClassTime c) {
        c = storageHelper.addClass(c);
        classTimes.add(c);
        notifyItemInserted(getItemCount());
    }

    public void delete(int position) {
        storageHelper.delete(classTimes.get(position));
        classTimes.remove(position);
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
        holder.lessonName.setText(subjects.get(position).getName());

        holder.colourBar.setBackgroundColor(subjects.get(position).getColor());
        //Todo- Set times
    }

    @Override
    public int getItemCount() {
        return classTimes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LessonTimeAdapter parent;
        private TextView lessonName;
        private TextView classTime;
        private ImageButton deleteButton;
        private View colourBar;

        public ViewHolder(View v, LessonTimeAdapter l) {
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
