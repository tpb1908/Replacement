package com.tpb.timetable.Home.Input;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.tpb.timetable.Data.Templates.Task;
import com.tpb.timetable.R;
import com.tpb.timetable.SlidingPanel.SlidingPanel;
import com.tpb.timetable.Utils.ThemeHelper;

/**
 * Created by theo on 11/05/16.
 */
public class ReminderInput extends SlidingPanel {
    private static final String TAG = "ReminderInput";
    private Task mCurrentTask;


    @Override
    public void init(Bundle savedInstanceState) {
        setPrimaryColors(ThemeHelper.getPrimary(), ThemeHelper.getPrimaryDark());
        enableFullscreen();
        setContent(R.layout.input_reminder);
        final Intent i = getIntent();
        if(i.getBooleanExtra("hasOpenPosition", false)) {
            expandFromPoints(i.getIntExtra("leftOffset", 0), i.getIntExtra("topOffset", 0), i.getIntExtra("viewWidth", 0), i.getIntExtra("viewHeight", 0));
        }

    }

    @Override
    public void finish() {
        if(getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.finish();
    }
}
