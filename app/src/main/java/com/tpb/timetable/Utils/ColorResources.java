package com.tpb.timetable.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created by theo on 30/05/16.
 */
public class ColorResources {
    private static final String TAG = "ColorResources";
    private static boolean darkTheme;
    private static Context mContext;
    private static ColorResources instance;
    private static ArrayList<Themable> mListeners;

    private static int primary;
    private static int primaryDark;
    private static int primaryLight;
    private static int accent;
    private static int iconsText;
    private static int primaryText;
    private static int primaryTextLight;
    private static int secondaryText;
    private static int secondaryTextLight;
    private static int background;
    private static int backgroundDark;
    private static int cardBackground;
    private static int cardBackgroundDark;
    private static int divider;
    private static int dividerDark;

    private ColorResources(Context context) {
        mContext = context;
    }

    public static ColorResources getColorResources(Context context, Themable listener) {
        if(instance == null) {
            instance = new ColorResources(context);
            SharedPreferences prefs = context.getSharedPreferences("colors", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if(!prefs.contains("colorPrimary")) writeDefaultValues(editor);
            final Resources res = mContext.getResources();
            darkTheme = prefs.getBoolean("darkTheme", false);
            primary = prefs.getInt("colorPrimary", res.getColor(R.color.colorPrimary));
            primaryDark = prefs.getInt("colorPrimaryDark", res.getColor(R.color.colorPrimaryDark));
            primaryLight = prefs.getInt("colorPrimaryLight", res.getColor(R.color.colorPrimaryLight));
            accent = prefs.getInt("colorAccent", res.getColor(R.color.colorAccent));
            primaryText = prefs.getInt("colorPrimaryText", res.getColor(R.color.colorPrimaryText));
            primaryTextLight = prefs.getInt("colorPrimaryTextLight", res.getColor(R.color.colorPrimaryTextLight));
            secondaryText = prefs.getInt("colorSecondaryText", res.getColor(R.color.colorSecondaryText));
            secondaryTextLight = prefs.getInt("colorSecondaryTextLight", res.getColor(R.color.colorSecondaryTextLight));
            iconsText = prefs.getInt("iconsText", res.getColor(R.color.colorIconsText));
            background = prefs.getInt("background", res.getColor(R.color.background));
            backgroundDark = prefs.getInt("backgroundDark", res.getColor(R.color.backgroundDark));
            cardBackground = prefs.getInt("card", res.getColor(R.color.card));
            cardBackgroundDark = prefs.getInt("cardDark", res.getColor(R.color.cardDark));
            divider = prefs.getInt("divider", res.getColor(R.color.divider));
            dividerDark = prefs.getInt("dividerDark", res.getColor(R.color.dividerDark));
            darkTheme = true;
            editor.apply();
        }
        if(listener != null) mListeners.add(listener);
        return instance;
    }

    private static void writeDefaultValues(SharedPreferences.Editor editor) {
        final Resources res = mContext.getResources();
        editor.putBoolean("darkTheme", false);
        editor.putInt("colorPrimary", res.getColor(R.color.colorPrimary));
        editor.putInt("colorPrimaryDark", res.getColor(R.color.colorPrimaryDark));
        editor.putInt("colorPrimaryLight", res.getColor(R.color.colorPrimaryLight));
        editor.putInt("colorAccent", res.getColor(R.color.colorAccent));
        editor.putInt("colorPrimaryText", res.getColor(R.color.colorPrimaryText));
        editor.putInt("colorPrimaryTextLight", res.getColor(R.color.colorPrimaryTextLight));
        editor.putInt("colorSecondaryText", res.getColor(R.color.colorSecondaryText));
        editor.putInt("colorSecondaryTextLight", res.getColor(R.color.colorSecondaryTextLight));
        editor.putInt("iconsText", res.getColor(R.color.colorIconsText));
        editor.putInt("background", res.getColor(R.color.background));
        editor.putInt("backgroundDark", res.getColor(R.color.backgroundDark));
        editor.putInt("card", res.getColor(R.color.card));
        editor.putInt("cardDark", res.getColor(R.color.cardDark));
        editor.putInt("divider", res.getColor(R.color.divider));
        editor.putInt("dividerDark", res.getColor(R.color.dividerDark));
        editor.commit();
    }

    public static void setPrimary(int primary) {
        ColorResources.primary = primary;
    }

    public static void setPrimaryDark(int primaryDark) {
        ColorResources.primaryDark = primaryDark;
    }

    public static void setPrimaryLight(int primaryLight) {
        ColorResources.primaryLight = primaryLight;
    }

    public static void setAccent(int accent) {
        ColorResources.accent = accent;
    }

    public static void setIconsText(int iconsText) {
        ColorResources.iconsText = iconsText;
    }

    public static void setPrimaryText(int primaryText) {
        ColorResources.primaryText = primaryText;
    }

    public static void setPrimaryTextLight(int primaryTextLight) {
        ColorResources.primaryTextLight = primaryTextLight;
    }

    public static void setSecondaryText(int secondaryText) {
        ColorResources.secondaryText = secondaryText;
    }

    public static void setSecondaryTextLight(int secondaryTextLight) {
        ColorResources.secondaryTextLight = secondaryTextLight;
    }

    public static void setBackground(int background) {
        ColorResources.background = background;
    }

    public static void setBackgroundDark(int backgroundDark) {
        ColorResources.backgroundDark = backgroundDark;
    }

    public static void setCardBackground(int cardBackground) {
        ColorResources.cardBackground = cardBackground;
    }

    public static void setCardBackgroundDark(int cardBackgroundDark) {
        ColorResources.cardBackgroundDark = cardBackgroundDark;
    }

    public static void setDarkTheme(boolean isDark) {
        darkTheme = isDark;
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    public static int getPrimary() {
        return primary;
    }

    public static int getPrimaryDark() {
        return primaryDark;
    }

    public static int getPrimaryLight() {
        return primaryLight;
    }

    public static int getAccent() {
        return accent;
    }

    public static int getIconsText() {
        return iconsText;
    }

    public static int getPrimaryText() {
        return darkTheme ? primaryTextLight : primaryText;
    }

    public static int getSecondaryText() {
        return darkTheme ? secondaryTextLight : secondaryText;
    }

    public static int getCardBackground() {
        return darkTheme ? cardBackgroundDark : cardBackground;
    }

    public static int getDivider() {
        return darkTheme ? dividerDark : divider;
    }

    public static int getBackground() {
        return darkTheme ? backgroundDark : background;
    }


    public static void theme(ViewGroup group) {
        final long start = System.nanoTime();
        final Stack<ViewGroup> viewStack = new Stack<>();
        viewStack.push(group);
        //Only the outer level has a background
        group.setBackgroundColor(getBackground());
        View v;
        while(!viewStack.isEmpty()) {
            group = viewStack.pop(); //The current ViewGroup
            for(int i = 0; i < group.getChildCount(); i++) { //All the views in the current Group
                v = group.getChildAt(i);
                Log.i(TAG, "View " + v.toString());
                //We don't want to do anything to any of these views
                if(v instanceof RelativeLayout ||
                        v instanceof LinearLayout ||
                        v instanceof RecyclerView ||
                        v instanceof CoordinatorLayout) {
                    //Unless they are a TextInputLayout, which needs different theming
                    if(v instanceof TextInputLayout) {
                        TextInputLayout t = (TextInputLayout) v;
                        try {
                            //Setting focused text color
                            final Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
                            final Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
                            fFocusedTextColor.setAccessible(true);
                            fDefaultTextColor.setAccessible(true);
                            fFocusedTextColor.set(t, ColorStateList.valueOf(primary));
                            fDefaultTextColor.set(t, ColorStateList.valueOf(getSecondaryText()));
                        } catch(Throwable ignored) {
                            Log.i(TAG, "Setting textinput layout " + ignored.toString());
                        }
                    }
                    Log.i(TAG, "Pushing view " + v.toString() + " to the stack");
                    //The ViewGroup is pushed to the stack so that it's views can be themed later
                    viewStack.push((ViewGroup) v);
                } else { //The view is to be themed
                    if(v instanceof CardView) {
                        //CardView background must be est, before theming its views
                        final CardView c = (CardView) v;
                        c.setCardBackgroundColor(getCardBackground());
                        viewStack.push(c);
                    } else if(v instanceof TextInputEditText || v instanceof EditText) {
                        //Both TextInputEditText and EditText are themed the same way
                        final EditText t = (EditText) v;
                        t.setTextColor(getPrimaryText());
                        //Setting color filter, which sets the bottom bar
                        t.getBackground().setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
                        t.setHighlightColor(accent);
                        try {
                            //Setting the cursor to the correct color
                            final Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                            fCursorDrawableRes.setAccessible(true);
                            final int mCursorDrawableRes = fCursorDrawableRes.getInt(t);
                            final Field fEditor = TextView.class.getDeclaredField("mEditor");
                            fEditor.setAccessible(true);
                            final Object editor = fEditor.get(t);
                            final Class<?> clazz = editor.getClass();
                            final Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
                            fCursorDrawable.setAccessible(true);
                            final Drawable[] drawables = new Drawable[5];
                            drawables[0] = ContextCompat.getDrawable(t.getContext(), mCursorDrawableRes);
                            drawables[1] = ContextCompat.getDrawable(t.getContext(), mCursorDrawableRes);
                            drawables[0].setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                            drawables[1].setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                            fCursorDrawable.set(editor, drawables);

                            //Setting up the selection handles to be the correct color
                            //TODO- Get the drawables only once?
                            final Field fHandleRes = TextView.class.getDeclaredField("mTextSelectHandleRes");
                            final Field fHandleLeftRes = TextView.class.getDeclaredField("mTextSelectHandleLeftRes");
                            final Field fHandleRightRes = TextView.class.getDeclaredField("mTextSelectHandleRightRes");
                            fHandleRes.setAccessible(true);
                            fHandleLeftRes.setAccessible(true);
                            fHandleRightRes.setAccessible(true);
                            final int centreHandleRes = fHandleRes.getInt(t);
                            final int leftHandleRes = fHandleLeftRes.getInt(t);
                            final int rightHandleRes = fHandleRightRes.getInt(t);
                            final Field fSelectHandleCenter = editor.getClass().getDeclaredField("mSelectHandleCenter");
                            final Field fSelectHandleLeft = editor.getClass().getDeclaredField("mSelectHandleLeft");
                            final Field fSelectHandleRight = editor.getClass().getDeclaredField("mSelectHandleRight");
                            fSelectHandleCenter.setAccessible(true);
                            fSelectHandleLeft.setAccessible(true);
                            fSelectHandleRight.setAccessible(true);
                            drawables[2] = ContextCompat.getDrawable(t.getContext(), centreHandleRes);
                            drawables[3] = ContextCompat.getDrawable(t.getContext(), leftHandleRes);
                            drawables[4] = ContextCompat.getDrawable(t.getContext(), rightHandleRes);
                            for(Drawable d : drawables) d.setColorFilter(accent, PorterDuff.Mode.SRC_IN);
                            fSelectHandleCenter.set(editor, drawables[2]);
                            fSelectHandleLeft.set(editor, drawables[3]);
                            fSelectHandleRight.set(editor, drawables[4]);
                        } catch(Throwable ignored) {
                            Log.e(TAG, ignored.toString());
                        }
                    } else if(v instanceof AppCompatCheckBox) {
                        final AppCompatCheckBox c = (AppCompatCheckBox) v;
                        c.setSupportButtonTintList(ColorStateList.valueOf(accent));
                        c.setHighlightColor(accent);
                        c.setTextColor(getPrimaryText());
                        //Getting a custom ripple for the checkbox
                        c.setBackgroundDrawable(getAdaptiveRippleDrawable(backgroundDark, accent));
                    } else if(v instanceof TextView) {
                        final TextView t = (TextView) v;
                        //Setting the text color based on whether or not the text is a title
                        if(t.getTextSize() >= mContext.getResources().getDimension(R.dimen.text_title_size)) {
                            t.setTextColor(getPrimaryText());
                        } else {
                            t.setTextColor(getSecondaryText());
                        }
                        t.setHintTextColor(getPrimaryText());
                        //This is for the fab sheet
                        if(t.getParent().getParent() instanceof CardView) {
                            t.setBackgroundColor(getCardBackground());
                       }
                    } else if(v instanceof ColoredSpace) {
                        Log.i(TAG, "Theming colored space");
                        ((ColoredSpace) v).setColor(getDivider());
                    }
                }
            }
        }
        Log.i(TAG, "Theming view took " + (System.nanoTime()-start)/1E9);
    }

    private static Drawable getAdaptiveRippleDrawable(int normalColor, int pressedColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor),
                    null, getRippleMask(normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }

    private static Drawable getRippleMask(int color) {
        final float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, 3);
        final RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        final ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    private static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        final StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{},
                new ColorDrawable(normalColor));
        return states;
    }

    private void themeViews() {
        for(Themable t : mListeners) {

        }
    }

}
