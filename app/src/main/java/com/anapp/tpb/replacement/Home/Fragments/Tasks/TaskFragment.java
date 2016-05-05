package com.anapp.tpb.replacement.Home.Fragments.Tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anapp.tpb.replacement.Home.Adapters.TodayTaskAdapter;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

/**
 * Created by theo on 08/04/16.
 */
public class TaskFragment extends Fragment {
    private static final String TAG = "TaskFragment";
    private TodayTaskAdapter mTaskAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_tasks, container, false);
        //DataHelper is created here so that the app doesn't force close when it is restarted
        mDataHelper = new DataHelper(getContext());
        mRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_tasks);
        mTaskAdapter = new TodayTaskAdapter(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycler.setAdapter(mTaskAdapter);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(mLayoutManager);
        return inflated;
    }

    public void addTask(Task task) {
        mTaskAdapter.addTask(task);
    }

    public void updateTask(Task task) {
        mTaskAdapter.updateTask(task);
    }
}
