package com.anapp.tpb.replacement.Home.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Interfaces.TaskOpener;
import com.anapp.tpb.replacement.Home.Utilities.MessageViewHolder;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by theo on 08/04/16.
 */
public class TodayTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TodayTaskAdapter";
    private Context mContext;
    private TaskOpener mTaskOpener;
    private DataHelper mDataHelper;
    private ArrayList<Task> mTasks;


    public TodayTaskAdapter(Context context, TaskOpener taskInterface) {
        mContext = context;
        mDataHelper = new DataHelper(context);
        mTasks = mDataHelper.getAllCurrentTasks();
        mTaskOpener = taskInterface;
        Collections.sort(mTasks);

    }



    public void addTask(Task task) {
        mDataHelper.addTask(task);
        if(task.getSubject() == null) {
            task.setSubject(mDataHelper.getSubject(task.getSubjectID()));
        }
        int pos;
        for(pos = 0; pos < mTasks.size(); pos++) {
            if(task.getEndDate() <= mTasks.get(pos).getEndDate()) break;
        }
        //mTasks.add(task);
        mTasks.add(pos, task);
        notifyItemInserted(pos);
    }

    public void updateTask(Task task) {
        mDataHelper.updateCurrent(task);
        if(task.getSubject() == null) {
            task.setSubject(mDataHelper.getSubject(task.getSubjectID()));
        }
        int pos = mTasks.indexOf(task);
        if(pos != -1) {
            mTasks.set(pos, task);
            Collections.sort(mTasks);
            notifyItemChanged(mTasks.indexOf(task));
        } else {
            addTask(task);
        }
    }

    private void deleteTask(int position) {
        Log.i(TAG, "Task being deleted at position " + position + "  " + mTasks.get(position));
        mDataHelper.deleteCurrent(mTasks.get(position));
        mTasks.remove(position);
        notifyItemRemoved(position);
    }

    private void editTask(int position) {
        mTaskOpener.openHomework(mTasks.get(position));
    }

    public TodayTaskAdapter() {
        super();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        switch(viewType) {
            case 0: //Message
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_no_data_message, parent, false);
                vh = new MessageViewHolder(v);
                return vh;
            case 1: //Task
                return null;
            case 2: //Homework
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_homework_test_large, parent, false);
                vh = new HomeworkViewHolder(v, this);
                return vh;
            case 3: //Reminder
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        Task task = mTasks.get(position);
        Subject subject = task.getSubject();
        String timeRange = "Set on ";
        //http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
        timeRange += TimeUtils.getDateString(new Date(task.getStartDate()));
        timeRange += " Due by " + TimeUtils.getDateString(new Date(task.getEndDate()));
        switch(holder.getItemViewType()) {
            case 0:
                MessageViewHolder mvh = (MessageViewHolder) holder;
                mvh.mMessage.setText(R.string.message_no_tasks);
                break;
            case 1:
                Log.i(TAG, "Binding with viewholder type 1");
                break;
            case 2:
                Log.i(TAG, "Rebinding view");
                int color = subject.getColor();
                HomeworkViewHolder hvh = (HomeworkViewHolder) holder;
                hvh.mTitleBar.setBackgroundColor(color);
                final String subjectNameClass = subject.getName() + ", " + subject.getTeacher();
                hvh.mSubjectName.setText(subjectNameClass);
                //Picking correct text color for the background
                if((Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114) > 186) {
                    hvh.mSubjectName.setTextColor(Color.parseColor("#000000"));
                    hvh.mSubjectName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_homework, 0, 0, 0);
                } else {
                    hvh.mSubjectName.setTextColor(Color.parseColor("#FFFFFF"));
                    //What the fuck is up with that method name??
                    hvh.mSubjectName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_homework_white, 0, 0, 0);
                }
                hvh.mHomeWorkTitle.setText(task.getTitle());
                hvh.mDueDay.setText(timeRange);
                hvh.mDetail = task.getDetail();
                hvh.mDetailHint = task.getDetail().split("\n", 2)[0];
                if(hvh.mDetail.length() > hvh.mDetailHint.length()) {
                    hvh.mDetailHint += "...";
                }
                hvh.mHomeWorkDetail.setText(hvh.mDetailHint);
                break;
            case 3:
                break;
        }
    }



    @Override
    public int getItemCount () {
        return mTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.size() == 0 ? 0 : mTasks.get(position).getType();
    }

    private static class TaskViewHolder extends RecyclerView.ViewHolder {
        private Task task;

        public TaskViewHolder(View v) {
            super(v);
        }
    }

    private static class HomeworkViewHolder extends TaskViewHolder {
        private RelativeLayout mTitleBar;
        private TextView mSubjectName;
        private TextView mHomeWorkDetail;
        private String mDetail;
        private String mDetailHint;
        private TextView mHomeWorkTitle;
        private TextView mDueDay;
        private Button mDoneButton;
        private Button mEditButton;
        private ImageButton mDeleteButton;
        private boolean mIsExpanded = false;


        public HomeworkViewHolder(View v, final TodayTaskAdapter parent) {
            super(v);
            mTitleBar =  (RelativeLayout) v.findViewById(R.id.layout_homework_title);
            mSubjectName = (TextView) v.findViewById(R.id.text_homework_subject);
            mHomeWorkTitle = (TextView) v.findViewById(R.id.edittext_homework_title);
            mDueDay = (TextView) v.findViewById(R.id.text_homework_due_day);
            mHomeWorkDetail = (TextView) v.findViewById(R.id.edittext_homework_detail);
            mDoneButton = (Button) v.findViewById(R.id.button_done);
            mEditButton = (Button) v.findViewById(R.id.button_edit);
            mDeleteButton = (ImageButton) v.findViewById(R.id.button_delete);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.deleteTask(getAdapterPosition());
                }
            });
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.editTask(getAdapterPosition());
                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIsExpanded) {
                        mHomeWorkDetail.setSingleLine(false);
                        mHomeWorkDetail.setText(mDetail);
                    } else {
                        mHomeWorkDetail.setText(mDetailHint);
                        mHomeWorkDetail.setSingleLine(true);
                    }
                    mIsExpanded = !mIsExpanded;
                }
            });

        }
    }

    private static class ReminderViewHolder extends TaskViewHolder {

        public ReminderViewHolder(View v) {
            super(v);

        }
    }


}
