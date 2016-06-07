package com.tpb.timetable.Setup.Input;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.klinker.android.sliding.SlidingActivity;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ThemeHelper;
import com.tpb.timetable.Utils.FormattingUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermInput extends SlidingActivity {
    private static final String TAG = "TermInput";
    private Term mCurrentTerm;
    private DBHelper.ArrayWrapper<Term> mTerms;
    private boolean mEditing;
    private boolean mDatePosition;
    private TextInputEditText mStartDateInput;
    private TextInputEditText mEndDateInput;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_term);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }
        final TextInputEditText titleInput = (TextInputEditText) findViewById(R.id.edittext_term_name);
        mStartDateInput = (TextInputEditText) findViewById(R.id.edittext_term_start_date);
        mEndDateInput = (TextInputEditText) findViewById(R.id.edittext_term_end_date);
        final TextInputLayout titleWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_name);
        final TextInputLayout startWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_start_date);
        final TextInputLayout endWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_term_end_date);
        assert mStartDateInput != null && mEndDateInput != null && titleInput != null
                && titleWrapper != null && startWrapper != null && endWrapper != null;

        mStartDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePosition = true;
                showDatePicker();
            }
        });
        mEndDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePosition = false;
                showDatePicker();
            }
        });
        mTerms = DBHelper.getInstance(this).getAllTerms();

        try {
            mCurrentTerm = (Term) i.getSerializableExtra("term");
            titleInput.setText(mCurrentTerm.getName());
            mStartDateInput.setText(FormattingUtils.dateToString(new Date(mCurrentTerm.getStartDate())));
            mEndDateInput.setText(FormattingUtils.dateToString(new Date(mCurrentTerm.getEndDate())));
            mEditing = true;
            setTitle(R.string.title_term_input_edit);
        } catch(Exception e) {
            mEditing = false;
            mCurrentTerm = new Term();
            setTitle(R.string.title_term_input);
        }

        setFab(getResources().getColor(R.color.colorAccent),
                R.drawable.fab_icon_tick_white,
                new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentTerm.setName(titleInput.getText().toString());
                boolean errorFlag = false;
                if(titleInput.getText().toString().equals("")) {
                    errorFlag = true;
                    final String ERROR = "Please input a term name";
                    titleWrapper.setError(ERROR);
                } else {
                    titleWrapper.setError(null);
                }
                if(mCurrentTerm.getStartDate() == 0) {
                    errorFlag = true;
                    final String ERROR = "Please set a start date";
                    startWrapper.setError(ERROR);
                } else {
                    if(mCurrentTerm.getStartDate()> mCurrentTerm.getEndDate()) {
                        errorFlag = true;
                        final String ERROR = "Start date must be before end date";
                        startWrapper.setError(ERROR);
                    } else {
                        startWrapper.setError(null);
                    }
                }
                if(mCurrentTerm.getEndDate() == 0) {
                    errorFlag = true;
                    final String ERROR = "Please set an end date";
                    endWrapper.setError(ERROR);
                } else {
                    if(mCurrentTerm.getStartDate() > mCurrentTerm.getEndDate()) {
                        errorFlag = true;
                        final String ERROR = "End date must be after start date";
                        endWrapper.setError(ERROR);
                    } else {
                        endWrapper.setError(null);
                    }
                }
                final boolean[] overlap = new boolean[1];
                for(int i = 0; i < mTerms.size(); i++) {
                    if(mCurrentTerm.overlaps(mTerms.get(i))) {
                        //TODO- Give date values
                        final String message = "This terms overlaps " +
                                mTerms.get(i).getName() + ". Delete "+
                                mTerms.get(i).getName() + "?";
                        final int index = i;
                        new AlertDialog.Builder(TermInput.this)
                                .setTitle("Overlapping term")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mTerms.remove(index);
                                        overlap[0] = false;
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        overlap[0] = true;
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                    }
                }
                if(overlap[0]) errorFlag = true;
                if(!errorFlag) {
                    if(mEditing) {
                        mTerms.update(mCurrentTerm);
                    } else {
                        mTerms.addToPos(mCurrentTerm);
                    }
                }
            }
        });
        ThemeHelper.theme((ViewGroup) findViewById(R.id.background));
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
                String dString = FormattingUtils.dateToString(d);
                if(mDatePosition) {
                    mStartDateInput.setText(dString);
                    mCurrentTerm.setStartDate(d.getTime());
                } else {
                    mEndDateInput.setText(dString);
                    mCurrentTerm.setEndDate(d.getTime());
                }
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }
    };
}
