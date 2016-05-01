package com.anapp.tpb.replacement.Home.Input;

import android.os.Bundle;
import android.widget.Spinner;

import com.anapp.tpb.replacement.Home.Utilities.SubjectSpinnerAdapter;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;
import com.klinker.android.sliding.SlidingActivity;

/**
 * Created by theo on 01/05/16.
 */
public class HomeworkInput extends SlidingActivity {
    private Task current;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_homework);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, null);
        Spinner s = (Spinner) findViewById(R.id.subject_spinner);
        DataHelper d = new DataHelper(this);
        s.setAdapter(new SubjectSpinnerAdapter(this, d.getAllSubjects()));

    }


}
