package com.tpb.timetable.Home.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.FormattingUtils;
import com.tpb.timetable.Utils.ThemeHelper;

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
    private final ArrayList<Runnable> mQueuedUpdates = new ArrayList<>();
    private final ArrayList<Boolean> mToggleStates = new ArrayList<>();

    public TaskAdapter() {
        super();
    }

    public TaskAdapter(Context context, TaskManager taskInterface) {
        mDB = DBHelper.getInstance(context);
        mTasks = mDB.getAllTasks();
        mTaskManager = taskInterface;
        mTasks.sort();
        mTasks.addListener(this);
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

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            final MessageViewHolder mvh = (MessageViewHolder) holder;
            mvh.setMessage(R.string.message_no_tasks);
            //Message View holder themes itself
        } else {
            final Task task = mTasks.get(position);
            final Subject subject = mDB.getSubject(task.getSubjectID());

            switch(holder.getItemViewType()) {
                case 1:
                    break;
                case 2:
                    ((HomeworkViewHolder) holder).setup(task, subject);
                    break;
                case 3:
                    break;
            }
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

    private void deleteTask(int position) {
        final Task mDeletedTask = mTasks.get(position);
        mTasks.remove(position);
        runQueuedUpdates();
        mTaskManager.showDeleteSnackBar(mDeletedTask);
        currentPosition = position;
    }

    private void editTask(int position, View v) {
        switch(getItemViewType(position)) {
            case 1: //Task
                break;
            case 2: //Homework
                currentPosition = position;
                mTaskManager.openHomework(mTasks.get(position), v);
                break;
            case 3: //Reminder
                break;
        }
    }

    public int numTasksToday() {
        return mTasks.size();
    }

    //Interface methods
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
        final boolean oldToggleState = mToggleStates.get(oldIndex);
        mToggleStates.remove(oldIndex);
        mToggleStates.set(newIndex, oldToggleState);
        notifyItemMoved(oldIndex, newIndex);
        notifyItemChanged(newIndex);
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
                final boolean oldToggleState = mToggleStates.get(index);
                mToggleStates.remove(index);
                mToggleStates.set(newIndex, oldToggleState);
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
        mToggleStates.remove(index);
        if(mTasks.size() == 0) {
            wasEmpty = true;
            mTaskManager.countChange(1, 0);
        }
    }

    @Override
    public void cleared() {
        notifyDataSetChanged();
        wasEmpty = true;
        mToggleStates.clear();
    }

    @Override
    public void add(final Task task) {
        mQueuedUpdates.add(new Runnable() {
            @Override
            public void run() {
                if(wasEmpty) {
                    notifyItemChanged(0);
                    wasEmpty = false;
                    mTaskManager.countChange(0, 1);
                } else {
                    mToggleStates.add(mTasks.indexOf(task), false);
                    notifyItemInserted(mTasks.indexOf(task));
                }
            }
        });
    }

    @Override
    public void add(int index, Task task) {
        if(wasEmpty) {
            notifyItemChanged(0);
            mTaskManager.countChange(0, 1);
            wasEmpty = false;
        } else {
            mToggleStates.add(mTasks.indexOf(task), false);
            notifyItemInserted(index);
        }
    }

    //Viewholders
    private static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TaskViewHolder(View v) {
            super(v);
        }
    }

    private static class HomeworkViewHolder extends TaskViewHolder {
        private TaskAdapter parent;
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
        private boolean mIsAnimating;
        private int mOriginalHeight = -1;

        public HomeworkViewHolder(View v, final TaskAdapter parent) {
            super(v);
            this.parent = parent;
            mIsAnimating = false;
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
                    parent.currentOpen = mIsExpanded;
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
                    if(!mIsAnimating) {
                        toggleDetail(300);
                        parent.mToggleStates.set(getAdapterPosition(), !mIsExpanded);
                    }
                }
            });
        }

        private void setup(final Task task, final Subject subject) {
            String timeRange = "Set on ";
            timeRange += FormattingUtils.dateToString(new Date(task.getStartDate()));
            timeRange += ", Due by " + FormattingUtils.dateToString(new Date(task.getEndDate()));
            final int titleBackground = subject.getColor();
            mHomeWorkTitle.setText(task.getTitle());
            mDueDay.setText(timeRange);
            mDetail = task.getDetail();
            mDetailHint = task.getDetail().split("\n", 2)[0];
            if(mDetail.length() > mDetailHint.length()) mDetailHint += "...";
            mHomeWorkDetail.setText(mDetailHint);
            ThemeHelper.theme((ViewGroup) itemView);
            mTitleBar.setBackgroundColor(titleBackground);
            final String identifier = subject.getName() + " " + subject.getTeacher();
            mSubjectName.setText(identifier);
            mSubjectName.setCompoundDrawablesWithIntrinsicBounds(
                    ThemeHelper.getColoredDrawable(R.drawable.icon_homework, titleBackground),
                    null,
                    null,
                    null
            );
            mSubjectName.setTextColor(ThemeHelper.getContrastingTextColor(titleBackground));
            if(mOriginalHeight == -1) mOriginalHeight = mHomeWorkDetail.getHeight();

            if(parent.mToggleStates.size() <= getAdapterPosition()) {
                parent.mToggleStates.add(false);
                mIsExpanded = false;
            } else {
                Log.i(TAG, "setup: Toggling view on create with " + parent.mToggleStates.get(getAdapterPosition()));
                mHomeWorkDetail.setText(mDetailHint);
                mHomeWorkDetail.getLayoutParams().height = mOriginalHeight;
                mHomeWorkDetail.requestLayout();
                mIsExpanded = !parent.mToggleStates.get(getAdapterPosition());
                toggleDetail(0);
            }
            Log.i(TAG, "setup: mIsExpanded " + mIsExpanded);


        }

        private void shrink(int duration) {
            final String[] lines = mDetail.split("\n");
            final StringBuilder builder = new StringBuilder();
            final View v = mHomeWorkDetail;
            final ValueAnimator valueAnimator = ValueAnimator.ofInt(v.getHeight(), mOriginalHeight);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                    mIsExpanded = !mIsExpanded;
                    mHomeWorkDetail.setText(mDetailHint);
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**
                     * Something of a hack. What this does, is builds up the correct fraction
                     * of the full detail. The index formula ensures that there is no juddering
                     * at the start by always adding 1 to the fraction of the possible number
                     * of lines. The min call just ensure that nothing can go wrong
                     */
                    if(mIsExpanded && animation.getAnimatedFraction() != 1.0) {
                        final int index = (int) Math.min(lines.length,
                                1+Math.floor((1-animation.getAnimatedFraction()) * lines.length));
                        for(int i = 0; i < index; i++) {
                            builder.append(lines[i]);
                            builder.append("\n");
                        }
                        mHomeWorkDetail.setText(builder.toString());
                        builder.setLength(0); //set length keeps the string buffer
                    }
                    v.getLayoutParams().height = (int) animation.getAnimatedValue();
                    v.requestLayout();

                }
            });
            mIsAnimating = true;
            valueAnimator.start();
        }

        private void grow(int duration) {
            mHomeWorkDetail.setText(mDetail);
            final int numLines = FormattingUtils.numLinesForTextView(mHomeWorkDetail, mDetail);
            final ValueAnimator valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight +(mOriginalHeight * numLines));
            final View v = mHomeWorkDetail;
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsAnimating = false;
                    mIsExpanded = true;
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v.getLayoutParams().height = (int) animation.getAnimatedValue();
                    v.requestLayout();
                }
            });
            mIsAnimating = true;
            valueAnimator.start();
        }

        private void toggleDetail(int duration) {
            Log.i(TAG, "toggleDetail: duration " + duration + " mIsExpanded " + mIsExpanded);
            if(mOriginalHeight == 0) mOriginalHeight = mHomeWorkDetail.getHeight();
            if(mDetailHint.contains("...")) {
                if(mIsExpanded) {
                    shrink(duration);
                } else {
                    grow(duration);
                }
            }
        }
    }

    private static class ReminderViewHolder extends TaskViewHolder {
        public ReminderViewHolder(View v) {
            super(v);
        }
    }
}
