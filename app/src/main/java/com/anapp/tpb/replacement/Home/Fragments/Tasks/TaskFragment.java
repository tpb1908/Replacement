package com.anapp.tpb.replacement.Home.Fragments.Tasks;

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

import com.anapp.tpb.replacement.Home.Adapters.TaskAdapter;
import com.anapp.tpb.replacement.Home.Interfaces.TaskOpener;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

/**
 * Created by theo on 08/04/16.
 */
public class TaskFragment extends Fragment implements TaskOpener {
    private static final String TAG = "TaskFragment";
    private TaskAdapter mTaskAdapter;
    private TaskOpener mTaskInterface;
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private DataHelper mDataHelper;


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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mTaskInterface = (TaskOpener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TaskOpener interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_tasks, container, false);
        //DataHelper is created here so that the app doesn't force close when it is restarted
        mDataHelper = DataHelper.getInstance(getContext());
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





}
