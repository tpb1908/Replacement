package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
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
    private ArrayList<Class> classes;
    private Spinner lessonSpinner;
    private ClassTime classTime;

    private EditText startTime;
    private EditText endTime;

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.class_input);

        subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
        Log.d("Received lesson array", subjects.toString());

        lessonSpinner = (Spinner) findViewById(R.id.subjectSpinner);
        String[] lessonsA = new String[subjects.size()];
        int i = 0;
        for (Subject l : subjects) {
            lessonsA[i++] = l.getName();
        }
        lessonSpinner.setAdapter(new LessonArrayAdapter(getApplicationContext(), R.layout.subject_spinner_layout, lessonsA));

        startTime = (EditText) findViewById(R.id.startTime);
        endTime = (EditText) findViewById(R.id.endTime);

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

        setTitle("New class");
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));

    }

    private void displayTimePicker(final boolean tap) {
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(ClassInput.this, R.style.datePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String output = hourOfDay + ":";
                if (minute < 10) {
                    output += "0" + minute;
                } else {
                    output += minute;
                }
                if (tap) {
                    startTime.setText(output);

                } else {
                    endTime.setText(output);
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
            TextView name = (TextView) row.findViewById(R.id.subjectNameText);
            View colourBar = row.findViewById(R.id.colourBar);
            name.setText(subjects.get(position).getName());
            colourBar.setBackgroundColor(subjects.get(position).getColor());
            Log.d("Spinner binding", subjects.get(position).toString());
            return row;
        }
    }


}
