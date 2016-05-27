package com.anapp.tpb.replacement.Setup.Input;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.anapp.tpb.replacement.Home.Utilities.DataWrapper;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;
import com.klinker.android.sliding.SlidingActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermInput extends SlidingActivity {
    private static final String TAG = "TermInput";
    private DataWrapper<Term> mTerms;
    private boolean mEditing;
    private boolean mDatePosition;
    private TextInputEditText startDateInput;
    private TextInputEditText endDateInput;
    private long mStartDate;
    private long mEndDate;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_term);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        Intent i = getIntent();
        mEditing = i.getBooleanExtra("editing", false);
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }
        final TextInputEditText titleInput = (TextInputEditText) findViewById(R.id.edittext_term_name);
        startDateInput = (TextInputEditText) findViewById(R.id.edittext_term_start_date);
        endDateInput = (TextInputEditText) findViewById(R.id.edittext_term_end_date);
        final TextInputLayout titleWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_name);
        final TextInputLayout startWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_start_date);
        final TextInputLayout endWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_end_date);
        assert startDateInput != null && endDateInput != null && titleInput != null
                && titleWrapper != null && startWrapper != null && endWrapper != null;

        startDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePosition = true;
                showDatePicker();
            }
        });
        endDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePosition = false;
                showDatePicker();
            }
        });
        DataHelper dataHelper = DataHelper.getInstance(this);


        if(mEditing) {

        }

        setFab(getResources().getColor(R.color.colorAccent),
                R.drawable.fab_icon_tick_white,
                new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errorFlag = false;
                if(titleInput.getText().toString().equals("")) {
                    errorFlag = true;
                    final String ERROR = "Please input a term name";
                    titleWrapper.setError(ERROR);
                } else {
                    titleWrapper.setError(null);
                }
                if(mStartDate == 0) {
                    errorFlag = true;
                    final String ERROR = "Please set a start date";
                    startWrapper.setError(ERROR);
                } else {
                    if(mStartDate > mEndDate) {
                        errorFlag = true;
                        final String ERROR = "Start date must be before end date";
                        startWrapper.setError(ERROR);
                    } else {
                        startWrapper.setError(null);
                    }
                }
                if(mEndDate == 0) {
                    errorFlag = true;
                    final String ERROR = "Please set an end date";
                    endWrapper.setError(ERROR);
                } else {
                    if(mStartDate > mEndDate) {
                        errorFlag = true;
                        final String ERROR = "End date must be after start date";
                        endWrapper.setError(ERROR);
                    } else {
                        endWrapper.setError(null);
                    }
                }
            }
        });

    }


    public void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(TermInput.this, R.style.DatePickerTheme, dateSetListener,
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
                if(mDatePosition) {
                    mStartDate = d.getTime();
                    startDateInput.setText(dString);
                } else {
                    mEndDate = d.getTime();
                    endDateInput.setText(dString);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }
    };
}
