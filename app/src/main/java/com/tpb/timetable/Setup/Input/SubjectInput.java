package com.tpb.timetable.Setup.Input;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.ViewGroup;

import com.thebluealliance.spectrum.SpectrumPalette;
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
    private SpectrumPalette pallete;



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

        pallete = (SpectrumPalette) findViewById(R.id.palette);
        pallete.setColors(getResources().getIntArray(R.array.material_primary));
        pallete.setOnColorSelectedListener(this);


        try {
            mCurrentSubject = (Subject) i.getSerializableExtra("subject");
            pallete.setSelectedColor(mCurrentSubject.getColor());
            setTitle("Edit Subject");
        } catch(Exception e) {
            mEditing = false;
            mCurrentSubject = new Subject();
            setTitle("New subject");
        }

        UIHelper.theme((ViewGroup) findViewById(R.id.text_input));
    }

    @Override
    public void onColorSelected(@ColorInt int color) {
        mCurrentSubject.setColor(color);
    }
}


