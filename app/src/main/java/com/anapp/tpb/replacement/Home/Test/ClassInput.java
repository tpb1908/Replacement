package com.anapp.tpb.replacement.Home.Test;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import com.anapp.tpb.replacement.Home.Utilities.Pair;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

/**
 * Created by theo on 17/05/16.
 */
public class ClassInput extends SlidingActivity {
    private static final String TAG = "ClassInput";
    private ArrayList<ClassTime> mClasses;
    private ClassTime mCurrentClass;
    private int mStartTime = -1;
    private int mEndTime = -1;
    private boolean editing;
    private int mDay;
    private EditText mStartInput;
    private EditText mEndInput;


    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        mDay = 0;
        mClasses = DataHelper.getInstance(getApplicationContext()).getClassesForDay(mDay);



    }


    public Pair<Boolean, ClassTime> checkRangeOverlap(ClassTime toCheck) {
        for(ClassTime ct : mClasses) {
            if(toCheck.overlaps(ct)) {
                return new Pair<>(true, toCheck);
            }
        }
        return new Pair<>(false, null);
    }

    private void displayTimePicker(final boolean position) {
        TimePickerDialog mTimeDialog;
        mTimeDialog = new TimePickerDialog(ClassInput.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int time = TimeUtils.hmToInt(hourOfDay, minute);
                if(position) {
                    mStartInput.setText(TimeUtils.format(time));
                } else {
                    mEndInput.setText(TimeUtils.format(time));
                }
            }
        }, 9, 0, true);
        mTimeDialog.show();
    }
}
