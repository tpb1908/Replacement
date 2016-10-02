package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.Format;
import com.tpb.timetable.Utils.UIHelper;

import java.util.ArrayList;

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


    public ClassAdapter(Context context, AdapterManager<ClassTime> manager, int day) {
        mClasses = DBHelper.getInstance(context).getClassesForDay(day);
        Log.d(TAG, "ClassAdapter: Classes " + mClasses.toString());
        mClasses.addListener(this);
        mManager = manager;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_class, parent, false);
            return new ClassViewHolder(v);
        } else {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_no_data_message, parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final ClassViewHolder cv = (ClassViewHolder) holder;
            cv.colourBar.setBackgroundColor(mClasses.get(position).getSubject().getColor());
            cv.subject.setText(mClasses.get(position).getSubject().getName());
            String timeRange = Format.format(mClasses.get(position).getStartTime()) +
                    " to " + Format.format(mClasses.get(position).getEndTime());
            cv.classTime.setText(timeRange);
            cv.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mManager.removed(mClasses.get(holder.getAdapterPosition()));
                    mClasses.remove(holder.getAdapterPosition());
                    runQueuedUpdates();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mManager.open(mClasses.get(holder.getAdapterPosition()), holder.itemView);
                }
            });
        } else {
            final MessageViewHolder mv = (MessageViewHolder) holder;
            mv.setMessage(R.string.message_no_classes_created);
        }
    }

    public int numClassesToday() {
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

    @Override
    public int getItemViewType(int position) {
        return mClasses.size() > 0 ? 0 : 1;
    }

    public void runQueuedUpdates() {
        if(mQueuedUpdates.size() > 3) {
            notifyDataSetChanged();
        } else {
            for(Runnable r : mQueuedUpdates) r.run();
        }
        mQueuedUpdates.clear();
    }

    private static class ClassViewHolder extends RecyclerView.ViewHolder {
        private View colourBar;
        private TextView subject;
        private TextView classTime;
        private ImageButton delete;

        ClassViewHolder(final View itemView) {
            super(itemView);
            colourBar = itemView.findViewById(R.id.colour_bar);
            subject = (TextView) itemView.findViewById(R.id.text_subject);
            classTime = (TextView) itemView.findViewById(R.id.text_class_time);
            delete = (ImageButton) itemView.findViewById(R.id.button_delete);
            UIHelper.theme((ViewGroup) itemView);
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
        notifyItemMoved(oldIndex, newIndex);
        notifyItemChanged(newIndex);
    }

    @Override
    public void updated(final int index, ClassTime classTime) {
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
