package com.tpb.timetable.Home.Interfaces;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by theo on 03/07/16.
 */
public interface AdapterManager<T> {

    void open(T t, @Nullable View v);

    void removed(T t);


}
