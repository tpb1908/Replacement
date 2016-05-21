package com.anapp.tpb.replacement.Home.Test;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.anapp.tpb.replacement.Home.Utilities.ColorPicker;
import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
import com.anapp.tpb.replacement.Home.Utilities.Pair;
import com.anapp.tpb.replacement.Home.Utilities.SubjectSpinnerAdapter;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.klinker.android.sliding.SlidingActivity;

import java.util.Iterator;

/**
 * Created by theo on 17/05/16.
 */
public class ClassInput extends SlidingActivity {
    private static final String TAG = "ClassInput";
    private DataWrapper<ClassTime> mClasses;
    private ClassTime mCurrentClass;
    private int mStartTime = -1;
    private int mEndTime = -1;
    private boolean mEditing;
    private int mDay;
    private TextInputEditText mStartInput;
    private TextInputEditText mEndInput;
    private Spinner mSubjectSpinner;
    private SubjectSpinnerAdapter mSpinnerAdapter;




    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        enableFullscreen();
        setPrimaryColors(ColorPicker.getPrimary(getApplicationContext()), ColorPicker.getPrimaryDark(getApplicationContext()));
        mDay = getIntent().getIntExtra("day", 0);
        mClasses = DataHelper.getInstance(getApplicationContext()).getClassesForDay(mDay);
        mStartInput = (TextInputEditText) findViewById(R.id.edittext_class_start_time);
        mEndInput = (TextInputEditText) findViewById(R.id.edittext_class_end_time);
        mSubjectSpinner = (Spinner) findViewById(R.id.spinner_subject);
        mSpinnerAdapter = new SubjectSpinnerAdapter(getApplicationContext(), DataHelper.getInstance(getApplicationContext()).getAllSubjects());
        mSubjectSpinner.setAdapter(mSpinnerAdapter);

        try {
            mCurrentClass = (ClassTime) getIntent().getSerializableExtra("currentClass");
            mClasses.remove(mCurrentClass);
            mStartTime = mCurrentClass.getStart();
            mEndTime = mCurrentClass.getEnd();
            String time = TimeUtils.format(mStartTime);
            mStartInput.setText(time);
            time = TimeUtils.format(mEndTime);
            mEndInput.setText(time);
            mEditing = true;
            int pos = mSpinnerAdapter.getPositionOfSubject(mCurrentClass.getSubjectID());
            if(pos != -1) {
                mSubjectSpinner.setSelection(pos);
            }
        } catch(Exception e) {
            mEditing = false;
        }

        if(mEditing) {
            setTitle("Edit class");
        } else {
            setTitle("New class");
        }

    }

    private FloatingActionButton.OnClickListener mFabListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean error = false;
            if(mStartTime == -1) {
                final String ERROR = "Start time must be set";
                TextInputLayout wrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_start_time);
                wrapper.setError(ERROR);
                error = true;
            }
            if(mEndTime == -1) {
                final String ERROR = "End time must be set";
                TextInputLayout wrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_end_time);
                wrapper.setError(ERROR);
                error = true;
            }
            if(!error && mStartTime < mEndTime) { //No point checking range if values aren't set
                final String ERROR = "End time must be after start time";
                TextInputLayout wrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_end_time);
                wrapper.setError(ERROR);
                error = true;
            }
            ClassTime ct = new ClassTime();
            ct.setStart(mStartTime);
            ct.setEnd(mEndTime);
            Pair<Boolean, ClassTime> overlap = checkRangeOverlap(ct);
            if(!error && overlap.getLeft()) {
                //TODO- Allow removing the other class
                final String ERROR = "Time range overlaps another class " +
                        overlap.getRight().getSubject().getName() +
                        " from " + TimeUtils.format(overlap.getRight().getStart()) +
                        " to " + TimeUtils.format(overlap.getRight().getEnd());
                TextInputLayout wrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_end_time);
                wrapper.setError(ERROR);
                error = true;
            }
            if(!error) {
                if(mEditing) {
                    ct.setId(mCurrentClass.getId());
                    ct.setSubjectID(mCurrentClass.getSubjectID());
                } else {
                    ct.setSubjectID((int)mSpinnerAdapter.getItemId(mSubjectSpinner.getSelectedItemPosition()));
                    Intent i = new Intent();
                    i.putExtra("edited", mEditing);
                    i.putExtra("class", ct);
                    setResult(RESULT_OK, i);
                    finish();
                }
            }
        }
    };

    public Pair<Boolean, ClassTime> checkRangeOverlap(ClassTime toCheck) {
        Iterator<ClassTime> iter = mClasses.iterator();
        ClassTime ct;
        while(iter.hasNext()) {
            ct = iter.next();
            if(toCheck.overlaps(ct)) {
                return new Pair<>(true, ct);
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
