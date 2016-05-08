package com.anapp.tpb.replacement.Home.Utilities;

import android.widget.TextView;

/**
 * Created by theo on 07/05/16.
 */
public class StringUtils {

    public static int numLinesForTextView(TextView textView, String text) {
        String[] lines = text.split("\n");
        int numLines = lines.length-1;
        for(String s : lines) {
            if(textView.getPaint().breakText(s,  0, s.length(),
                    true, textView.getWidth(), null) > s.length()) {
                numLines ++;
            }
        }
        return numLines;
    }

}
