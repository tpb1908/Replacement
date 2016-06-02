package com.tpb.timetable.Home.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mattyork.colours.Colour;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ColorResources;
import com.tpb.timetable.Utils.FormattingUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by theo on 08/04/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DBHelper.ArrayChangeListener<Task> {
    private static final String TAG = "TaskAdapter";
    private TaskManager mTaskManager;
    private DBHelper mDB;
    private DBHelper.ArrayWrapper<Task> mTasks;
    private boolean currentOpen = false;
    private int currentPosition = -1;
    private boolean wasEmpty = false; //this is needed so that adding a value doesn't cause an inconsistency
    private ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();


    public TaskAdapter(Context context, TaskManager taskInterface) {
        mDB = DBHelper.getInstance(context);
        mTasks = mDB.getAllTasks();
        mTaskManager = taskInterface;
        mTasks.sort();
        mTasks.addListener(this);
    }

    public void runQueuedUpdates() {
        if(mQueuedUpdates.size() > 3) {
            notifyDataSetChanged();
        } else {
            for(Runnable r : mQueuedUpdates) {
                r.run();
            }
        }
        mQueuedUpdates.clear();
    }

    private void deleteTask(int position) {
        Log.i(TAG, "Task being deleted at position " + position + "  " + mTasks.get(position));
        final Task mDeletedTask = mTasks.get(position);
        mTasks.remove(position);
        runQueuedUpdates();
        mTaskManager.showDeleteSnackBar(mDeletedTask);
    }

    private void editTask(int position, View v) {
        switch(getItemViewType(position)) {
            case 1: //Task
                break;
            case 2:
                currentPosition = position;
                mTaskManager.openHomework(mTasks.get(position), v);
                break;
            case 3: //Reminder
                break;
        }
    }

    public TaskAdapter() {
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
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_homework, parent, false);
                vh = new HomeworkViewHolder(v, this);
                return vh;
            case 3: //Reminder
                return null;
            default:
                Log.i(TAG, "Returning null viewholder");
                return null;
        }
    }


    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final MessageViewHolder mvh = (MessageViewHolder) holder;
            mvh.mMessage.setText(R.string.message_no_tasks);
        } else {
            final Task task = mTasks.get(position);
            final Subject subject = mDB.getSubject(task.getSubjectID());
            String timeRange = "Set on ";
            //http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
            timeRange += FormattingUtils.dateToString(new Date(task.getStartDate()));
            timeRange += ", Due by " + FormattingUtils.dateToString(new Date(task.getEndDate()));
            switch(holder.getItemViewType()) {
                case 1:
                    break;
                case 2:
                    final int color = subject.getColor();
                    final HomeworkViewHolder hvh = (HomeworkViewHolder) holder;
                    ColorResources.theme((ViewGroup) hvh.itemView);
                    hvh.mTitleBar.setBackgroundColor(color);
                    final String subjectNameClass = subject.getName() + ", " + subject.getTeacher();
                    hvh.mSubjectName.setText(subjectNameClass);
                    if(Colour.blackOrWhiteContrastingColor(color) == 0) {
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
                    Log.i(TAG, "View being bound at "  + position);
                    if(position == currentPosition && currentOpen) {
                        final Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                hvh.openDetail(0);
                            }
                        };
                        new Handler().postDelayed(r, 50);
                    }
                    break;
                case 3:
                    break;
            }
        }
    }

    @Override
    public int getItemCount () {
        if(mTasks.size() == 0) {
            wasEmpty = true;
            return 1;
        }
        return mTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTasks.size() == 0 ? 0 : mTasks.get(position).getType();
    }

    public int numTasksToday() {
        return mTasks.size();
    }


    private static class TaskViewHolder extends RecyclerView.ViewHolder {

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
        private int mOriginalHeight = 0;


        public HomeworkViewHolder(View v, final TaskAdapter parent) {
            super(v);
            mTitleBar =  (RelativeLayout) v.findViewById(R.id.layout_homework_title);
            mSubjectName = (TextView) v.findViewById(R.id.text_homework_subject);
            mHomeWorkTitle = (TextView) v.findViewById(R.id.text_homework_title);
            mDueDay = (TextView) v.findViewById(R.id.text_homework_due_day);
            mHomeWorkDetail = (TextView) v.findViewById(R.id.text_homework_detail);
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
                    parent.currentOpen = mIsExpanded;
                    parent.editTask(getAdapterPosition(), mEditButton);
                }
            });
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDetail(300);
                }
            });
        }

        private void openDetail(int duration) {
            final View v = mHomeWorkDetail;
            if(mOriginalHeight == 0) mOriginalHeight = v.getHeight();
            ValueAnimator valueAnimator;
            final int numLines = FormattingUtils.numLinesForTextView(mHomeWorkDetail, mDetail);
            if(!mIsExpanded) {
                mHomeWorkDetail.setSingleLine(false);
                mHomeWorkDetail.setText(mDetail);
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight + (mOriginalHeight * numLines));
            } else {
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight +  (mOriginalHeight * numLines), mOriginalHeight);
            }
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                /*
                    This listener is for changing the text back to a hint
                    The text must only be changed once the animation is
                    complete. Otherwise the text is updated, and the
                    animation just shrinks white space
                 */
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(mIsExpanded) {
                        mHomeWorkDetail.setText(mDetailHint);
                        mHomeWorkDetail.setSingleLine(true);
                    }
                    mIsExpanded = !mIsExpanded;
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (int) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }

    private static class ReminderViewHolder extends TaskViewHolder {

        public ReminderViewHolder(View v) {
            super(v);

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
    public void set(final int index, Task task) {
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
    }

    @Override
    public void updated(final int index, final Task task) {
        if(task.getSubject() == null) {
            task.setSubject(mDB.getSubject(task.getSubjectID()));
        }
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                final int newIndex = mTasks.indexOf(task);
                notifyItemMoved(index, newIndex);
                notifyItemChanged(newIndex);
            }
        });

    }

    @Override
    public void removed(final int index, Task task) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                notifyItemRemoved(index);
            }
        });
        if(mTasks.size() == 0) wasEmpty = true;
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
        wasEmpty = true;
    }

    @Override
    public void add(final Task task) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                if(wasEmpty) {
                    notifyItemChanged(0);
                    wasEmpty = false;
                } else {
                    notifyItemInserted(mTasks.indexOf(task));
                }
            }
        });
    }

    @Override
    public void add(int index, Task task) {
        Log.i(TAG, "Adding a task");
        if(wasEmpty) {
            notifyDataSetChanged();
            wasEmpty = false;
        } else {
            notifyItemInserted(index);
        }
    }

}
