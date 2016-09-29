package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;

import java.util.ArrayList;

import static com.tpb.timetable.R.id.subject;

/**
 * Created by theo on 25/05/16.
 */
public class ClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<ClassTime> {
    private static final String TAG = "ClassAdapter";
    private DBHelper.ArrayWrapper<ClassTime> mClasses;
    private AdapterManager<ClassTime> mManager;
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private Context mContext;
    private boolean mWasEmpty;


    public ClassAdapter(Context context, AdapterManager<ClassTime> manager) {
        mClasses = DBHelper.getInstance(context).getAllClasses();
        mClasses.addListener(this);
        mManager = manager;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_subject, parent, false);
            return new ClassViewHolder(v);
        } else {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public int numSubjects() {
        return mClasses.size();
    }

    @Override
    public int getItemCount() {
        if(mClasses.size() == 0) {
            mWasEmpty = true;
            return 1;
        } else {
            mWasEmpty = false;
            return mClasses.size();
        }
    }

    public void runQueuedUpdates() {
        if(mQueuedUpdates.size() > 3) {
            notifyDataSetChanged();
        } else {
            for(Runnable r : mQueuedUpdates) r.run();
        }
        mQueuedUpdates.clear();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {

        public ClassViewHolder(final View itemView) {
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
    public void add(ClassTime classTime) {
        add(mClasses.indexOf(classTime), classTime);
    }

    @Override
    public void add(final int index, ClassTime classTime) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                if(mWasEmpty) {
                    notifyItemChanged(0);
                } else {
                    notifyItemInserted(index);
                }
            }
        });
    }

    @Override
    public void set(final int index, ClassTime classTime) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(index);
            }
        });
    }

    @Override
    public void moved(int oldIndex, int newIndex) {
        Log.i(TAG, "moved: " + oldIndex + ", " + newIndex + ", " + subject);
        notifyItemMoved(oldIndex, newIndex);
        notifyItemChanged(newIndex);
    }

    @Override
    public void updated(final int index, ClassTime classTime) {
        Log.i(TAG, "updated: " + index + ", " + subject);
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(index);
            }
        });
    }

    @Override
    public void removed(final int index, ClassTime classTime) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(index);
            }
        });
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
    }


}
