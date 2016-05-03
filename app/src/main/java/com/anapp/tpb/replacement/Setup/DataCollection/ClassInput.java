package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
 * Created by Theo on 19/02/2016.
 */
public class ClassInput extends SlidingActivity {
    private ArrayList<Subject> subjects; //Classes must have their database IDs
    private ArrayList<ClassTime> classesForDay;
    private Spinner lessonSpinner;
    private FloatingActionButton.OnClickListener fabListener;
    private ClassTime current;
    private int day;
    private int start = -1;
    private int end = -1;
    private boolean editing;
    private EditText startTime;
    private EditText endTime;

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        lessonSpinner = (Spinner) findViewById(R.id.spinner_subject);
        startTime = (EditText) findViewById(R.id.edittext_start_time);
        endTime = (EditText) findViewById(R.id.editttext_end_time);
        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
        classesForDay = (ArrayList<ClassTime>) getIntent().getSerializableExtra("classes");
        Log.d("Classes passed", "Classes passed " + classesForDay);
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
            Log.d("Subject", "Class of " + current);
            for(Subject s : subjects) {
                if(s.getId() == current.getSubjectID()) {
                    Log.d("Subject", "Found subject with id of " + s.getId());
                    spinnerDefaultIndex = subjects.indexOf(s);
                }
            }
            endTime.setText(time);
            editing = true;
        } catch (Exception e) {
            editing = false;
        }
        final String[] subjectNames = new String[subjects.size()];
        int i = 0;
        for (Subject s : subjects) {
            subjectNames[i++] = s.getName();
        }

        lessonSpinner.setAdapter(new ClassArrayAdapter(getApplicationContext(), R.layout.listitem_subject_spinner, subjectNames));
        lessonSpinner.setSelection(spinnerDefaultIndex);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTimePicker(true);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTimePicker(false);
            }
        });
        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassTime c = new ClassTime();
                c.setDay(day);
                Log.d("Spinner position ", "" + lessonSpinner.getSelectedItemPosition());
                c.setSubjectID(subjects.get(lessonSpinner.getSelectedItemPosition()).getId());
                if (start != -1 && end != 0 - 1) {
                    if (start < end) {
                        c.setStart(start);
                        c.setEnd(end);
                        if (editing) {
                            c.setId(current.getId());
                        }
                        ClassTime o = checkOverlap(c);
                        if (o == null) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("class", c);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        } else {
                            displayMessage(2, o);
                        }
                    } else {
                        displayMessage(1, null);
                    }
                } else {
                    displayMessage(0, null);
                }
            }
        };
        if (editing) {
            setTitle("Edit Class");
        } else {
            setTitle("New Class");
        }
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
    }

    private ClassTime checkOverlap(ClassTime toCheck) {
        //TODO- Consider returning an array of values, in order to inform user of their stupidity
        ClassTime overlap = null;
        if (editing) {
            //If the current class is in the ArrayList, it will be found to overlap with itself
            classesForDay.remove(toCheck);
        }
        for (ClassTime ct : classesForDay) {
            Log.d("Data", "Class of " + ct.toString());
            if (ct.getDay() == toCheck.getDay()) {
                Log.d("Data", "Class of " + ct.toString());
                //See here -http://stackoverflow.com/a/325964/4191572  > used rather than >= as lessons times may be continuous
                if (ct.getStart() < toCheck.getEnd() && ct.getEnd() > toCheck.getStart()) {
                    overlap = ct;
                    Log.d("Data", "Overlap found " + ct.toString());
                    break;
                }
            }
        }
        return overlap;
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

    //Method displays an error mMessage.
    private void displayMessage(int messageID, ClassTime overlap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassInput.this, R.style.DialogTheme);
        switch (messageID) {
            case 0:
                builder.setTitle("Invalid time range")
                        .setMessage("Please input start and end times")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case 1:
                builder.setTitle("Invalid time range")
                        .setMessage("Start time must be before end time")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
                            }
                        })
                        .show();
                break;
            case 2:
                // + subjects.get(overlap.getSubjectID()).getName()
                Log.d("Data", "ClassTime passed to message handler " + overlap.toString());
                builder.setTitle("Invalid time range")
                        .setMessage("Overlap with another class: " +
                                ", from " + overlap.getStart() + " to " + overlap.getEnd())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
                            }
                        })
                        .show();
                break;
        }
    }

    private class ClassArrayAdapter extends ArrayAdapter<String> { //Change to subject

        public ClassArrayAdapter (Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);
        }



        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position,parent);
        }

        public View getCustomView(int position, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
            TextView name = (TextView) row.findViewById(R.id.text_class_past_info);
            View colourBar = row.findViewById(R.id.colour_bar);
            name.setText(subjects.get(position).getName() + ", " + subjects.get(position).getTeacher());
            colourBar.setBackgroundColor(subjects.get(position).getColor());
            return row;
        }
    }
}