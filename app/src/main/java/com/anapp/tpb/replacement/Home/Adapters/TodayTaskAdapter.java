package com.anapp.tpb.replacement.Home.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

/**
 * Created by theo on 08/04/16.
 */
public class TodayTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder (RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount () {
        return 0;
    }

    private static class TaskViewHolder extends RecyclerView.ViewHolder {
        private Task task;
        private Subject subject;

        public TaskViewHolder(View v) {
            super(v);
        }
    }

    private static class HomeworkViewHolder extends TaskViewHolder {

        public HomeworkViewHolder(View v, Task task) {
            super(v);
            if(task.getType() != 0) {
                throw new IllegalArgumentException("Cannot create HomeworkViewHolder with non homework task");
            }
        }
    }

    private static class ReminderViewHolder extends TaskViewHolder {

        public ReminderViewHolder(View v, Task task) {
            super(v);
            if(task.getType() != 2) {
                throw new IllegalArgumentException("Cannot create ReminderViewHolder with non reminder task");
            }
        }
    }


}
