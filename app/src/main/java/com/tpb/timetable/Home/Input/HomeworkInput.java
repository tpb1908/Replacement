package com.tpb.timetable.Home.Input;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.klinker.android.sliding.SlidingActivity;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.FormattingUtils;
import com.tpb.timetable.Utils.ThemeHelper;

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

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_homework);
        setPrimaryColors(
                ThemeHelper.getPrimary(),
                ThemeHelper.getPrimaryDark());
        enableFullscreen();
        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }
        final EditText mTitleInput = (EditText) findViewById(R.id.edittext_homework_title);
        final TextInputLayout mTitleWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_homework_title);
        final EditText mDetailInput = (EditText) findViewById(R.id.edittext_homework_detail);
        final TextInputLayout mDetailWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_homework_detail);
        final TextInputLayout mDateWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_homework_due_date);
        mDateInput = (EditText) findViewById(R.id.edittext_homework_due_date);
        final AppCompatCheckBox mShowReminderInput = (AppCompatCheckBox) findViewById(R.id.checkbox_show_reminder);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_subject);
        final DBHelper db = DBHelper.getInstance(this);
        final SubjectSpinnerAdapter spinnerAdapter = new SubjectSpinnerAdapter(this, db.getAllSubjects());
        assert  mTitleInput != null && mTitleWrapper != null && mDetailInput != null &&
                mDetailWrapper != null && mDateWrapper != null && mDateInput != null
                && mShowReminderInput != null && spinner != null;
        spinner.setAdapter(spinnerAdapter);
        mDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });
        try {
            mCurrentTask = (Task) i.getSerializableExtra("task");
            mTitleInput.setText(mCurrentTask.getTitle());
            mDetailInput.setText(mCurrentTask.getDetail());
            mShowReminderInput.setChecked(mCurrentTask.getShowReminder());
            int spinnerPos = spinnerAdapter.getPositionOfSubject(mCurrentTask.getSubjectID());
            if(spinnerPos != -1) spinner.setSelection(spinnerPos, true);
            mDateInput.setText(FormattingUtils.dateToString(new Date(mCurrentTask.getEndDate())));
            mEditing = true;
            setTitle(R.string.title_homework_input_edit);
        } catch(Exception e) {
            mCurrentTask = new Task(2);
            mEditing = false;
            setTitle(R.string.title_homework_input);
        }

        setFab(getResources().getColor(R.color.colorAccent),
                R.drawable.fab_icon_tick_white, new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errorFlag = false;
                final long CURRENT = Calendar.getInstance().getTimeInMillis();
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
                if(mDateInput.getText().toString().equals("")) {
                    errorFlag = true;
                    mDateWrapper.setError("Please set a date");
                } else if(mCurrentTask.getEndDate() < CURRENT ) {
                    errorFlag = true;
                    mDateWrapper.setError("Due date must be after the current date");
                } else {
                    mDateWrapper.setError(null);
                }
                if(!mEditing) mCurrentTask.setStartDate(CURRENT);
                if(!errorFlag) {
                    mCurrentTask.setSubjectID((int) spinner.getSelectedItemId());
                    mCurrentTask.setTitle(mTitleInput.getText().toString());
                    mCurrentTask.setDetail(mDetailInput.getText().toString());
                    mCurrentTask.setShowReminder(mShowReminderInput.isChecked());
                    Log.i(TAG, "Adding homework " + mCurrentTask.toString());
                    if(mEditing) {
                        db.getAllTasks().update(mCurrentTask);
                    } else {
                        db.getAllTasks().addToPos(mCurrentTask);
                    }
                    finish();
                }
            }
        });
        ThemeHelper.theme((ViewGroup) findViewById(R.id.background));
    }

    @Override
    public void finish() {
        if(getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.finish();
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
                final Date d = format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                mDateInput.setText(FormattingUtils.dateToString(d));
                mCurrentTask.setEndDate(d.getTime());
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }
    };

}
