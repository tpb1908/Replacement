package com.anapp.tpb.replacement.Home.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Utilities.MessageViewHolder;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by theo on 08/04/16.
 */
public class TodayTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "TodayTaskAdapter";
    private Context mContext;
    private DataHelper mDataHelper;
    private ArrayList<Task> mTasks;


    public TodayTaskAdapter(Context context) {
        mContext = context;
        mDataHelper = new DataHelper(context);
        mTasks = mDataHelper.getAllCurrentTasks();
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
                vh = new HomeworkViewHolder(v);
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
        Subject subject = mDataHelper.getSubject(task.getSubjectID());
        String timeRange = "Set on ";
        //http://stackoverflow.com/questions/3942878/how-to-decide-font-color-in-white-or-black-depending-on-background-color
        timeRange += new SimpleDateFormat("dd/MM").format(new Date(task.getStartDate()));
        timeRange += " Due by " + new SimpleDateFormat("dd/MM").format(new Date(task.getEndDate()));
        switch(holder.getItemViewType()) {
            case 0:
                MessageViewHolder mvh = (MessageViewHolder) holder;
                mvh.mMessage.setText(R.string.message_no_tasks);
                break;
            case 1:
                break;
            case 2:
                int color = subject.getColor();
                HomeworkViewHolder hvh = (HomeworkViewHolder) holder;
                hvh.mColorBar.setBackgroundColor(color);
                final String subjectNameClass = subject.getName() + ", " + subject.getTeacher();
                hvh.mSubjectName.setText(subjectNameClass);
                //Picking correct text color for the background
                //TODO- Change this to an image drawable, and switch icon that way
                if((Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114) > 186) {
                    hvh.mSubjectName.setTextColor(Color.parseColor("#000000"));
                    hvh.mSubjectName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_homework, 0, 0, 0);
//                    title.getCompoundDrawables()[0].setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
                } else {
                    hvh.mSubjectName.setTextColor(Color.parseColor("#FFFFFF"));
 //                   title.getCompoundDrawables()[0].setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
                    //What the fuck is up with that method name??
                    hvh.mSubjectName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_homework_white, 0, 0, 0);
                }
                hvh.mHomeWorkTitle.setText(task.getTitle());
                hvh.mDueDay.setText(timeRange);
                //Either the first 80 characters, or the first line
                String detailChunk = task.getDetail().substring(0, Math.min(task.getDetail().length(), 80));
                if(detailChunk.contains("\n")) {
                    detailChunk = detailChunk.split("\n", 2)[0];
                }
                detailChunk += "...";
                hvh.mHomeworkDetailStrings = new String[] {detailChunk, task.getDetail()};
                hvh.mHomeWorkDetail.setText(detailChunk);
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
        private Subject subject;

        public TaskViewHolder(View v) {
            super(v);
        }
    }

    private static class HomeworkViewHolder extends TaskViewHolder {
        private View mColorBar;
        private TextView mSubjectName;
        private TextView mHomeWorkDetail;
        private String[] mHomeworkDetailStrings;
        private TextView mHomeWorkTitle;
        private TextView mDueDay;
        private Button mDoneButton;
        private Button mEditButton;
        private boolean expanded = false;

        public HomeworkViewHolder(View v) {
            super(v);
            setIsRecyclable(false);
            mColorBar = v.findViewById(R.id.colour_bar);
            mSubjectName = (TextView) v.findViewById(R.id.text_class_past_info);
            mHomeWorkTitle = (TextView) v.findViewById(R.id.text_homework_title);
            mDueDay = (TextView) v.findViewById(R.id.text_homework_due_day);
            mHomeWorkDetail = (TextView) v.findViewById(R.id.text_homework_detail);
            mDoneButton = (Button) v.findViewById(R.id.button_done);
            mEditButton = (Button) v.findViewById(R.id.button_edit);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!expanded) {
                        mHomeWorkDetail.setText(mHomeworkDetailStrings[0]);
                    } else {
                        mHomeWorkDetail.setText(mHomeworkDetailStrings[1]);
                    }
                    expanded = !expanded;
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
