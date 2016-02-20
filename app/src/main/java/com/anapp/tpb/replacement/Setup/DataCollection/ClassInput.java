package com.anapp.tpb.replacement.Setup.DataCollection;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Lesson;
import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;

/**
 * Created by Theo on 19/02/2016.
 */
public class ClassInput extends SlidingActivity {
    private ArrayList<Lesson> lessons; //Lessons must have their database IDs
    private ArrayList<Class> classes;
    private Spinner lessonSpinner;

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.class_input);

        lessons = (ArrayList<Lesson>) getIntent().getSerializableExtra("lessons");
        Log.d("Received lesson array", lessons.toString());

        lessonSpinner = (Spinner) findViewById(R.id.lessonSpinner);
        String[] lessonsA = new String[lessons.size()];
        int i = 0;
        for (Lesson l : lessons) {
            lessonsA[i++] = l.getName();
        }
        lessonSpinner.setAdapter(new LessonArrayAdapter(getApplicationContext(), R.layout.lesson_spinner_layout, lessonsA));

        setTitle("New class");
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));

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
            View row = inflater.inflate(R.layout.lesson_spinner_layout, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
            TextView name = (TextView) row.findViewById(R.id.lessonNameText);
            View colourBar = row.findViewById(R.id.colourBar);
            name.setText(lessons.get(position).getName());
            colourBar.setBackgroundColor(lessons.get(position).getColor());
            Log.d("Spinner binding", lessons.get(position).toString());
            return row;
        }
    }


}
