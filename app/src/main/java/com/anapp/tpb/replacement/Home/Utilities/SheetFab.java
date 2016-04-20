package com.anapp.tpb.replacement.Home.Utilities;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.gordonwong.materialsheetfab.AnimatedFab;

/**
 * Created by theo on 10/04/16.
 */
public class SheetFab extends FloatingActionButton implements AnimatedFab {

    public SheetFab (Context context) {
        super(context);
    }

    public SheetFab (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SheetFab (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void show () {
        super.show();
    }

    @Override
    public void show (float translationX, float translationY) {

    }






}
