package com.anapp.tpb.replacement.Home.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by theo on 25/04/16.
 */
//Use this to return the correct resource ID for a particular colour, which is itself stored in xml
    //http://developer.android.com/reference/android/content/SharedPreferences.html
    //Use the listener to updateCurrent colors throughout
public class ColorPicker {
    private static final String NAME = "COLORS";
    private static SharedPreferences preferences;
    private static int[] currentColors;


    public static boolean writeCurrentColors(Context context, int[] colors) {
        if(preferences == null) {
            preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putInt("color_primary", colors[0]);
            editor.putInt("color_primary_light", colors[1]);
            editor.putInt("color_orimary_dark", colors[2]);
            editor.putInt("color_accent", colors[3]);
            editor.apply();
            currentColors = colors.clone();
        } catch(IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public static int[] getCurrentColors(Context context) {
        if(currentColors == null) {
            currentColors = new int[4];
        } else {
            return currentColors;
        }
        if(preferences == null) {
            preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        //TODO- Default values
        currentColors[0] = preferences.getInt("color_primary", -1);
        currentColors[1] = preferences.getInt("color_primary_light", -1);
        currentColors[2] = preferences.getInt("color_primary_dark", -1);
        currentColors[3] = preferences.getInt("color_accent", -1);

        return currentColors;
    }

    public static int getPrimary(Context context) {
        if(currentColors == null) {
            getCurrentColors(context);
        }
        return currentColors[0];
    }

    public static int getPrimaryDark(Context context) {
        if(currentColors == null) {
            getCurrentColors(context);
        }
        return currentColors[1];
    }




}
