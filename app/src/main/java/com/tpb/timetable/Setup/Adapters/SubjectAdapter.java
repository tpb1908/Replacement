package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.R;

import java.util.ArrayList;

/**
 * Created by theo on 25/05/16.
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Subject> {
    private DBHelper mDB;
    private DBHelper.ArrayWrapper<Subject> mSubjects;
    private Context mContext;
    
    
    public SubjectAdapter(Context context) {
        mDB = DBHelper.getInstance(context);
        mSubjects = mDB.getAllSubjects();
        mSubjects.addListener(this);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_subject, parent);
            return new SubjectViewHolder(v);
        } else {
            View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mSubjects.size() > 0 ? mSubjects.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return mSubjects.size() == 0 ? 0 : 1;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {

        public SubjectViewHolder(View itemView) {
            super(itemView);
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
    public void addAll(ArrayList<Subject> valuesAdded) {
        notifyDataSetChanged();
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
