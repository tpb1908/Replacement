package com.tpb.timetable.Setup.Input;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Home.Input.SubjectSpinnerAdapter;
import com.tpb.timetable.R;
import com.tpb.timetable.SlidingPanel.SlidingPanel;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 30/09/16.
 */

public class ClassInput extends SlidingPanel {
    private static final String TAG = "ClassInput";
    private ClassTime mCurrentClass;
    private boolean mEditing;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        setPrimaryColors(UIHelper.getPrimary(), UIHelper.getPrimaryDark());
        enableFullscreen();

        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }
        final int day = i.getIntExtra("day", 0);

        final TextInputLayout startWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_start_time);
        final TextInputEditText start = (TextInputEditText) findViewById(R.id.edittext_class_start_time);
        final TextInputLayout endWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_end_time);
        final TextInputEditText end = (TextInputEditText) findViewById(R.id.edittext_class_end_time);



        final Spinner subjectSpinner = (Spinner) findViewById(R.id.spinner_subject);
        final SubjectSpinnerAdapter ssa = new SubjectSpinnerAdapter(this, DBHelper.getInstance(this).getAllSubjects());
        subjectSpinner.setAdapter(ssa);

        setFab(UIHelper.getAccent(), R.drawable.fab_icon_tick_white, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        UIHelper.theme((ViewGroup) findViewById(android.R.id.content));

    }
}
