package com.tpb.timetable.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.tpb.timetable.R;

/**
 * Created by theo on 02/06/16.
 * I hate spelling colour like this
 */
public class ColoredSpace extends View {
    private int mColor = 0;

    public ColoredSpace(Context context) {
        super(context);
    }

    public ColoredSpace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ColoredSpace,
                0, 0);
        try {
            mColor = a.getInteger(R.styleable.ColoredSpace_backgroundColor, mColor);
        } finally {
            a.recycle();
        }
    }

    public ColoredSpace(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setColor(int color) {
        this.mColor = color;
        this.setBackgroundColor(mColor);
        invalidate();
        requestLayout();
    }

    public int getColor() {
        return mColor;
    }

}
