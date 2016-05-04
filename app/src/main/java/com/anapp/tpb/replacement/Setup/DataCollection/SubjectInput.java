package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.klinker.android.sliding.SlidingActivity;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;

/**
 * Created by Theo on 19/01/2016.
 */
public class SubjectInput extends SlidingActivity {

    private ArrayList<Subject> subjects;
    private FloatingActionButton.OnClickListener fabListener;

    private TextView subjectName;
    private TextView classRoom;
    private TextView teacher;
    private View colourBar;

    private boolean editing;
    private int colour;
    private Subject current;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_subject);
        subjectName = (TextView) findViewById(R.id.edittext_subject_name);
        classRoom = (TextView) findViewById(R.id.edittext_classroom_input);
        teacher = (TextView) findViewById(R.id.edittext_teacher_input);
        colourBar = findViewById(R.id.colour_bar);
        colour = R.color.colorPrimary;
        try {
            current = (Subject) getIntent().getSerializableExtra("editingSubject");
            subjectName.setText(current.getName());
            classRoom.setText(current.getClassroom());
            teacher.setText(current.getTeacher());
            colourBar.setBackgroundColor(current.getColor());
            colour = current.getColor();
            editing = true;
        } catch (Exception e) {
            editing = false;
        }
        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subject l = new Subject();
                l.setName(subjectName.getText().toString());
                l.setClassroom(classRoom.getText().toString());
                l.setTeacher(teacher.getText().toString());
                l.setColor(colour);
                if (editing) {
                    l.setId(current.getId());
                }
                if (l.getName() != null && l.getClassroom() != null && l.getTeacher() != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("subject", l);
                    returnIntent.putExtra("edited", editing);
                    setResult(RESULT_OK, returnIntent);
                    Log.d("Result", "Result returned. Finishing");
                    finish();
                } else {
                    new AlertDialog.Builder(SubjectInput.this)
                            .setTitle("Invalid input")
                            .setMessage("Please input all values")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Button press", "Positive");
                                }
                            })
                            .show();
                }
            }
        };
        colourBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpectrumDialog.Builder b = new SpectrumDialog.Builder(getApplicationContext());
                Resources r = getResources();
                int[] colors = new int[] {r.getColor(R.color.amber_500), r.getColor(R.color.blue_500), r.getColor(R.color.blue_grey_500),
                        r.getColor(R.color.brown_500), r.getColor(R.color.cyan_500), r.getColor(R.color.deep_orange_500), r.getColor(R.color.deep_purple_500),
                        r.getColor(R.color.green_500), r.getColor(R.color.indigo_500), r.getColor(R.color.lime_500), r.getColor(R.color.teal_500),
                        r.getColor(R.color.yellow_500), r.getColor(R.color.red_500), r.getColor(R.color.pink_500), r.getColor(R.color.light_blue_500)};
                b.setColors(colors);
                b.setDismissOnColorSelected(false);
                b.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int setColor) {
                        colour = setColor;
                        colourBar.setBackgroundColor(setColor);
                    }
                });

                b.build().show(getSupportFragmentManager(), "");

            }
        });
        if (editing) {
            setTitle("Edit Subject");
        } else {
            setTitle("New Subject");
        }
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
    }
}
