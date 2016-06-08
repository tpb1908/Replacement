package com.tpb.timetable.Home;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

import com.gordonwong.materialsheetfab.AnimatedFab;
import com.tpb.timetable.R;

/**
 * Created by theo on 10/04/16.
 */
public class SheetFab extends FloatingActionButton implements AnimatedFab {
    private static final String TAG = "SheetFab";

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
        // Set FAB's translation
        setTranslation(translationX, translationY);
        // Only use scale animation if FAB is hidden
        if (getVisibility() != View.VISIBLE) {
            // Pivots indicate where the animation begins from
            final float pivotX = getPivotX() + translationX;
            final float pivotY = getPivotY() + translationY;

            ScaleAnimation anim;
            // If pivots are 0, that means the FAB hasn't been drawn yet so just use the
            // center of the FAB
            if (pivotX == 0 || pivotY == 0) {
                anim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                anim = new ScaleAnimation(0, 1, 0, 1, pivotX, pivotY);
            }
            // Animate FAB expanding
            anim.setDuration(200);
            anim.setInterpolator(getInterpolator());
            startAnimation(anim);
            setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void hide() {
        if (getVisibility() == View.VISIBLE) {
            // Pivots indicate where the animation begins from
            final float pivotX = getPivotX() + getTranslationX();
            final float pivotY = getPivotY() + getTranslationY();

            // Animate FAB shrinking
            ScaleAnimation anim = new ScaleAnimation(1, 0, 1, 0, pivotX, pivotY);
            anim.setDuration(200);
            anim.setInterpolator(getInterpolator());
            startAnimation(anim);
            setVisibility(View.INVISIBLE);
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
