package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
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
    private ArrayList<Subject> subjects; //Lessons must have their database IDs
    private ArrayList<ClassTime> classes;
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
        setContent(R.layout.class_input);
        lessonSpinner = (Spinner) findViewById(R.id.subjectSpinner);
        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);
        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
        classes = (ArrayList<ClassTime>) getIntent().getSerializableExtra("classes");
        day = getIntent().getIntExtra("day", 0);
        try {
            current = (ClassTime) getIntent().getSerializableExtra("editingClass");
            start = current.getStart();
            end = current.getEnd();
            if (start < 1000) {
                startTime.setText(Integer.toString(start).substring(0, 1) + ":" + Integer.toString(start).substring(1));
            } else {
                startTime.setText(Integer.toString(start).substring(0, 2) + ":" + Integer.toString(start).substring(2));
            }
            if (end < 1000) {
                endTime.setText(Integer.toString(end).substring(0, 1) + ":" + Integer.toString(end).substring(1));
            } else {
                endTime.setText(Integer.toString(end).substring(0, 2) + ":" + Integer.toString(end).substring(2));
            }
            //TODO- Work out how to select the selected subject
            editing = true;
        } catch (Exception e) {
            editing = false;
        }
        Log.d("Editing", "" + editing);
        Log.d("Received lesson array", subjects.toString());

        final String[] lessonsA = new String[subjects.size()];
        int i = 0;
        for (Subject l : subjects) {
            lessonsA[i++] = l.getName();
        }
        lessonSpinner.setAdapter(new LessonArrayAdapter(getApplicationContext(), R.layout.subject_spinner_layout, lessonsA));

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
                c.setSubjectID(subjects.get(lessonSpinner.getSelectedItemPosition()).getId() - 1);
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
        ClassTime overlap = null;
        if (editing) {
            classes.remove(toCheck);
        }
        for (ClassTime ct : classes) {
            if (ct.getDay() == day) {
                if (ct.getStart() < toCheck.getEnd() && ct.getEnd() > toCheck.getStart()) {
                    overlap = ct;
                    Log.d("Overlap found", ct.toString());
                    break;
                }
            }
        }
        return overlap;
    }

    private void displayTimePicker(final boolean tap) {
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(ClassInput.this, R.style.datePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("Time received", "Hour of " + hourOfDay + " Minute of " + minute);
                String output = hourOfDay + ":";
                if (minute < 10) {
                    output += "0" + minute;
                } else {
                    output += minute;
                }

                if (tap) {
                    startTime.setText(output);
                    start = (hourOfDay * 100) + minute; //Storing time in format (hh:mm) from 0000 to 2359
                } else {
                    endTime.setText(output);
                    end = (hourOfDay * 100) + minute;
                }
            }
        }, 8, 0, true); //Use 24 hour time. Default of 8am
        if (tap) {
            mTimePicker.setTitle("Select start time");
        } else {
            mTimePicker.setTitle("Select end time");
        }
        mTimePicker.show();
    }

    private void displayMessage(int messageID, ClassTime overlap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassInput.this, R.style.DialogTheme);
        switch (messageID) {
            case 0:
                builder.setTitle("Invalid time range")
                        .setMessage("Please input start and end times")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Button press- ", "Positive");
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
                builder.setTitle("Invalid time range")
                        .setMessage("Overlap with another lesson: " + subjects.get(overlap.getSubjectID()).getName() +
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

    private class LessonArrayAdapter extends ArrayAdapter<String> {

        public LessonArrayAdapter(Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.subject_spinner_layout, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
            TextView name = (TextView) row.findViewById(R.id.subjectText);
            View colourBar = row.findViewById(R.id.colourBar);
            name.setText(subjects.get(position).getName() + ", " + subjects.get(position).getTeacher());
            colourBar.setBackgroundColor(subjects.get(position).getColor());
            Log.d("Spinner binding", subjects.get(position).toString());
            return row;
        }
    }
}