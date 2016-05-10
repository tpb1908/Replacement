package com.anapp.tpb.replacement.Home.Interfaces;

import android.view.View;

import com.anapp.tpb.replacement.Storage.TableTemplates.Task;

/**
 * Created by theo on 01/05/16.
 */
public interface TaskOpener {

    void openTask(Task t);

    void openReminder(Task r);

    void openHomework(Task h);

    void openTask(Task t, View v);

    void openReminder(Task r, View v);

    void openHomework(Task h, View v);

}
