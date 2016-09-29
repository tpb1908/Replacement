package com.tpb.timetable.Setup.Collectors;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpb.timetable.R;
import com.tpb.timetable.Setup.Adapters.ClassAdapter;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 28/09/16.
 */
public class DayFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "DayFragment";
    private ClassAdapter mClassAdapter;
    private RecyclerView mRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View inflated = inflater.inflate(R.layout.fragment_class_input, container, false);
        UIHelper.theme((ViewGroup) inflated);
        mRecycler = (RecyclerView) inflated.findViewById(R.id.recycler_class_today);
        mClassAdapter = new ClassAdapter(getContext(), null);
        mRecycler.setAdapter(mClassAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
