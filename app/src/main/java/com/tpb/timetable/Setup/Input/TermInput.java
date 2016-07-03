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

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Term;
import com.tpb.timetable.R;
import com.tpb.timetable.SlidingPanel.SlidingPanel;
import com.tpb.timetable.Utils.FormattingUtils;
import com.tpb.timetable.Utils.UIHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 25/05/16.
 */
public class TermInput extends SlidingPanel {
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
        setPrimaryColors(UIHelper.getPrimary(), UIHelper.getPrimaryDark());
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
                final boolean[] flags = new boolean[5];
                flags[0] = titleInput.getText().toString().equals("");
                flags[1] = mCurrentTerm.getStartDate() == 0;
                flags[2] = mCurrentTerm.getEndDate() == 0;
                flags[3] = mCurrentTerm.getStartDate() > mCurrentTerm.getEndDate();

                titleWrapper.setError(flags[0] ?
                        getApplicationContext().getString(R.string.error_no_term_name) : null);
                startWrapper.setError(flags[1] ?
                        getApplicationContext().getString(R.string.error_no_term_name) : null);
                endWrapper.setError(flags[2] ?
                        getApplicationContext().getString(R.string.error_no_term_name) : null);
                if(!(flags[1] || flags[2]) && flags[3]) {
                    startWrapper.setError(getApplicationContext().getString(R.string.error_start_date_after_end));
                    endWrapper.setError(getApplicationContext().getString(R.string.error_end_date_before_start));
                }

                for(int i = 0; i < mTerms.size(); i++) {
                    if(mCurrentTerm.overlaps(mTerms.get(i)) && !mTerms.get(i).equals(mCurrentTerm)) {
                        flags[4] = true;
                        //TODO- Give date values
                        final int index = i;
                        new AlertDialog.Builder(TermInput.this)
                                .setTitle("Overlapping term")
                                .setMessage(String.format(
                                        getApplicationContext().getString(R.string.error_overlapping_terms),
                                        mTerms.get(i).getName(), mTerms.get(i).getName()))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mTerms.remove(index);
                                        close();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                    }
                }

                if(!(flags[0] || flags[1] || flags[2] || flags[3] || flags[4])) {
                    close();
                }
            }
        });
        UIHelper.theme((ViewGroup) findViewById(R.id.background));
    }

    private void close() {
        if(mEditing) {
            mTerms.update(mCurrentTerm);
            finish();
        } else {
            mTerms.addToPosition(mCurrentTerm);
            finish();
        }
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
