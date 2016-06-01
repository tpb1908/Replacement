package com.tpb.timetable.Home.Fragments.Tasks;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Home.Adapters.TaskAdapter;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ColorResources;


/**
 * Created by theo on 08/04/16.
 */
public class TaskFragment extends Fragment implements TaskManager {
    private static final String TAG = "TaskFragment";
    private TaskAdapter mTaskAdapter;
    private TaskManager mTaskInterface;
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBHelper mDB;


    public TaskFragment() {
    }


    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //TODO- DataUpdateListener if subjects have changed etc
    @Override
    public void onResume() {
        super.onResume();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new LinearLayoutManager(getContext());
        } else {//if(mTaskAdapter.numTasksToday() > 0) {
            mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        }
        mRecycler.setLayoutManager(mLayoutManager);
        mTaskAdapter.runQueuedUpdates();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTaskInterface = (TaskManager) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TaskManager interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_tasks, container, false);
        ColorResources.theme((ViewGroup) inflated);
        //DataHelper is created here so that the app doesn't force close when it is restarted
        mDB = DBHelper.getInstance(getContext());
        mRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_tasks);
        mTaskAdapter = new TaskAdapter(getContext(), this);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecycler.setAdapter(mTaskAdapter);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(mLayoutManager);
        return inflated;
    }

    @Override
    public void openTask(Task t, View v) {
        mTaskInterface.openTask(t, v);
    }

    @Override
    public void openReminder(Task r, View v) {
        mTaskInterface.openReminder(r, v);
    }

    @Override
    public void openHomework(Task h, View v) {
        mTaskInterface.openHomework(h, v);
    }

    @Override
    public void showDeleteSnackBar(Task t) {
        mTaskInterface.showDeleteSnackBar(t);
    }
}
