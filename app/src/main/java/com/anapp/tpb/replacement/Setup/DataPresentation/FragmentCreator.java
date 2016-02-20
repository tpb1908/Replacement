package com.anapp.tpb.replacement.Setup.DataPresentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;

/**
 * Created by pearson-brayt15 on 14/01/2016.
 */
public class FragmentCreator extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public FragmentCreator() {
    }

    public static FragmentCreator newInstance(int layoutResId) {
        FragmentCreator sampleSlide = new FragmentCreator();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

    public void checkData() {

    }

    public void dataEntryComplete() {
        String firstName = ((TextView) getView().findViewById(R.id.setup_name_collection_first)).getText().toString();
        Log.d("Name: ", firstName);
    }
}
