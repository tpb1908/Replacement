package com.tpb.timetable.Home.Fragments.Tasks;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.Home.Adapters.TaskAdapter;
import com.tpb.timetable.Home.Interfaces.FABManager;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ThemeHelper;


/**
 * Created by theo on 08/04/16.
 */
public class TaskFragment extends Fragment implements TaskManager, Themable {
    private static final String TAG = "TaskFragment";
    protected View mView;
    private TaskAdapter mTaskAdapter;
    private TaskManager mTaskInterface;
    private FABManager mFABManager;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCurrentRotation;

    public TaskFragment() {}

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentRotation = getResources().getConfiguration().orientation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflated = inflater.inflate(R.layout.fragment_tasks, container, false);
        ThemeHelper.theme((ViewGroup) inflated);
        ThemeHelper.addListener(this);
        mRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_tasks);
        mTaskAdapter = new TaskAdapter(getContext(), this);
        setupLayoutManager();
        mRecycler.setAdapter(mTaskAdapter);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(mLayoutManager);
        mView = inflated;
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) { //Up- e.g. finger moving up the screen
                    mFABManager.hideFAB();
                } else { //Down
                    mFABManager.showFAB();
                }
            }
        });

        return inflated;
    }

    //TODO- DataUpdateListener if subjects have changed etc
    @Override
    public void onResume() {
        super.onResume();
        final int newRotation = getResources().getConfiguration().orientation;
        if(newRotation != mCurrentRotation) {
            mCurrentRotation = newRotation;
            setupLayoutManager();
            mRecycler.setLayoutManager(mLayoutManager);
        }
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
        try {
            mFABManager = (FABManager) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " mus implement FABManager interface");
        }
    }

    private void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mTaskAdapter.numTasksToday() > 0) {
                mLayoutManager  = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager =  new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }
        }
        if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mTaskAdapter.numTasksToday() > 0) {
            mLayoutManager.setSpanCount(2);
        } else {
            mLayoutManager.setSpanCount(1);
        }
    }

    //Interface methods below
    @Override
    public ViewGroup getViewGroup() {
        mTaskAdapter.notifyDataSetChanged();
        return (ViewGroup) mView;
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

    @Override
    public void countChange(int previousCount, int newCount) {
        if(previousCount <= 1 || newCount == 1) setupLayoutManager();
    }
}
