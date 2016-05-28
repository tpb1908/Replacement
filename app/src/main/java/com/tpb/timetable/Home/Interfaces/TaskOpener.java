package com.tpb.timetable.Home.Interfaces;

import android.support.annotation.Nullable;
import android.view.View;

import com.tpb.timetable.Data.Templates.Task;

/**
 * Created by theo on 01/05/16.
 */
public interface TaskOpener {

    void openTask(Task t, @Nullable View v);

    void openReminder(Task r, @Nullable View v);

    void openHomework(Task h, @Nullable View v);

}
