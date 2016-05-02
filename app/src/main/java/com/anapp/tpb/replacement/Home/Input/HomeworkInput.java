package com.anapp.tpb.replacement.Home.Input;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
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
    private Task mCurrentTask;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_homework);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        Intent i = getIntent();
        expandFromPoints(i.getIntExtra("leftOffset", 0), i.getIntExtra("topOffset", 0), i.getIntExtra("viewWidth", 0), i.getIntExtra("viewHeight", 0));

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
        Spinner s = (Spinner) findViewById(R.id.spinner_subject);
        DataHelper d = new DataHelper(this);
        s.setAdapter(new SubjectSpinnerAdapter(this, d.getAllSubjects()));
    }


}
