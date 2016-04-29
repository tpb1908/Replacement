package com.anapp.tpb.replacement.Setup2.Input;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.anapp.tpb.replacement.Home.Utilities.SubjectSpinnerAdapter;
import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

/**
 * Created by theo on 20/04/16.
 */
public class ClassInput extends SlidingActivity {
    private static final String TAG = "ClassInput";
    private ArrayList<Subject> subjects;
    private ArrayList<ClassTime> classesForDay;
    private ClassTime current;
    private int start = -1;
    private int end = -1;
    private boolean editing;
    private Spinner lessonSpinner;
    private EditText startTime;
    private EditText endTime;
    private FloatingActionButton.OnClickListener fabListener;
    private int day;

    public ClassInput () {
    }

    public void onCreate (Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init (Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        lessonSpinner = (Spinner) findViewById(R.id.subjectSpinner);
        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);
        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
        classesForDay = (ArrayList<ClassTime>) getIntent().getSerializableExtra("classes");
        day = getIntent().getIntExtra("day", 0);
        int spinnerDefaultIndex = 0;
        try {
            current = (ClassTime) getIntent().getSerializableExtra("editingClass");
            classesForDay.remove(current);
            start = current.getStart();
            end = current.getEnd();
            String time; //Recommended to perform string concatenation outside of setText() method
            //Time is stored in a 0000 2359 format. If hour < 10, then time < 100 -> String must be split differently
            time = TimeUtils.format(start);
            startTime.setText(time);
            time = TimeUtils.format(end);
            endTime.setText(time);
            for (Subject s : subjects) {
                if (s.getId() == current.getSubjectID()) {
                    spinnerDefaultIndex = subjects.indexOf(s);
                    break;
                }
            }
            editing = true;
            Log.i(TAG, "Editing a class " + current);
        } catch (Exception e) {
            editing = false;
        }
        lessonSpinner.setAdapter(new SubjectSpinnerAdapter(getApplicationContext(), subjects));
        lessonSpinner.setSelection(spinnerDefaultIndex);

        addListeners();
        if (editing) {
            setTitle("Edit Class");
        } else {
            setTitle("New Class");
        }
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
    }

    private void addListeners() {
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                displayTimePicker(true);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                displayTimePicker(false);
            }
        });

        fabListener = new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if(start != -1 && end != -1) {
                    ClassTime ct = new ClassTime();
                    if(editing) {
                        ct.setId(current.getId());
                        ct.setSubjectID(current.getSubjectID());
                    } else {
                        ct.setSubjectID(subjects.get(lessonSpinner.getSelectedItemPosition()).getId());
                    }
                    ct.setStart(start);
                    ct.setEnd(end);
                    ct.setDay(day);
                    if(!checkTimeRangeOverlap(ct)) {
                        Intent i = new Intent();
                        i.putExtra("edited", editing);
                        i.putExtra("class", ct);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                } else {
                    new AlertDialog.Builder(ClassInput.this)
                            .setTitle("Missing time")
                            .setMessage("Please input both start and end times")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }
        };
    }
    //FIXME- Boolean inversion
    private boolean checkTimeRangeOverlap(ClassTime toCheck) {
        for(ClassTime ct : classesForDay) {
            if(ct.overlaps(toCheck)) {
                new AlertDialog.Builder(ClassInput.this)
                        .setTitle("Overlap")
                        .setMessage("A class from " + start + " to " + end +
                        " will overlap another class from " + ct.getStart() +
                        " to " + ct.getEnd())
                        .setPositiveButton("OK", null)
                        .show();
                return true;
            }
        }
        return false;
    }

    private void displayTimePicker (final boolean startEnd) {
        TimePickerDialog mTimePicker;
        //Creating dialog
        mTimePicker = new TimePickerDialog(ClassInput.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet (TimePicker view, int hourOfDay, int minute) {
                String output = hourOfDay + ":";
                //Formatting the string
                if (minute < 10) {
                    output += "0" + minute;
                } else {
                    output += minute;
                }
                if (startEnd) {
                    startTime.setText(output);
                    start = (hourOfDay * 100) + minute;
                } else {
                    endTime.setText(output);
                    end = (hourOfDay * 100) + minute;
                }
            }
        }, 9, 0, true); //Use 24 hour time. Default of 9am
        if (startEnd) {
            mTimePicker.setTitle("Select start time");
        } else {
            mTimePicker.setTitle("Select end time");
        }
        mTimePicker.show();
    }

}
