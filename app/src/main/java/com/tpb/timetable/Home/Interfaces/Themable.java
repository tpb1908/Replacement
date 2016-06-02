package com.tpb.timetable.Home.Interfaces;

import android.view.ViewGroup;

/**
 * Created by theo on 30/05/16.
 * This interface will be used to theme the application
 * throughout.
 * When an activity needs to use the ColorResources class, it
 * must add itself to the listener list.
 * When the ColorResource is updated, it calls all of the listeners
 * so that they can be recreated.
 */
public interface Themable {

    ViewGroup getViews();

}
