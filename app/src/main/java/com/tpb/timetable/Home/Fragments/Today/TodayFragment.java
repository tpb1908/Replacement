package com.tpb.timetable.Home.Fragments.Today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.Home.Adapters.TodayClassAdapter;
import com.tpb.timetable.Home.Interfaces.ClassOpener;
import com.tpb.timetable.Home.Interfaces.TaskOpener;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.FormattingUtils;

import java.util.Calendar;
import java.util.Date;

public class TodayFragment extends Fragment implements ClassOpener {
    private static final String TAG = "TodayFragment";
    private TaskOpener mTaskInterface;
    private ClassOpener mClassInterface;
    private TodayClassAdapter mClassAdapter;
    private RecyclerView mClassRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private BroadcastReceiver mBroadcastReceiver;
    private DBHelper mDB;
    private TextView mDayTermText;
    private int mCurrentDay;
    private int mCurrentRotation;


    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        //Nothing currently needs to be passed
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive (Context context, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    //Simplest way of causing timer bar to update
                    mClassAdapter.collectData();
                }
            }
        };
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        mCurrentRotation = getResources().getConfiguration().orientation;

    }


    @Override
    public void openClass(ClassTime c) {
        mClassInterface.openClass(c);
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_today_classes, container, false);
        //DataHelper is created here so that the app doesn't force close when it is restarted
        mDB =  DBHelper.getInstance(getContext());
        mClassRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_class_today);
        mClassAdapter = new TodayClassAdapter(getContext(), this, mDB);
        mLayoutManager = new LinearLayoutManager(getContext());
        mClassRecycler.setAdapter(mClassAdapter);
        mClassRecycler.setLayoutManager(mLayoutManager);
        mDayTermText = (TextView) inflated.findViewById(R.id.text_today_term);
        setDayTermText();
        mCurrentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        return inflated;
    }

    /**
     * Sets the title mMessage to the current day and term
     */
    private void setDayTermText() {
        String dayTerm = FormattingUtils.dateToString(new Date());
        Term currentTerm = mDB.getCurrentTerm();
        if(currentTerm.getName() != null) {
            dayTerm += "- " + currentTerm.getName();
            mDayTermText.setText(dayTerm);
        } else {
            dayTerm += "- Holiday";
            mDayTermText.setText(dayTerm);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClassInterface = (ClassOpener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ClassOpener interface");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(mClassAdapter == null) {
            mClassAdapter = new TodayClassAdapter(getContext(), this, mDB);
        }
        if(mCurrentDay != today) { //The app has been left overnight
            mCurrentDay = today;
            setDayTermText();
            mClassAdapter.notifyDataSetChanged();
            mClassAdapter.collectData();
        }
        //Rotation- Linear for vertical, two item width for horizontal if there are classes
        int newRotation = getResources().getConfiguration().orientation;
        if(newRotation != mCurrentRotation) {
            mCurrentRotation = newRotation;
            if(mCurrentRotation == Configuration.ORIENTATION_PORTRAIT) {
                mLayoutManager = new LinearLayoutManager(getContext());
            } else if(mClassAdapter.numClassesToday() > 0) {
                mLayoutManager = new GridLayoutManager(getContext(), 2);
            }
            mClassRecycler.setLayoutManager(mLayoutManager);
        }
        //Updating mClassAdapter and reregistering receiver
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        mClassAdapter.collectData();
    }

    @Override
    public void onPause() {
        //Broadcast reciever must be unregistered
        getActivity().unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }
}
