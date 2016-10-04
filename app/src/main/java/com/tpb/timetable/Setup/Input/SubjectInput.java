package com.tpb.timetable.Setup.Input;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thebluealliance.spectrum.SpectrumPalette;
import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.R;
import com.tpb.timetable.SlidingPanel.SlidingPanel;
import com.tpb.timetable.Utils.UIHelper;

/**
 * Created by theo on 24/09/16.
 */

public class SubjectInput extends SlidingPanel implements SpectrumPalette.OnColorSelectedListener {
    private static final String TAG = "SubjectInput";
    private Subject mCurrentSubject;
    private boolean mEditing;


    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_subject);
        setPrimaryColors(UIHelper.getPrimary(), UIHelper.getPrimaryDark());
        enableFullscreen();
        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }

        final SpectrumPalette pallete = (SpectrumPalette) findViewById(R.id.palette);
        pallete.setColors(getResources().getIntArray(R.array.material_primary));
        pallete.setOnColorSelectedListener(this);

        final TextInputEditText name = (TextInputEditText) findViewById(R.id.edittext_subject_name);
        final TextInputLayout nameWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_subject_name);
        final TextInputEditText teacher = (TextInputEditText) findViewById(R.id.edittext_subject_teacher);
        final TextInputLayout teacherWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_subject_teacher);
        final TextInputEditText classroom = (TextInputEditText) findViewById(R.id.edittext_subject_classroom);
        final TextInputLayout classroomWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_subject_classroom);


        try {
            mCurrentSubject = (Subject) i.getSerializableExtra("subject");
            mEditing = true;
            pallete.setSelectedColor(mCurrentSubject.getColor());
            name.setText(mCurrentSubject.getName());
            teacher.setText(mCurrentSubject.getTeacher());
            classroom.setText(mCurrentSubject.getClassroom());
            setTitle("Edit Subject");
        } catch(Exception e) {
            mEditing = false;
            mCurrentSubject = new Subject();
            setTitle("New Subject");
        }

        UIHelper.theme(this, (ViewGroup) findViewById(R.id.content_container));

        setFab(UIHelper.getAccent(),
                R.drawable.fab_icon_tick_white, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean error = false;
                        if(name.getText().toString().isEmpty()) {
                            error = true;
                            nameWrapper.setError("Enter a subject name");
                        } else {
                            nameWrapper.setError(null);
                            mCurrentSubject.setName(name.getText().toString());
                        }
                        if(teacher.getText().toString().isEmpty()) {
                            error = true;
                            teacherWrapper.setError("Enter a teacher name");
                        } else {
                            teacherWrapper.setError(null);
                            mCurrentSubject.setTeacher(teacher.getText().toString());
                        }
                        if(classroom.getText().toString().isEmpty()) {
                            error = true;
                            classroomWrapper.setError("Enter a classroom");
                        } else {
                            classroomWrapper.setError(null);
                            mCurrentSubject.setClassroom(classroom.getText().toString());
                        }

                        if(mCurrentSubject.getColor() == 0) {
                            error = true;
                            Toast.makeText(getApplicationContext(), "Select a colour", Toast.LENGTH_LONG).show();
                        }

                        if(!error) {
                            if(mEditing) {
                                DBHelper.getInstance(getApplicationContext()).getAllSubjects().update(mCurrentSubject);
                            } else {
                                DBHelper.getInstance(getApplicationContext()).getAllSubjects().add(mCurrentSubject);
                            }
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        mCurrentSubject.setColor(color);
    }
}


