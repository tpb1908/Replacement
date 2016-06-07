package com.tpb.timetable.Home.Input;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.klinker.android.sliding.SlidingActivity;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.R;
import com.tpb.timetable.Utils.ThemeHelper;
import com.tpb.timetable.Utils.FormattingUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 05/05/16.
 */
public class AssessmentInput extends SlidingActivity {
    private static final String TAG = "AssessmentInput";
    private EditText mDateInput;
    private LinearLayout mMarkLayout;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_assessment);
        setPrimaryColors(ThemeHelper.getPrimary(), ThemeHelper.getPrimaryDark());
        enableFullscreen();
        final Intent i = getIntent();
        expandFromPoints(i.getIntExtra("leftOffset", 0), i.getIntExtra("topOffset", 0), i.getIntExtra("viewWidth", 0), i.getIntExtra("viewHeight", 0));
        final RelativeLayout background = (RelativeLayout) findViewById(R.id.background);
        ThemeHelper.theme(background);
        final EditText mTitleInput = (EditText) findViewById(R.id.edittext_assessment_title);
        final EditText mNotesInputs = (EditText) findViewById(R.id.edittext_assessment_notes);
        final Spinner mSubjectSpinner = (Spinner) findViewById(R.id.spinner_subject);
        mDateInput = (EditText) findViewById(R.id.edittext_assessment_date);
        mMarkLayout = (LinearLayout) findViewById(R.id.layout_assessment_marks);
        final DBHelper db = DBHelper.getInstance(this);
        mSubjectSpinner.setAdapter(new SubjectSpinnerAdapter(this, db.getAllSubjects()));

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick_white, new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void showDatePicker(View v) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(AssessmentInput.this, R.style.DatePickerTheme, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date();
            try {
                d = format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                String dString = FormattingUtils.dateToString(d);
                mDateInput.setText(dString);
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            } finally {
                Date now = new Date();
                if(d.before(now)) {
                    mMarkLayout.setVisibility(View.VISIBLE);
                    //TODO- Add the mark input
                } else {
                    new AlertDialog.Builder(AssessmentInput.this)
                            .setTitle("Marking")
                            .setMessage("Once the date entered has passed you will be prompted for your mark")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        }
    };

    @Override
    public void finish() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.finish();
    }
}
