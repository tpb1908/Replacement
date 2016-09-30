package com.tpb.timetable.Setup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Adapters.MessageViewHolder;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.UIHelper;

import java.util.ArrayList;

import static com.tpb.timetable.R.id.subject;

/**
 * Created by theo on 25/05/16.
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Subject> {
    private static final String TAG = "SubjectAdapter";
    private DBHelper.ArrayWrapper<Subject> mSubjects;
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private AdapterManager<Subject> mManager;
    private Context mContext;
    private boolean mWasEmpty;
    
    
    public SubjectAdapter(Context context, AdapterManager<Subject> manager) {
        mSubjects = DBHelper.getInstance(context).getAllSubjects();
        mSubjects.addListener(this);
        mManager = manager;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            final View v = LayoutInflater.from(mContext).inflate(R.layout.listitem_subject, parent, false);
            return new SubjectViewHolder(v, this);
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
        } else {
            final MessageViewHolder mv = (MessageViewHolder) holder;
            mv.setMessage(R.string.message_no_subjects_created);
        }
    }

    public int numSubjects() {
        return mSubjects.size();
    }

    @Override
    public int getItemCount() {
        if(mSubjects.size() == 0) {
            mWasEmpty = true;
            return 1;
        } else {
            mWasEmpty = false;
            return mSubjects.size();
        }
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

        SubjectViewHolder(final View itemView, final SubjectAdapter parent) {
            super(itemView);
            colourBar = itemView.findViewById(R.id.colour_bar);
            subject = (TextView) itemView.findViewById(R.id.text_subject);
            teacher = (TextView) itemView.findViewById(R.id.text_teacher_name);
            classroom = (TextView) itemView.findViewById(R.id.text_classroom);
            final ImageButton delete = (ImageButton) itemView.findViewById(R.id.button_delete);
            UIHelper.setDrawableColor(delete.getDrawable(), UIHelper.getCardBackground());
            delete.refreshDrawableState();
            //delete.setImageDrawable(UIHelper.getColoredDrawable(R.drawable.icon_delete, UIHelper.getCardBackground()));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.delete(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parent.open(getAdapterPosition(), itemView);
                }
            });
            final ImageView iv = (ImageView) itemView.findViewById(R.id.icon_class);
            UIHelper.setDrawableColor(iv.getDrawable(), UIHelper.getCardBackground());
           // iv.setImageDrawable(UIHelper.getColoredDrawable(R.drawable.icon_class, UIHelper.getCardBackground()));

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

    private void open(int pos, View view) {
        mManager.open(mSubjects.get(pos), view);
    }

    private void delete(int pos) {
        mManager.removed(mSubjects.get(pos));
        mSubjects.remove(pos);
        runQueuedUpdates();
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
        add(mSubjects.indexOf(subject), subject);
    }

    @Override
    public void add(final int index, Subject subject) {
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
    public void set(final int index, Subject subject) {
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
    public void updated(final int index, Subject subject) {
        Log.i(TAG, "updated: " + index + ", " + subject);
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(index);
            }
        });
    }

    @Override
    public void removed(final int index, Subject subject) {
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
