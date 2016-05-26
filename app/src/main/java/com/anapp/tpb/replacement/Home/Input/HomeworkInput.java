package com.anapp.tpb.replacement.Home.Input;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.anapp.tpb.replacement.Home.Utilities.SubjectSpinnerAdapter;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Task;
import com.klinker.android.sliding.SlidingActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 01/05/16.
 */
public class HomeworkInput extends SlidingActivity {
    private static final String TAG = "HomeworkInput";
    private Task mCurrentTask;
    private EditText mDateInput;
    private boolean mEditing;
    private boolean mCancel;
    private Intent returnIntent;


    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_homework);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        Intent i = getIntent();
        returnIntent = new Intent();
        mCancel = true;
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0), i.getIntExtra("topOffset", 0), i.getIntExtra("viewWidth", 0), i.getIntExtra("viewHeight", 0));
        }
        final EditText mTitleInput = (EditText) findViewById(R.id.edittext_homework_title);
        final TextInputLayout mTitleWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_homework_title);
        final EditText mDetailInput = (EditText) findViewById(R.id.edittext_homework_detail);
        final TextInputLayout mDetailWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_homework_detail);
        mDateInput = (EditText) findViewById(R.id.edittext_homework_due_date);
        final CheckBox mShowReminderInput = (CheckBox) findViewById(R.id.checkbox_show_reminder);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_subject);
        final DataHelper d = DataHelper.getInstance(this);
        SubjectSpinnerAdapter spinnerAdapter = new SubjectSpinnerAdapter(this, d.getAllSubjects());
        spinner.setAdapter(spinnerAdapter);

        //TODO- Find out why this works, but definition in XML causes some strange crash
//        mDateInput.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePicker(v);
//            }
//        });

        try {
            mCurrentTask = (Task) i.getSerializableExtra("task");
            mTitleInput.setText(mCurrentTask.getTitle());
            mDetailInput.setText(mCurrentTask.getDetail());
            mShowReminderInput.setChecked(mCurrentTask.showReminder());
            int spinnerPos = spinnerAdapter.getPositionOfSubject(mCurrentTask.getSubjectID());
            if(spinnerPos != -1) {
                spinner.setSelection(spinnerPos, true);
            }
            mDateInput.setText(TimeUtils.getDateString(new Date(mCurrentTask.getEndDate())));
            mEditing = true;
        } catch(Exception e) {
            mCurrentTask = new Task(2);
            mEditing = false;
        }

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick_white, new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errorFlag = false;

                if(mTitleInput.getText().toString().equals("")) {
                    errorFlag = true;
                    mTitleWrapper.setError("Please set a title");
                } else {
                    mTitleWrapper.setError(null);
                }

                if(mDetailInput.getText().toString().equals("")) {
                    errorFlag = true;
                    mDetailWrapper.setError("Please add some detail");
                } else {
                    mDetailWrapper.setError(null);
                }
                if(!errorFlag) {
                    mCancel = false;
                    if(!mEditing) { //Don't change the time that a task is set
                        Calendar c = Calendar.getInstance();
                        mCurrentTask.setStartDate(c.getTimeInMillis());
                    }
                    mCurrentTask.setSubjectID((int)spinner.getSelectedItemId());
                    mCurrentTask.setTitle(mTitleInput.getText().toString());
                    mCurrentTask.setDetail(mDetailInput.getText().toString());
                    mCurrentTask.setShowReminder(mShowReminderInput.isChecked());
                    returnIntent.putExtra("task", mCurrentTask);
                    if(mEditing) {
                        d.getAllCurrentTasks().update(mCurrentTask);
                    } else {
                        d.getAllCurrentTasks().add(mCurrentTask);
                    }
                    finish();
                } else {
                    mCancel = true;
                }
            }
        });

    }

    public void showDatePicker(View v) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(HomeworkInput.this, R.style.DatePickerTheme, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                String dString = TimeUtils.getDateString(d);
                mDateInput.setText(dString);
                mCurrentTask.setEndDate(d.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }
    };

    @Override
    public void finish() {
        if(!mCancel) {
            if(mEditing) {
                setResult(1, returnIntent);
            } else {
                setResult(0, returnIntent);
            }
        } else {
            setResult(-1, returnIntent);
        }
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.finish();
    }
}
