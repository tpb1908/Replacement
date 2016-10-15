package com.tpb.timetable.Home.Fragments.Today;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.Home.Adapters.TodayClassAdapter;
import com.tpb.timetable.Home.Interfaces.ClassOpener;
import com.tpb.timetable.Home.Interfaces.TaskManager;
import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.Format;
import com.tpb.timetable.Utils.UIHelper;

import java.util.Calendar;
import java.util.Date;

public class TodayFragment extends Fragment implements ClassOpener, Themable {
    private static final String TAG = "TodayFragment";
    protected View mView;
    private TaskManager mTaskInterface;
    private ClassOpener mClassInterface;
    private TodayClassAdapter mClassAdapter;
    private RecyclerView mClassRecycler;
    private StaggeredGridLayoutManager mLayoutManager;
    private BroadcastReceiver mBroadcastReceiver;
    private DBHelper mDB;
    private TextView mDayTermText;
    private int mCurrentDay;
    private int mCurrentRotation;


    public TodayFragment() {}

    public static TodayFragment newInstance() {
        return new TodayFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflated = inflater.inflate(R.layout.fragment_today_classes, container, false);
        //DataHelper is created here so that the app doesn't force close when it is restarted
        UIHelper.theme(getContext(), (ViewGroup) inflated);
        UIHelper.addListener(this);
        mDB =  DBHelper.getInstance(getContext());
        mClassRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_class_today);
        mClassAdapter = new TodayClassAdapter(getContext(), this, mDB);
        setupLayoutManager();
        mClassRecycler.setAdapter(mClassAdapter);
        mClassRecycler.setLayoutManager(mLayoutManager);
        mDayTermText = (TextView) inflated.findViewById(R.id.text_today_term);
        setDayTermText();
        mCurrentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        mView = inflated;
        inflated.findViewById(R.id.theme_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.setDarkTheme(getContext(), !UIHelper.isDarkTheme());
            }
        });
        return inflated;
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
        final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
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
        final int newRotation = getResources().getConfiguration().orientation;
        if(newRotation != mCurrentRotation) {
            mCurrentRotation = newRotation;
            setupLayoutManager();
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

    private void setupLayoutManager() {
        if(mLayoutManager == null) {
            if(mCurrentRotation == Configuration.ORIENTATION_LANDSCAPE && mClassAdapter.numClassesToday()> 0) {
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


    private void setDayTermText() {
        String dayTerm = Format.dateToString(new Date());
        final Term currentTerm = mDB.getCurrentTerm();
        if(currentTerm.getName() != null) {
            dayTerm += "- " + currentTerm.getName();
            mDayTermText.setText(dayTerm);
        } else {
            dayTerm += " - Holiday";
            mDayTermText.setText(dayTerm);
        }
        mDayTermText.setTextColor(UIHelper.getPrimaryText());
    }


    @Override
    public void openClass(ClassTime c) {
        mClassInterface.openClass(c);
    }

    @Override
    public ViewGroup getViewGroup() {
        return (ViewGroup) mView;
    }
}
