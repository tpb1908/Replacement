package com.anapp.tpb.replacement.Setup.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataCollection.LessonInput;
import com.anapp.tpb.replacement.Setup.DataCollection.TermInput;
import com.anapp.tpb.replacement.Setup.DataPresentation.LessonCollector;
import com.anapp.tpb.replacement.Storage.StorageHelpers.LessonStorageHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Lesson;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by pearson-brayt15 on 04/02/2016.
 */
public class LessonListAdapter extends RecyclerView.Adapter<LessonListAdapter.ViewHolder> {
    private LessonCollector parent;
    private LessonStorageHelper storageHelper;
    private ArrayList<Lesson> lessons;

    public LessonListAdapter(LessonCollector parent, LessonStorageHelper storageHelper) {
        this.parent = parent;
        this.storageHelper = storageHelper;
        lessons = storageHelper.getAllLessons();
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    private void updateLesson(int position) {
        Intent i = new Intent(parent.getApplicationContext(), LessonInput.class);
        i.putExtra("editingLesson", lessons.get(position));
        parent.startActivityForResult(i, 1);
    }

    public void updateLessonValue(Lesson l) {
        lessons.set(lessons.indexOf(l), l);
        storageHelper.update(l);
        notifyItemChanged(lessons.indexOf(l));
    }

    public void addLesson(Lesson l) {
        l = storageHelper.addLesson(l);
        lessons.add(l);
        notifyItemInserted(getItemCount());
    }

    public void delete(int position) {
        storageHelper.delete(lessons.get(position));
        lessons.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_listitem, parent, false);
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
        holder.lessonName.setText(lessons.get(position).getName());
        holder.classRoom.setText(lessons.get(position).getClassroom());
        holder.teacherName.setText(lessons.get(position).getTeacher());
        holder.colourBar.setBackgroundColor(lessons.get(position).getColor());
        Log.d("Lesson binding", lessons.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LessonListAdapter parent;
        private TextView lessonName;
        private TextView teacherName;
        private TextView classRoom;
        private View colourBar;
        private ImageButton deleteButton;

        public ViewHolder(View v, LessonListAdapter p) {
            super(v);
            this.parent = p;
            this.lessonName = (TextView) v.findViewById(R.id.lessonName);
            this.teacherName = (TextView) v.findViewById(R.id.teacherName);
            this.classRoom = (TextView) v.findViewById(R.id.classroom);
            this.deleteButton = (ImageButton) v.findViewById(R.id.deleteButton);
            this.colourBar = v.findViewById(R.id.colourBar);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.updateLesson(getAdapterPosition());
                }
            });

        }

    }
}
