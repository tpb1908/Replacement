package com.anapp.tpb.replacement.Setup.DataCollection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.klinker.android.sliding.SlidingActivity;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

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
        subjectName = (TextView) findViewById(R.id.subjectNameInput);
        classRoom = (TextView) findViewById(R.id.classroomInput);
        teacher = (TextView) findViewById(R.id.teacherInput);
        colourBar = findViewById(R.id.colourBar);
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

                final ColorPicker cp = new ColorPicker(SubjectInput.this, Color.red(colour), Color.green(colour), Color.blue(colour));
                cp.show();

                Button ok = (Button) cp.findViewById(R.id.okColorButton);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        colour = cp.getColor();
                        colourBar.setBackgroundColor(colour);
                        cp.dismiss();
                    }
                });
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
