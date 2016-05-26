package com.anapp.tpb.replacement.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anapp.tpb.replacement.Home.Interfaces.DataUpdateListener;
import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
import com.anapp.tpb.replacement.Home.Utilities.MessageViewHolder;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.DataTemplate;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

/**
 * Created by theo on 25/05/16.
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DataUpdateListener<DataTemplate> {
    private DataHelper mDataHelper;
    private DataWrapper<Subject> mSubjects;
    private Context mContext;
    
    
    public SubjectAdapter(Context context) {
        mDataHelper = DataHelper.getInstance(context);
        mSubjects = mDataHelper.getAllSubjects();
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
    public void updateAll() {
        notifyDataSetChanged();
    }

    @Override
    public void add(DataTemplate o) {
        Subject subject = (Subject) o;

    }

    @Override
    public void add(int index, DataTemplate o) {
        Subject subject = (Subject) o;
        notifyItemInserted(index);
    }

    @Override
    public void remove(int index, DataTemplate o) {
        Subject subject = (Subject) o;
        notifyItemRemoved(index);
    }

    @Override
    public void update(DataTemplate o) {
        Subject subject = (Subject) o;
        int index = mSubjects.indexOf(subject);
        if(index != -1) {
            notifyItemChanged(index);
        }
    }

    @Override
    public void move(DataTemplate o, int oldPos, int newPos) {
        Subject subject = (Subject) o;
        notifyItemMoved(oldPos, newPos);
    }
}
