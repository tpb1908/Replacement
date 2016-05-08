package com.anapp.tpb.replacement.Setup2.Input;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Term;
import com.klinker.android.sliding.SlidingActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by pearson-brayt15 on 21/04/2016.
 */
public class TermInput extends SlidingActivity {
    private static final String TAG = "TermInput";
    private Calendar calendar;
    private TextView termNameEntry;
    private TextView startDateText;
    private TextView endDateText;
    private Calendar startDate;
    private Calendar endDate;
    private ArrayList<Term> terms;
    private FloatingActionButton.OnClickListener fabListener;
    private boolean editing = false;
    private Term editingTerm;
    private boolean startEnd = true;


    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        terms = (ArrayList<Term>) getIntent().getSerializableExtra("terms");
        setContent(R.layout.input_term);
        calendar = Calendar.getInstance();
        startDateText = (EditText) findViewById(R.id.termStartDateTextView);
        endDateText = (EditText) findViewById(R.id.termEndDateTextView);
        termNameEntry = (EditText) findViewById(R.id.termNameInput);

        try {
            editingTerm = (Term) getIntent().getSerializableExtra("editingTerm");
            terms.remove(editingTerm);
            startDate = new GregorianCalendar();
            startDate.setTimeInMillis(editingTerm.getStartDate());
            Date d = new Date(editingTerm.getStartDate());
            startDateText.setText(d.toString().substring(0, 10));
            d.setTime(editingTerm.getEndDate());
            endDateText.setText(d.toString().substring(0, 10));
            endDate = new GregorianCalendar();
            endDate.setTimeInMillis(editingTerm.getEndDate());
            termNameEntry.setText(editingTerm.getName());
            editing = true;

        } catch (Exception e) {
            Log.i(TAG, "Not editing a term");
        }
        if (editing) {
            setTitle("Edit Term");
        } else {
            setTitle("New Term");
        }
        addListeners();
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);

        // startDatePicker.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up)); //This only sets the animation for the view, not the popup
    }

    private void addListeners() {
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEnd = true;
                new DatePickerDialog(TermInput.this, R.style.DatePickerTheme, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEnd = false;
                new DatePickerDialog(TermInput.this, R.style.DatePickerTheme, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        //TODO-Refactor this logic. It works, but looks horrid
        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorTitle="";
                String errorMessage="";
                if (startDate != null && endDate != null && !termNameEntry.getText().toString().equals("")) {
                    if (startDate.before(endDate)) {
                        Intent returnIntent = new Intent();
                        Term t;
                        if (editing) {
                            t = new Term(termNameEntry.getText().toString(), startDate.getTimeInMillis(), endDate.getTimeInMillis());
                            t.setId(editingTerm.getId());
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("term", t);
                        } else {
                            t = new Term(termNameEntry.getText().toString(), startDate.getTimeInMillis(), endDate.getTimeInMillis());
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("term", t);
                        }
                        if(!checkDateRangeOverlap(t)) {
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    } else {
                        errorTitle = "Invalid dates";
                        errorMessage = "Start date must be before end date";
                    }
                } else {
                    errorTitle = "Missing values";
                    errorMessage = "Please input all values";
                }
                new AlertDialog.Builder(TermInput.this)
                        .setTitle(errorTitle)
                        .setMessage(errorMessage)
                        .setPositiveButton("OK", null)
                        .show();
            }
        };
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                String dString = d.toString().substring(0, 10);
                if (startEnd) {
                    startDateText.setText(dString);
                    startDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                } else {
                    endDateText.setText(dString);
                    endDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Parsing exception in OnDateSetListener",e);
            }
        }

    };

    //FIXME- Boolean inversion
    private boolean checkDateRangeOverlap(Term toCheck) {
        Term overlap = null;
        if (terms != null) {
            for (Term t : terms) {
                if (toCheck.overlaps(t) ) {
                    overlap = t;
                    break;
                }
            }
        }
        if (overlap != null) {
            new AlertDialog.Builder(TermInput.this)
                    .setTitle("Overlapping terms")
                    .setMessage("Term overlaps with " + overlap.getName() + ".")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return false;
    }
}
