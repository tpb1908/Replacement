package com.anapp.tpb.replacement.Home.Input;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
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
import java.util.GregorianCalendar;

/**
 * Created by theo on 01/05/16.
 */
public class HomeworkInput extends SlidingActivity {
    private static final String TAG = "HomeworkInput";
    private Task mCurrentTask;
    private EditText mDateInput;



    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_homework);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        Intent i = getIntent();
        expandFromPoints(i.getIntExtra("leftOffset", 0), i.getIntExtra("topOffset", 0), i.getIntExtra("viewWidth", 0), i.getIntExtra("viewHeight", 0));
        mCurrentTask = new Task(2);
        final EditText mTitleInput = (EditText) findViewById(R.id.edittext_homework_title);
        final EditText mDetailInput = (EditText) findViewById(R.id.edittext_homework_detail);
        mDateInput = (EditText) findViewById(R.id.edittext_homework_due_date);
        final CheckBox mShowReminderInput = (CheckBox) findViewById(R.id.checkbox_show_reminder);

        final Spinner s = (Spinner) findViewById(R.id.spinner_subject);
        DataHelper d = new DataHelper(this);
        s.setAdapter(new SubjectSpinnerAdapter(this, d.getAllSubjects()));

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick_white, new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                int errorFlag = -1;
                if(mCurrentTask.getEndDate() == 0L) {
                    errorFlag = 0;
                } else if(mTitleInput.getText().toString().equals("")) {
                    errorFlag = 1;
                } else if(mDetailInput.getText().toString().equals("")) {
                    errorFlag = 2;
                }

                if(errorFlag == -1) {
                    mCurrentTask.setSubjectID((int)s.getSelectedItemId());
                    mCurrentTask.setTitle(mTitleInput.getText().toString());
                    mCurrentTask.setDetail(mDetailInput.getText().toString());
                    mCurrentTask.setShowReminder(mShowReminderInput.isChecked());
                    setResult(1);
                    finish();
                } else {
                    String errorTitle= "";
                    String errorMessage = "";
                    switch(errorFlag) {
                        case 0:
                            errorTitle = "Date";
                            errorMessage = "You must input a due date";
                            break;
                        case 1:
                            errorTitle = "Title";
                            errorMessage = "You must input a title";
                            break;

                        case 2:
                            errorTitle = "Detail";
                            errorMessage = "You must input some detail";
                            break;
                    }
                    new AlertDialog.Builder(HomeworkInput.this)
                            .setTitle(errorTitle)
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .show();
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
                mCurrentTask.setEndDate( new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis());
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }
    };


}
