package com.anapp.tpb.replacement.Setup2.Input;

import android.app.AlertDialog;
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

/**
 * Created by pearson-brayt15 on 21/04/2016.
 */
public class SubjectInput extends SlidingActivity {
    private static final String TAG = "SubjectInput";
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
        colourBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        if (editing) {
            setTitle("Edit Subject");
        } else {
            setTitle("New Subject");
        }
        addListeners();
        enableFullscreen();
        setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
        setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
    }

    private void addListeners() {
        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subject subject = new Subject();
                subject.setName(subjectName.getText().toString());
                subject.setClassroom(classRoom.getText().toString());
                subject.setTeacher(teacher.getText().toString());
                subject.setColor(colour);
                if (editing) {
                    subject.setId(current.getId());
                }
                if (subject.getName() != null && subject.getClassroom() != null && subject.getTeacher() != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("subject", subject);
                    returnIntent.putExtra("edited", editing);
                    setResult(RESULT_OK, returnIntent);
                    Log.i(TAG, "Returning new subject " + subject.toString());
                    finish();
                } else {
                    new AlertDialog.Builder(SubjectInput.this)
                            .setTitle("Invalid input")
                            .setMessage("Please input all values")
                            .setPositiveButton("OK", null)
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
    }
}
