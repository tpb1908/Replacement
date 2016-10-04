package com.tpb.timetable.Setup.Collectors;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Home.Interfaces.AdapterManager;
import com.tpb.timetable.R;
import com.tpb.timetable.Setup.Adapters.ClassAdapter;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 28/09/16.
 */
public class DayFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "DayFragment";
    private AdapterManager<ClassTime> mManager;
    private ClassAdapter mClassAdapter;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mLayoutManager;
    private int mCurrentRotation;
    private int day;

    static DayFragment newInstance(int day) {
        final DayFragment df = new DayFragment();
        final Bundle args = new Bundle();
        args.putInt("day", day+2);
        df.setArguments(args);
        return df;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentRotation = getResources().getConfiguration().orientation;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mManager = (AdapterManager<ClassTime>) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement " + AdapterManager.class.getName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflated = inflater.inflate(R.layout.fragment_class_input, container, false);
        UIHelper.theme(getContext(), (ViewGroup) inflated);
        mRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_class_today);
        day = getArguments().getInt("day");
        mClassAdapter = new ClassAdapter(getContext(), mManager, day);
        setupLayoutManager();
        mRecycler.setAdapter(mClassAdapter);
        mRecycler.setHasFixedSize(false);
        mRecycler.setLayoutManager(mLayoutManager);
        return inflated;
    }

    @Override
    public void onResume() {
        super.onResume();
        final int newRotation = getResources().getConfiguration().orientation;
        if(newRotation != mCurrentRotation) {
            mCurrentRotation = newRotation;
            setupLayoutManager();
            mRecycler.setLayoutManager(mLayoutManager);
        }
        mClassAdapter.runQueuedUpdates();
    }

    public void runQueuedUpdates() {
        mClassAdapter.runQueuedUpdates();
    }


    private void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mClassAdapter.numClassesToday() > 0) {
                mLayoutManager  = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            } else {
                mLayoutManager =  new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            }
        }
        if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mClassAdapter.numClassesToday() > 0) {
            mLayoutManager.setSpanCount(2);
        } else {
            mLayoutManager.setSpanCount(1);
        }
    }

    @Override
    public void onDestroyView() {
        /*
        Either use the code below, or set the FragmentPagerAdapter limit
        The limit is probably better as it stops scroll positions being
        destroyed after the fragment has been left
         */
//        mLayoutManager = null;
//        setupLayoutManager();
        super.onDestroyView();
    }
}


