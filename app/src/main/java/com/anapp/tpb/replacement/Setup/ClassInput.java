package com.anapp.tpb.replacement.Setup;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

/**
 * Created by theo on 20/04/16.
 */
public class ClassInput extends SlidingActivity {
    private ArrayList<ClassTime> classesForDay;
    private ArrayList<Subject> subjects;
    private Spinner lessonSpinner;
    private ClassTime current;
    private int start = -1;
    private int end = -1;
    private boolean editing;
    private EditText startTime;
    private EditText endTime;
    private int day;

    public ClassInput() {
    }

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init (Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        enableFullscreen();
        lessonSpinner = (Spinner) findViewById(R.id.spinner_subject);
        startTime = (EditText) findViewById(R.id.edittext_class_start_time);
        endTime = (EditText) findViewById(R.id.edittext_class_end_time);
        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
        classesForDay = (ArrayList<ClassTime>) getIntent().getSerializableExtra("classes");
        day = getIntent().getIntExtra("day", 0);
        int spinnerDefaultIndex = 0;
        try {
            current = (ClassTime) getIntent().getSerializableExtra("editingClass");
            start = current.getStart();
            end = current.getEnd();
            String time; //Recommended to perform string concatenation outside of setText() method
            //Time is stored in a 0000 2359 format. If hour < 10, then time < 100 -> String must be split differently
            if (start < 1000) {
                time = (Integer.toString(start).substring(0, 1) + ":" + Integer.toString(start).substring(1));
            } else {
                time = (Integer.toString(start).substring(0, 2) + ":" + Integer.toString(start).substring(2));
            }
            startTime.setText(time);
            if (end < 1000) {
                time = (Integer.toString(end).substring(0, 1) + ":" + Integer.toString(end).substring(1));
            } else {
                time = (Integer.toString(end).substring(0, 2) + ":" + Integer.toString(end).substring(2));
            }
            for(Subject s : subjects) {
                if(s.getId() == current.getSubjectID()) {
                    spinnerDefaultIndex = subjects.indexOf(s);
                    break;
                }
            }
            endTime.setText(time);
            editing = true;
        } catch (Exception e) {
            editing = false;
        }
        lessonSpinner.setAdapter(new SpinnerAdapter(getApplicationContext(),  subjects.toArray(new Subject[0])));
        lessonSpinner.setSelection(spinnerDefaultIndex);
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

        FloatingActionButton.OnClickListener fabListener = new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //finish();
            }
        };

        if (editing) {
            setTitle("Edit Class");
        } else {
            setTitle("New Class");
        }

        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
    }

    private void displayTimePicker(final boolean startEnd) {
        TimePickerDialog mTimePicker;
        //Creating dialog
        mTimePicker = new TimePickerDialog(ClassInput.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("Data", "Time received- Hour of " + hourOfDay + " Minute of " + minute);
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

    private class SpinnerAdapter extends ArrayAdapter<Subject> {

        public SpinnerAdapter(Context context, Subject[] subjects) {
            super(context, R.layout.listitem_subject_spinner, R.id.text_subject,  subjects);
        }

        @Override
        public View getDropDownView (int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent);
        }

        public View getCustomView(int position, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
            TextView name = (TextView) row.findViewById(R.id.text_subject);
            View colourBar = row.findViewById(R.id.colour_bar);
            String text = this.getItem(position).getName() + ", " + this.getItem(position).getTeacher();
            name.setText(text);
            colourBar.setBackgroundColor(this.getItem(position).getColor());
            return row;
        }
    }



}
