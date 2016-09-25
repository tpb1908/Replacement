package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;

import java.util.ArrayList;

/**
 * Created by theo on 25/05/16.
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Subject> {
    private static final String TAG = "SubjectAdapter";
    private DBHelper.ArrayWrapper<Subject> mSubjects;
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private AdapterManager<Subject> mManger;
    private Context mContext;
    
    
    public SubjectAdapter(Context context, AdapterManager<Subject> manager) {
        mSubjects = DBHelper.getInstance(context).getAllSubjects();
        mSubjects.addListener(this);
        mManger = manager;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_subject, parent, false);
            return new SubjectViewHolder(v);
        } else {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final SubjectViewHolder svh = (SubjectViewHolder) holder;
            final Subject s = mSubjects.get(position);
            svh.colourBar.setBackgroundColor(s.getColor());
            svh.subject.setText(s.getName());
            svh.teacher.setText(s.getTeacher());
            svh.classroom.setText(s.getClassroom());
        }
    }

    public int numSubjects() {
        return mSubjects.size();
    }

    @Override
    public int getItemCount() {
        return mSubjects.size() > 0 ? mSubjects.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mSubjects.size() > 0 ? 0 : 1;
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        private View colourBar;
        private TextView subject;
        private TextView teacher;
        private TextView classroom;

        SubjectViewHolder(View itemView) {
            super(itemView);
            colourBar = itemView.findViewById(R.id.colour_bar);
            subject = (TextView) itemView.findViewById(R.id.text_subject);
            teacher = (TextView) itemView.findViewById(R.id.text_teacher_name);
            classroom = (TextView) itemView.findViewById(R.id.text_classroom);
        }
    }

    @Override
    public void dataSetChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void dataSorted() {
        notifyDataSetChanged();
    }

    @Override
    public void add(Subject subject) {
        notifyItemInserted(mSubjects.indexOf(subject));
    }

    @Override
    public void add(int index, Subject subject) {
        notifyItemInserted(index);
    }

    @Override
    public void set(int index, Subject subject) {
        notifyItemChanged(index);
    }

    @Override
    public void moved(int oldIndex, int newIndex) {
        notifyItemMoved(oldIndex, newIndex);
    }

    @Override
    public void updated(int index, Subject subject) {
        notifyItemChanged(index);
    }

    @Override
    public void removed(int index, Subject subject) {
        notifyItemRemoved(index);
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
    }


}
