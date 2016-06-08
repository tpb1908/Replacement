package com.tpb.timetable.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpb.timetable.Home.Interfaces.Themable;
import com.tpb.timetable.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created by theo on 30/05/16.
 */
public class ThemeHelper {
    private static final String TAG = "ThemeHelper";
    private static boolean darkTheme;
    private static Context mContext;
    private static ThemeHelper instance;
    private static final ArrayList<Themable> mListeners = new ArrayList<>();
    private static SharedPreferences.Editor mEditor;

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

    private ThemeHelper(Context context) {
        mContext = context;
    }

    public static ThemeHelper getColorResources(Context context, Themable listener) {
        if(instance == null) {
            instance = new ThemeHelper(context);
            final SharedPreferences prefs = context.getSharedPreferences("colors", Context.MODE_PRIVATE);
            mEditor = prefs.edit();
            if(!prefs.contains("colorPrimary")) writeDefaultValues(mEditor);
            darkTheme = prefs.getBoolean("darkTheme", false);
            primary = prefs.getInt("colorPrimary", ContextCompat.getColor(mContext, R.color.colorPrimary));
            primaryDark = prefs.getInt("colorPrimaryDark", ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            primaryLight = prefs.getInt("colorPrimaryLight", ContextCompat.getColor(mContext, R.color.colorPrimaryLight));
            accent = prefs.getInt("colorAccent", ContextCompat.getColor(mContext, R.color.colorAccent));
            primaryText = prefs.getInt("colorPrimaryText", ContextCompat.getColor(mContext, R.color.colorPrimaryText));
            primaryTextLight = prefs.getInt("colorPrimaryTextLight", ContextCompat.getColor(mContext, R.color.colorPrimaryTextLight));
            secondaryText = prefs.getInt("colorSecondaryText", ContextCompat.getColor(mContext, R.color.colorSecondaryText));
            secondaryTextLight = prefs.getInt("colorSecondaryTextLight", ContextCompat.getColor(mContext, R.color.colorSecondaryTextLight));
            iconsText = prefs.getInt("iconsText", ContextCompat.getColor(mContext, R.color.colorIconsText));
            background = prefs.getInt("background", ContextCompat.getColor(mContext, R.color.background));
            backgroundDark = prefs.getInt("backgroundDark", ContextCompat.getColor(mContext, R.color.backgroundDark));
            cardBackground = prefs.getInt("card", ContextCompat.getColor(mContext, R.color.card));
            cardBackgroundDark = prefs.getInt("cardDark", ContextCompat.getColor(mContext, R.color.cardDark));
            divider = prefs.getInt("divider", ContextCompat.getColor(mContext, R.color.divider));
            dividerDark = prefs.getInt("dividerDark", ContextCompat.getColor(mContext, R.color.dividerDark));
            mEditor.apply();
        }
        if(listener != null) mListeners.add(listener);


        return instance;
    }

    public static void addListener(Themable t) {
        if(t != null) mListeners.add(t);
    }

    private static void writeDefaultValues(SharedPreferences.Editor editor) {
        editor.putBoolean("darkTheme", false);
        editor.putInt("colorPrimary", ContextCompat.getColor(mContext, R.color.colorPrimary));
        editor.putInt("colorPrimaryDark", ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        editor.putInt("colorPrimaryLight", ContextCompat.getColor(mContext, R.color.colorPrimaryLight));
        editor.putInt("colorAccent", ContextCompat.getColor(mContext, R.color.colorAccent));
        editor.putInt("colorPrimaryText", ContextCompat.getColor(mContext, R.color.colorPrimaryText));
        editor.putInt("colorPrimaryTextLight", ContextCompat.getColor(mContext, R.color.colorPrimaryTextLight));
        editor.putInt("colorSecondaryText", ContextCompat.getColor(mContext, R.color.colorSecondaryText));
        editor.putInt("colorSecondaryTextLight", ContextCompat.getColor(mContext, R.color.colorSecondaryTextLight));
        editor.putInt("iconsText", ContextCompat.getColor(mContext, R.color.colorIconsText));
        editor.putInt("background", ContextCompat.getColor(mContext, R.color.background));
        editor.putInt("backgroundDark", ContextCompat.getColor(mContext, R.color.backgroundDark));
        editor.putInt("card", ContextCompat.getColor(mContext, R.color.card));
        editor.putInt("cardDark", ContextCompat.getColor(mContext, R.color.cardDark));
        editor.putInt("divider", ContextCompat.getColor(mContext, R.color.divider));
        editor.putInt("dividerDark", ContextCompat.getColor(mContext, R.color.dividerDark));
        editor.commit();
    }

    public static void setPrimary(int primary) {
        ThemeHelper.primary = primary;
    }

    public static void setPrimaryDark(int primaryDark) {
        ThemeHelper.primaryDark = primaryDark;
    }

    public static void setPrimaryLight(int primaryLight) {
        ThemeHelper.primaryLight = primaryLight;
    }

    public static void setAccent(int accent) {
        ThemeHelper.accent = accent;
    }

    public static void setIconsText(int iconsText) {
        ThemeHelper.iconsText = iconsText;
    }

    public static void setPrimaryText(int primaryText) {
        ThemeHelper.primaryText = primaryText;
    }

    public static void setPrimaryTextLight(int primaryTextLight) {
        ThemeHelper.primaryTextLight = primaryTextLight;
    }

    public static void setSecondaryText(int secondaryText) {
        ThemeHelper.secondaryText = secondaryText;
    }

    public static void setSecondaryTextLight(int secondaryTextLight) {
        ThemeHelper.secondaryTextLight = secondaryTextLight;
    }

    public static void setBackground(int background) {
        ThemeHelper.background = background;
    }

    public static void setBackgroundDark(int backgroundDark) {
        ThemeHelper.backgroundDark = backgroundDark;
    }

    public static void setCardBackground(int cardBackground) {
        ThemeHelper.cardBackground = cardBackground;
    }

    public static void setCardBackgroundDark(int cardBackgroundDark) {
        ThemeHelper.cardBackgroundDark = cardBackgroundDark;
    }

    public static void setDarkTheme(boolean isDark) {
        darkTheme = isDark;
        mEditor.putBoolean("darkTheme", isDark);
        mEditor.commit();
        themeViews();
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


    private static void themeTextInputLayout(TextInputLayout t) {
        try {
            //Setting focused text color
            final Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            final Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fFocusedTextColor.setAccessible(true);
            fDefaultTextColor.setAccessible(true);
            fFocusedTextColor.set(t, ColorStateList.valueOf(primary));
            fDefaultTextColor.set(t, ColorStateList.valueOf(getSecondaryText()));
        } catch(Throwable ignored) {}
    }

    private static void themeCardView(CardView c) {
        c.setCardBackgroundColor(getCardBackground());
    }

    private static void themeEditText(EditText t) {
        t.setTextColor(getPrimaryText());
        //By setting the background color filter we set the color of the bottom bar
        t.getBackground().setColorFilter(accent, PorterDuff.Mode.SRC_ATOP);
        t.setHighlightColor(accent);
        try {
            //We set the cursor to the accent color
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

            //We set the selection handles to the accent color
            //TODO- Cache the drawables? Performance doesn't seem to be a problem
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
        } catch(Throwable ignored) {}
    }

    private static void themeCheckBox(AppCompatCheckBox c) {
        c.setSupportButtonTintList(ColorStateList.valueOf(accent));
        c.setHighlightColor(accent);
        c.setTextColor(getPrimaryText());
        //Getting a custom ripple for the checkbox
        c.setBackgroundDrawable(getAdaptiveRippleDrawable(backgroundDark, accent));
    }

    private static void themeTextView(TextView t) {
        /*
         *   We set the text color based on text size (Is it a title?),
         *   line count (Does the TextView contain the body?),
         *   and a special clause for detail text which changes
         */
        if(t.getTextSize() >= mContext.getResources().getDimension(R.dimen.text_title_size)
                || t.getLineCount() > 1
                || t.getId() == R.id.text_homework_detail) {
            t.setTextColor(getPrimaryText());
        } else {
            t.setTextColor(getSecondaryText());
        }
        t.setHintTextColor(getPrimaryText());
        /*
         * This clause is for the FAB sheet. If we don't set its background, the dark
         * theme won't show text. Also, this should never cause an exception. Every TextView
         * has a parent, and if the parent of that parent is null, then instanceof will
         * return false.
         */
        if(t.getParent().getParent() instanceof CardView) t.setBackgroundColor(getCardBackground());
    }

    public static void theme(ViewGroup group) {
        final long start = System.nanoTime();
        final Stack<ViewGroup> groupStack = new Stack<>();
        groupStack.push(group);
        //Only the outer level has a background
        group.setBackgroundColor(getBackground());
        View v;
        while(!groupStack.isEmpty()) {
            //We get the current ViewGroup and iterate through its children
            group = groupStack.pop();
            for(int i = 0; i < group.getChildCount(); i++) {
                v = group.getChildAt(i);
                //We don't want to do anything to any of these views
                if(v instanceof RelativeLayout ||
                        v instanceof LinearLayout ||
                        v instanceof RecyclerView ||
                        v instanceof CoordinatorLayout) {
                    //Unless they are a TextInputLayout, which needs different theming
                    if(v instanceof TextInputLayout) themeTextInputLayout((TextInputLayout) v);
                    //All of these view types contain other views
                    groupStack.push((ViewGroup) v);
                } else { //The view is singular, so we theme it
                    if(v instanceof CardView) {
                        //CardView is technically a layout
                        themeCardView((CardView) v);
                        groupStack.push((CardView)v);
                    } else if(v instanceof FrameLayout) {
                        //Later, for the SlidingPanel
                        //v.setBackgroundColor(getBackground());
                    } else if(v instanceof TextInputEditText || v instanceof EditText) {
                        //We can theme TextInputEditText in the same way as EditText
                        themeEditText((EditText) v);
                    } else if(v instanceof AppCompatCheckBox) {
                        themeCheckBox((AppCompatCheckBox) v);
                    } else if(v instanceof TextView) {
                        themeTextView((TextView) v);
                    } else if(v instanceof ColoredSpace) {
                        ((ColoredSpace) v).setColor(getDivider());
                    }
                }
            }
        }
        Log.i(TAG, "Theming  " + group.getClass().getName() + " took " + (System.nanoTime()-start)/1E9);
    }


    //http://stackoverflow.com/questions/27787870/how-to-use-rippledrawable-programmatically-in-code-not-xml-with-android-5-0-lo
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

    public static void themeViews() {
        final long start = System.nanoTime();
        for(Themable t : mListeners) {
            theme(t.getViewGroup());
        }
        final double secs =  (System.nanoTime()-start)/1E9;
        Toast.makeText(mContext,
                "Theming took " + secs,
                Toast.LENGTH_LONG).show();
    }

    //Adds arguments for SlidingActivity to start from a button
    public static void setExpandLocation(View v, Intent i) {
        if(v != null) {
            final int[] location = new int[2];
            v.getLocationInWindow(location);
            i.putExtra("leftOffset", location[0]);
            i.putExtra("topOffset", location[1]);
            i.putExtra("viewWidth", v.getWidth());
            i.putExtra("viewHeight", v.getHeight());
            i.putExtra("hasOpenPosition", true);
        } else {
            i.putExtra("hasOpenPosition", false);
        }
    }

    /** From and for Klinker sliding activity
     * Adjust the alpha of a color.
     * @param color the color [0x00000000, 0xffffffff]
     * @param factor the factor for the alpha [0,1]
     * @return the adjusted color
     */
    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}
