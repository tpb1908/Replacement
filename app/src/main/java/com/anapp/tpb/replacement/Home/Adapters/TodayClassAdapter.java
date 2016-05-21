package com.anapp.tpb.replacement.Home.Adapters;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Interfaces.ClassOpener;
import com.anapp.tpb.replacement.Home.Interfaces.DataUpdateListener;
import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
import com.anapp.tpb.replacement.Home.Utilities.MessageViewHolder;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.Calendar;

/**
 * Created by pearson-brayt15 on 03/03/2016.
 */
public class TodayClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DataUpdateListener<ClassTime> {
    private static final String TAG = "TodayClassAdapter";
    private Context mContext;
    private ClassOpener mClassInterface;
    private DataWrapper<ClassTime> mClasses;
    private DataHelper mDataHelper;
    private Calendar mCalendar;
    private int currentTime;
    private int currentTimePosition;
    private int currentEndTime = Integer.MAX_VALUE;

    public TodayClassAdapter(Context context, ClassOpener mClassInterface, DataHelper dataHelper) {
        this.mContext = context;
        this.mDataHelper = dataHelper;
        this.mCalendar = Calendar.getInstance();
        this.mClassInterface = mClassInterface;
        this.mClasses = dataHelper.getClassesToday();
        mClasses.addListener(this);
    }

    //TODO- DataHelper methods to get the data in a wrapper



    public int numClassesToday() {
        return  mClasses.size();
    }

    @Override
    public void updateAll() {
        notifyDataSetChanged();
    }

    @Override
    public void add(ClassTime classTime) {
        notifyItemInserted(mClasses.indexOf(classTime));
    }

    @Override
    public void add(int index, ClassTime classTime) {
        notifyItemInserted(index);
    }

    @Override
    public void remove(ClassTime classTime) {
        notifyItemRemoved(mClasses.indexOf(classTime));
    }

    @Override
    public void remove(int index, ClassTime classTime) {
        notifyItemRemoved(index);
    }

    @Override
    public void update(ClassTime classTime) {
        notifyItemChanged(mClasses.indexOf(classTime));
    }

    @Override
    public void move(ClassTime classTime, int oldPos, int newPos) {
        notifyItemMoved(oldPos, newPos);
    }

    /**
     * ViewHolder class for a class in the current day which is yet to pass
     */
    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        private static TodayClassAdapter parent;
        private TextView className;
        private TextView classTime;
        private TextView teacherName;
        private TextView classRoom;
        private View colourBar;
        private ProgressBar timerBar;

        public ClassViewHolder(View v, TodayClassAdapter p) {
            super(v);
            parent = p;
            setIsRecyclable(false);
            className = (TextView) v.findViewById(R.id.text_homework_subject);
            classTime = (TextView) v.findViewById(R.id.text_class_time);
            classRoom = (TextView) v.findViewById(R.id.text_classroom);
            teacherName = (TextView) v.findViewById(R.id.text_teacher_name);
            colourBar = v.findViewById(R.id.colour_bar);
            timerBar = (ProgressBar) v.findViewById(R.id.progressbar_class_timer);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    parent.openClass(getAdapterPosition());
                }
            });
        }
    }

    /**
     * ViewHolder for a class in the current day which has already passed
     */
    public static class ClassPastViewHolder extends RecyclerView.ViewHolder {
        private TextView info;

        public ClassPastViewHolder(View v) {
            super(v);
            setIsRecyclable(false);
            info = (TextView) v.findViewById(R.id.text_homework_subject);
        }
    }


    /**
     * Forwards a call to the Home class to open a class, getting the ClassTime
     * @param position The position of the class to open
     */
    public void openClass(int position) {
        mClassInterface.openClass(mClasses.get(position));
    }

    /**
     * Used to reset the data helper if it is cleared from memory
     */
    public void resume(DataHelper dataHelper) {
        this.mDataHelper = dataHelper;
    }

    /**
     * Collects the correct data for the adapter
     * This is the classes, subjects, and the current minute
     * Assuming that the class data hasn't been changed, the DataHelper will just
     * send back the same arraylist as before, rather than reading it again
     */
    public void collectData() {
        mCalendar = Calendar.getInstance();
        //this.mClasses = mDataHelper.getClassesForDay(mCalendar.get(Calendar.DAY_OF_WEEK));
        currentTime = (mCalendar.get(Calendar.HOUR_OF_DAY)*100) + mCalendar.get(Calendar.MINUTE);
        if(currentEndTime < currentTime) {
            notifyItemChanged(currentTimePosition);
        } else {
            notifyDataSetChanged();
        }
    }


    /**
     * Creates either a new class view holder or a mMessage viewholder
     * @param parent The viewgroup through which to inflate  the class
     * @param viewType The type of view to inflate. 0 for mMessage, otherwise, listitem_class_today
     * @return A ClassViewHolder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        if(viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_no_data_message, parent, false);
            vh = new MessageViewHolder(v);
        } else if(viewType == 1){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class_today, parent, false);
            vh = new ClassViewHolder(v, this);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class_today_past, parent, false);
            vh = new ClassPastViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        if(h.getItemViewType() == 0) {
            MessageViewHolder holder = (MessageViewHolder) h;
            holder.mMessage.setText(mContext.getResources().getString(R.string.message_no_classes));
        } else {
            ClassTime ct = mClasses.get(position);
            Subject s = ct.getSubject();
            if(h.getItemViewType() == 1) {
                ClassViewHolder holder = (ClassViewHolder) h ;
                String timeRange;
                holder.className.setText(s.getName());
                holder.classRoom.setText(s.getClassroom());
                holder.teacherName.setText(s.getTeacher());
                holder.timerBar.setScaleY(3f);
                mCalendar = Calendar.getInstance();

                int start = ct.getStart();
                int end = ct.getEnd();
                //If the class is currently running
                if(currentTime > start && currentTime < end) {
                    //Setting up the timerBar
                    currentEndTime = ct.getEnd();
                    currentTimePosition = h.getAdapterPosition();
                    float scale = TimeUtils.getPercentageComplete(currentTime, start, end);
                    holder.timerBar.setVisibility(View.VISIBLE);
                    //Filters timer color by subject color
                    holder.timerBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, s.getColor()));
                    holder.timerBar.setProgress((int) (100 * scale));
                } else {
                    holder.timerBar.setVisibility(View.INVISIBLE);
                }
                holder.colourBar.setBackgroundColor(s.getColor());
                timeRange = TimeUtils.format(start);
                timeRange += " to " + TimeUtils.format(end);
                holder.classTime.setText(timeRange);
            } else {
                ClassPastViewHolder holder = (ClassPastViewHolder) h;
                String info = s.getName() + " with " + s.getTeacher() +
                        " from " + TimeUtils.format(ct.getStart()) +
                        " to " + TimeUtils.format(ct.getEnd());
                holder.info.setText(info);
            }
        }
    }

    @Override
    public int getItemViewType (int position) {
        //If there are no mClasses, the only item will be of type mMessage
        if(mClasses.size() == 0) {
            return 0;
        } else if(mClasses.get(position).getEnd() > currentTime){
            return  1; //Full view
        } else { //If the class has past
            return 2;
        }
    }


    @Override
    public int getItemCount() {
        //There will always be at least one item, either message or mClasses
        return mClasses.size() == 0 ? 1 : mClasses.size();
    }

}
