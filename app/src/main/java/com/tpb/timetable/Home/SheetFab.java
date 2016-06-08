package com.tpb.timetable.Home;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.gordonwong.materialsheetfab.AnimatedFab;
import com.tpb.timetable.R;

/**
 * Created by theo on 10/04/16.
 */
public class SheetFab extends FloatingActionButton implements AnimatedFab {
    private static final String TAG = "SheetFab";
    private boolean offScreen = false;

    public SheetFab(Context context) {
        super(context);
    }

    public SheetFab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SheetFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void show () {
        show(0,0);
    }

    @Override
    public void show (float translationX, float translationY) {
        if(offScreen) {
            animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            offScreen = false;
        }
    }

    @Override
    public void hide() {
        if(!offScreen) {
            animate().translationY(this.getHeight() + 48).setInterpolator(new AccelerateInterpolator(2)).start();
            offScreen = true;
        }
    }
    private void setTranslation(float translationX, float translationY) {
        animate().setInterpolator(getInterpolator()).setDuration(200)
                .translationX(translationX).translationY(translationY);
    }

    private Interpolator getInterpolator() {
        return AnimationUtils.loadInterpolator(getContext(), R.interpolator.msf_interpolator);
    }
}
