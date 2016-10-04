package com.tpb.timetable.Setup.Input;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.tpb.timetable.Data.DBHelper;
import com.tpb.timetable.Data.Templates.ClassTime;
import com.tpb.timetable.Data.Templates.Subject;
import com.tpb.timetable.Home.Input.SubjectSpinnerAdapter;
import com.tpb.timetable.R;
import com.tpb.timetable.SlidingPanel.SlidingPanel;
import com.tpb.timetable.Utils.Format;
import com.tpb.timetable.Utils.UIHelper;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by theo on 30/09/16.
 */

public class ClassInput extends SlidingPanel {
    private static final String TAG = "ClassInput";
    private ClassTime mCurrentClass;
    private boolean mEditing;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.input_class);
        setPrimaryColors(UIHelper.getPrimary(), UIHelper.getPrimaryDark());
        enableFullscreen();

        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0),
                    i.getIntExtra("topOffset", 0),
                    i.getIntExtra("viewWidth", 0),
                    i.getIntExtra("viewHeight", 0));
        }
        final int day = i.getIntExtra("day", 2); //Default monday


        mCurrentClass = new ClassTime();
        mCurrentClass.setDay(day);

        final TextInputLayout startWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_start_time);
        final TextInputEditText start = (TextInputEditText) findViewById(R.id.edittext_class_start_time);
        final TextInputLayout endWrapper = (TextInputLayout) findViewById(R.id.wrapper_edittext_class_end_time);
        final TextInputEditText end = (TextInputEditText) findViewById(R.id.edittext_class_end_time);

        final Spinner subjectSpinner = (Spinner) findViewById(R.id.spinner_subject);
        final SubjectSpinnerAdapter ssa = new SubjectSpinnerAdapter(this, DBHelper.getInstance(this).getAllSubjects());
        subjectSpinner.setAdapter(ssa);

        try {
            mCurrentClass = (ClassTime) i.getSerializableExtra("class");
            mEditing = true;
            start.setText(Format.format(mCurrentClass.getStartTime()));
            end.setText(Format.format(mCurrentClass.getEndTime()));
            subjectSpinner.setSelection(ssa.getPositionOfSubject(mCurrentClass.getSubjectID()));
            setTitle("Edit class");
        } catch(Exception e) {
            mEditing = false;
            mCurrentClass = new ClassTime();
            mCurrentClass.setDay(day);
            setTitle("New class");
        }

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int min = now.get(Calendar.MINUTE);
                if(mCurrentClass.getStartTime() != -1) {
                    hour = Format.getHour(mCurrentClass.getStartTime());
                    min = Format.getMinute(mCurrentClass.getStartTime())
;                }
                final TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        start.setText(Format.format(hourOfDay, minute));
                        mCurrentClass.setStartTime((hourOfDay*60) + minute);
                    }
                },
                        hour,
                        min,
                        true
                );
                tpd.setAccentColor(UIHelper.getAccent());
                tpd.setThemeDark(UIHelper.isDarkTheme());
                tpd.show(getFragmentManager(), TAG);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int min = now.get(Calendar.MINUTE);
                if(mCurrentClass.getEndTime() != -1) {
                    hour = Format.getHour(mCurrentClass.getEndTime());
                    min = Format.getMinute(mCurrentClass.getEndTime())
                    ;                }
                final TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        end.setText(Format.format(hourOfDay, minute));
                        mCurrentClass.setEndTime((hourOfDay*60) + minute);
                    }
                },
                        hour,
                        min,
                        true
                );
                tpd.setAccentColor(UIHelper.getAccent());
                tpd.setThemeDark(UIHelper.isDarkTheme());
                tpd.show(getFragmentManager(), TAG);
            }
        });


        setFab(UIHelper.getAccent(), R.drawable.fab_icon_tick_white, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error = false;
                final Subject s = ssa.getSubject(subjectSpinner.getSelectedItemPosition());
                mCurrentClass.setSubject(s);
                if(start.getText().toString().isEmpty()) {
                    error = true;
                    startWrapper.setError("Enter a start time");
                } else {
                    startWrapper.setError(null);
                }
                if(end.getText().toString().isEmpty()) {
                    error = true;
                    endWrapper.setError("Enter an end time");
                } else {
                    endWrapper.setError(null);
                }
                //TODO- Check that times are in the right order
                final DBHelper.ArrayWrapper<ClassTime> classes = DBHelper.getInstance(getApplicationContext()).getClassesForDay(day);
                for(int i = 0; i < classes.size(); i++) {
                    if(classes.get(i).overlaps(mCurrentClass) && !classes.get(i).equals(mCurrentClass)) {
                        error = true;
                        final ClassTime overlap = classes.get(i);
                        new AlertDialog.Builder(ClassInput.this)
                                .setTitle("Overlapping term")
                                .setMessage(String.format(
                                        getApplicationContext().getString(R.string.error_overlapping_classes),
                                        overlap.getSubject().getName()))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        classes.remove(overlap);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                    }
                }
                if(!error) {
                    if(mEditing) {
                        classes.update(mCurrentClass);
                    } else {
                        classes.addToPosition(mCurrentClass);
                    }
                    finish();
                }

            }
        });
        UIHelper.theme(this, (ViewGroup) findViewById(R.id.content_container));

    }
}
