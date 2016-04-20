package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
 * Created by Theo on 19/01/2016.
 */
public class TermInput extends SlidingActivity {

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
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d("onDateSet- ", year + " " + (monthOfYear + 1) + " " + dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = format.parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                String dString = d.toString().substring(0, 10);
                if (startEnd) {
                    startDateText.setText(dString);
                    startDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    Log.d("Start date set- ", startDate.toString());
                } else {
                    endDateText.setText(dString);
                    endDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                    Log.d("End date set- ", endDate.toString());
                }

            } catch (ParseException e) {
                Log.d("Exception- ", e.getMessage());
            }
        }

    };

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        terms = (ArrayList<Term>) getIntent().getSerializableExtra("terms");
        setContent(R.layout.term_input);
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
            Log.d("Term Input", "No term to edit");
        }

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEnd = true;
                new DatePickerDialog(TermInput.this, R.style.datePickerTheme, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEnd = false;
                new DatePickerDialog(TermInput.this, R.style.datePickerTheme, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate != null && endDate != null && !termNameEntry.getText().toString().equals("")) {
                    if (startDate.before(endDate)) {
                        Intent returnIntent = new Intent();
                        Term t;
                        if (editing) {
                            t = new Term(termNameEntry.getText().toString(), startDate.getTimeInMillis(), endDate.getTimeInMillis());
                            Log.d("Editing", "Finished edit of term " + t.toString());
                            t.setId(editingTerm.getId());
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("term", t);
                        } else {
                            t = new Term(termNameEntry.getText().toString(), startDate.getTimeInMillis(), endDate.getTimeInMillis());
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("term", t);
                        }
                        Term overlap = checkDateRangeOverlap(t);
                        if (overlap != null) {
                            displayMessage(0, overlap.getName());
                        } else {
                            setResult(RESULT_OK, returnIntent);
                            Log.d("Result", " Result returned. Finishing");
                            finish();
                        }
                    } else {
                        displayMessage(1, "");
                    }
                } else {
                    displayMessage(2, "");
                }
            }
        };
        if (editing) {
            setTitle("Edit Term");
        } else {
            setTitle("New Term");
        }
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);

        // startDatePicker.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up)); //This only sets the animation for the view, not the popup
    }

    private void displayMessage(int messageId, String overlap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TermInput.this, R.style.DialogTheme);
        switch (messageId) {
            case 0:
                builder.setTitle("Invalid date range")
                        .setMessage("Date range overlaps another term: " + overlap)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
                            }
                        })
                        .show();
                break;
            case 1:
                builder.setTitle("Invalid date range")
                        .setMessage("Start date must be before end date")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
                            }
                        })
                        .show();
                break;
            case 2:
                builder.setTitle("Invalid input")
                        .setMessage("Please enter both dates and term name")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
                            }
                        })
                        .show();
                break;
        }


        new AlertDialog.Builder(TermInput.this)
                .setTitle("Invalid date range")
                .setMessage("Start date must be before end date")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Button press- ", "Positive");
                    }
                })
                .show();
    }

    private Term checkDateRangeOverlap(Term toCheck) {
        Term overlap = null;
        if (terms != null) {
            if (editing) {
            }
            for (Term t : terms) {
                Log.d("Checking for overlap ", t.getStartDate() + "    " + toCheck.getEndDate() + "    " + t.getEndDate() + "    " + toCheck.getStartDate());
                if ((t.getStartDate() <= toCheck.getEndDate()) && (t.getEndDate() >= toCheck.getStartDate())) {
                    overlap = t;
                    Log.d("Overlapping term ", t.toString());
                    break;
                }
            }
        }
        return overlap;
    }

}
